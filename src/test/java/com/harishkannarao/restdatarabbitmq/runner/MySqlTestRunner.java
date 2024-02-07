package com.harishkannarao.restdatarabbitmq.runner;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

public class MySqlTestRunner {

    private static final int PORT = 3306;
    private static final String DATABASE = "test-database";
    private static final String USERNAME = "test-user";
    private static final String PASSWORD = "test-password";
    private static final GenericContainer<?> CONTAINER = new GenericContainer<>(DockerImageName.parse("mysql:8-debian"))
            .withExposedPorts(PORT)
            .withEnv("MYSQL_ROOT_PASSWORD", "test-root")
            .withEnv("MYSQL_DATABASE", DATABASE)
            .withEnv("MYSQL_USER", USERNAME)
            .withEnv("MYSQL_PASSWORD", PASSWORD);

    public static void start(boolean useFixedPorts) {
        if (useFixedPorts) {
            CONTAINER.setPortBindings(List.of(PORT + ":" + PORT));
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
        CONTAINER.close();
    }

    public static String getHost() {
        return CONTAINER.getHost();
    }

    public static Integer getPort() {
        return CONTAINER.getMappedPort(PORT);
    }

    public static String getDatabase() {
        return DATABASE;
    }
    public static String getUsername() {
        return USERNAME;
    }

    public static String getPassword() {
        return PASSWORD;
    }

    public static String getJdbcUrl() {
        return String.format("jdbc:mysql://%s:%s/%s", getHost(), getPort(), getDatabase());
    }
}
