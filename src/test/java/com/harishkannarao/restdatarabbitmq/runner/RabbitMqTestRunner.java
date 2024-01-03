package com.harishkannarao.restdatarabbitmq.runner;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

public class RabbitMqTestRunner {

    private static final int PORT = 5672;
    private static final int MANAGEMENT_PORT = 15672;
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";
    private static final GenericContainer CONTAINER = new GenericContainer(DockerImageName.parse("rabbitmq:3-management-alpine"))
            .withExposedPorts(PORT, MANAGEMENT_PORT)
            .withEnv("RABBITMQ_DEFAULT_USER", USERNAME)
            .withEnv("RABBITMQ_DEFAULT_PASS", PASSWORD);

    public static void start(boolean useFixedPorts) {
        if (useFixedPorts) {
            CONTAINER.setPortBindings(List.of(PORT + ":" + PORT, MANAGEMENT_PORT + ":" + MANAGEMENT_PORT));
        } else {
            CONTAINER.setPortBindings(List.of());
        }
        CONTAINER.start();
    }

    public static boolean isRunning() {
        return CONTAINER.isRunning();
    }

    public static void stop() {
        CONTAINER.stop();
    }

    public static String getHost() {
        return CONTAINER.getHost();
    }

    public static Integer getPort() {
        return CONTAINER.getMappedPort(PORT);
    }

    public static Integer getManagementPort() {
        return CONTAINER.getMappedPort(MANAGEMENT_PORT);
    }

    public static String getUsername() {
        return USERNAME;
    }

    public static String getPassword() {
        return PASSWORD;
    }

}
