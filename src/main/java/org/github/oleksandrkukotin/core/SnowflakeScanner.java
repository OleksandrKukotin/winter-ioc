package org.github.oleksandrkukotin.core;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.github.oleksandrkukotin.core.annotation.Snowflake;

public class SnowflakeScanner {

    private final ClassGraph classGraph = new ClassGraph().enableAllInfo();

    public SnowflakeScanner() {
        classGraph.addClassLoader(SnowflakeScanner.class.getClassLoader());
    }

    public Class<?>[] getClasses(String packageName) {
        try (ScanResult scanResult = classGraph.acceptPackages(packageName).scan()) {
            return scanResult.getClassesWithAnnotation(Snowflake.class.getName())
                    .loadClasses()
                    .toArray(new Class<?>[0]);
        }
    }
}
