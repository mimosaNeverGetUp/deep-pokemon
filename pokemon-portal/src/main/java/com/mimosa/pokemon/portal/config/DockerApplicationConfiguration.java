/*
 *  MIT License
 *
 *  Copyright (c) 2024-2024 mimosa
 */

package com.mimosa.pokemon.portal.config;

import com.mongodb.client.MongoClients;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Profile("docker")
@Configuration
public class DockerApplicationConfiguration {
    private static final String SPRING_DATA_MONGODB_URI = "spring.data.mongodb.uri";
    private static final String SPRING_DATA_MONGODB_DATABASE = "spring.data.mongodb.database";
    private static final String SPRING_DATA_REDIS_HOST = "spring.data.redis.host";
    private static final String SPRING_DATA_REDIS_PORT = "spring.data.redis.port";
    private static final String EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE = "eureka.client.serviceUrl.defaultZone";
    private Properties applicationProperties;

    private void initPropertiesIfNeed() {
        if (applicationProperties == null) {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(new ClassPathResource("application-docker.yml"));
            applicationProperties = factory.getObject();
        }
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        initPropertiesIfNeed();
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(applicationProperties.getProperty(SPRING_DATA_MONGODB_URI)),
                applicationProperties.getProperty(SPRING_DATA_MONGODB_DATABASE));
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        initPropertiesIfNeed();
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

    @Bean
    public EurekaClientConfigBean eurekaClientConfigBean() {
        EurekaClientConfigBean eurekaClientConfigBean = new EurekaClientConfigBean();
        eurekaClientConfigBean.setRegisterWithEureka(true);
        eurekaClientConfigBean.setServiceUrl(Collections.singletonMap(EurekaClientConfigBean.DEFAULT_ZONE,
                applicationProperties.getProperty(EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE)));
        return eurekaClientConfigBean;
    }

    public static class YamlPropertySourceFactory implements PropertySourceFactory {

        @Override
        public org.springframework.core.env.PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(encodedResource.getResource());

            Properties properties = factory.getObject();

            return new PropertiesPropertySource(encodedResource.getResource().getFilename(), properties);
        }
    }
}