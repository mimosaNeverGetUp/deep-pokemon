/*
 * The MIT License
 *
 * Copyright (c) [2023]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mimosa.deeppokemon.config;

import com.github.dockerjava.api.command.InspectContainerResponse;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
public class MongodbTestConfig {
    @Bean
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer() {
        return new StandAloneMongoDBContainer("mongo:latest").withExposedPorts(27017)
                .withExposedPorts(27017)
                .withCopyFileToContainer(MountableFile.forClasspathResource("db/"), "/docker-entrypoint-initdb.d")
                .waitingFor(Wait.forLogMessage("(?i).*waiting for connections.*", 2))
                .withStartupTimeout(Duration.ofSeconds(10))
                .withReuse(false);
    }

    public static class StandAloneMongoDBContainer extends MongoDBContainer {
        public StandAloneMongoDBContainer(String dockerImageName) {
            super(dockerImageName);
            this.setCommand("mongod");
        }

        @Override
        protected void containerIsStarted(InspectContainerResponse containerInfo, boolean reused) {
            // not to init replica set
        }
    }
}