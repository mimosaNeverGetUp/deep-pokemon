/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.config;

import com.mongodb.client.MongoClients;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

@Profile({"docker", "swarm"})
@Configuration
public class DockerApplicationConfiguration {
    private static final Logger log = LoggerFactory.getLogger(DockerApplicationConfiguration.class);
    private static final String SPRING_DATA_MONGODB_URI = "spring.data.mongodb.uri";
    private static final String SPRING_DATA_MONGODB_DATABASE = "spring.data.mongodb.database";
    private static final String SPRING_DATA_REDIS_HOST = "spring.data.redis.host";
    private static final String SPRING_DATA_REDIS_PORT = "spring.data.redis.port";
    private Properties applicationProperties;

    private void initPropertiesIfNeed(ConfigurableEnvironment environment) {
        if (applicationProperties == null) {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            if (Arrays.stream(environment.getActiveProfiles()).toList().contains("swarm")) {
                log.info("use swarm application properties");
                factory.setResources(new ClassPathResource("application-swarm.yml"));
            } else {
                factory.setResources(new ClassPathResource("application-docker.yml"));
            }
            applicationProperties = factory.getObject();
            if (applicationProperties == null) {
                log.warn("no application properties found");
                return;
            }
            environment.getPropertySources().addFirst(new PropertiesPropertySource("docker", applicationProperties));
        }
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(ConfigurableEnvironment environment) {
        initPropertiesIfNeed(environment);
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(applicationProperties.getProperty(SPRING_DATA_MONGODB_URI)),
                applicationProperties.getProperty(SPRING_DATA_MONGODB_DATABASE));
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(ConfigurableEnvironment environment) {
        initPropertiesIfNeed(environment);
        final SocketOptions socketOptions = SocketOptions.builder().connectTimeout(Duration.ofSeconds(3)).build();
        final ClientOptions clientOptions =
                ClientOptions.builder().socketOptions(socketOptions).build();
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(clientOptions).build();

        String host = applicationProperties.getProperty(SPRING_DATA_REDIS_HOST);
        int port = Integer.parseInt(applicationProperties.getProperty(SPRING_DATA_REDIS_PORT));
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port),
                lettuceClientConfiguration);
    }
}