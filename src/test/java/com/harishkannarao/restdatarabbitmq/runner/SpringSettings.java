package com.harishkannarao.restdatarabbitmq.runner;

import java.util.Properties;
import java.util.Set;

public record SpringSettings(
        Set<Class<?>> sources,
        Properties properties
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpringSettings(Set<Class<?>> sources1, Properties properties1))) return false;
        return java.util.Objects.equals(sources, sources1) &&
                java.util.Objects.equals(properties, properties1);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(sources, properties);
    }
}
