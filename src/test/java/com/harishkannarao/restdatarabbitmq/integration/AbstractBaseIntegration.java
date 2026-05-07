package com.harishkannarao.restdatarabbitmq.integration;

import com.harishkannarao.restdatarabbitmq.RestDataRabbitmqApplication;
import com.harishkannarao.restdatarabbitmq.config.MockitoMockHolder;
import com.harishkannarao.restdatarabbitmq.config.RabbitMqConfiguration;
import com.harishkannarao.restdatarabbitmq.runner.MySqlTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.RabbitMqTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.SpringBootTestRunner;
import com.harishkannarao.restdatarabbitmq.runner.SpringSettings;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractBaseIntegration {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractBaseIntegration.class);

    protected Set<Class<?>> additionalConfigurations() {
        return Collections.emptySet();
    }

    protected Properties additionalProperties() {
        return new Properties();
    }

    protected <T> T getBean(Class<T> clazz) {
        return SpringBootTestRunner.getBean(clazz);
    }

    protected String getProperty(String key) {
        return getBean(Environment.class).getProperty(key);
    }

    @BeforeEach
    public void applicationBootStrap() {
        startDependencies();

        final Properties properties = createProperties();
        properties.putAll(additionalProperties());
        Set<Class<?>> defaultSources = Set.of(
                RestDataRabbitmqApplication.class,
                RabbitMqConfiguration.class);
        Set<Class<?>> finalSources = Stream.concat(
                        defaultSources.stream(),
                        additionalConfigurations().stream())
                .collect(Collectors.toUnmodifiableSet());
        final SpringSettings settings = new SpringSettings(finalSources, properties);
        SpringBootTestRunner.bootStrap(settings);

        RabbitAdmin rabbitAdmin = SpringBootTestRunner.getBean(RabbitAdmin.class);
        rabbitAdmin.purgeQueue("test-queue-1", false);
        rabbitAdmin.purgeQueue("test-queue-2", false);
        resetMocks();
    }

    private void resetMocks() {
        Map<String, MockitoMockHolder> mockBeans = SpringBootTestRunner.getBeansOfType(MockitoMockHolder.class);
        mockBeans.forEach((mockName, mockitoMockHolder) -> {
            LOGGER.info("Resetting mock: {}", mockName);
            Mockito.reset(mockitoMockHolder.mock());
        });
    }

    private static Properties createProperties() {
        final Properties properties = new Properties();
        properties.setProperty("server.port", "0");
        properties.setProperty("spring.profiles.active", "int-test");
        properties.setProperty("test.store-received-messages", "false");

        properties.setProperty("spring.datasource.url", MySqlTestRunner.getJdbcUrl());
        properties.setProperty("spring.datasource.username", MySqlTestRunner.getUsername());
        properties.setProperty("spring.datasource.password", MySqlTestRunner.getPassword());
        properties.setProperty("spring.flyway.url", MySqlTestRunner.getJdbcUrl());
        properties.setProperty("spring.flyway.user", MySqlTestRunner.getUsername());
        properties.setProperty("spring.flyway.password", MySqlTestRunner.getPassword());

        properties.setProperty("spring.rabbitmq.host", RabbitMqTestRunner.getHost());
        properties.setProperty("spring.rabbitmq.port", RabbitMqTestRunner.getPort().toString());
        properties.setProperty("spring.rabbitmq.username", RabbitMqTestRunner.getUsername());
        properties.setProperty("spring.rabbitmq.password", RabbitMqTestRunner.getPassword());
        return properties;
    }

    private static void startDependencies() {
        Supplier<Boolean> mySqlStarter = () -> {
            if (!MySqlTestRunner.isRunning()) {
                MySqlTestRunner.start(true);
            }
            return true;
        };
        Supplier<Boolean> rabbitMqStarter = () -> {
            if (!RabbitMqTestRunner.isRunning()) {
                RabbitMqTestRunner.start(true);
            }
            return true;
        };
        Stream.of(mySqlStarter, rabbitMqStarter)
                .parallel()
                .map(Supplier::get)
                .forEach(_ -> {
                });
    }
}
