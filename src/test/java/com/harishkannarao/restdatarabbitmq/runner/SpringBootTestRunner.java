package com.harishkannarao.restdatarabbitmq.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.core.env.Environment;

import java.util.Optional;

public class SpringBootTestRunner {
    private static ConfigurableApplicationContext context;
    private static SpringSettings springSettings;

    public static void stop() {
        if (isRunning()) {
            SpringApplication.exit(context);
        }
    }

    public static void start(SpringSettings settings) {
        String[] args = settings.properties().entrySet().stream()
                .map(entry -> String.format("--%s=%s", entry.getKey(), entry.getValue()))
                .toArray(String[]::new);
        context = SpringApplication.run(settings.sources().toArray(Class<?>[]::new), args);
        springSettings = settings;
    }

    public static void restart(SpringSettings settings) {
        stop();
        start(settings);
    }

    public static void bootStrap(SpringSettings settings) {
        if (!SpringBootTestRunner.isRunning()) {
            SpringBootTestRunner.start(settings);
        } else if (!settings.equals(SpringBootTestRunner.getSettings())) {
            SpringBootTestRunner.restart(settings);
        }
    }

    public static boolean isRunning() {
        return Optional.ofNullable(context)
                .map(Lifecycle::isRunning)
                .orElse(false);
    }

    public static SpringSettings getSettings() {
        return springSettings;
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static String getPort() {
        return getBean(Environment.class).getProperty("local.server.port");
    }

    public static String getApplicationUrl() {
        return String.format("http://localhost:%s", getPort());
    }
}
