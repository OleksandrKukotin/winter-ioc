package org.github.oleksandrkukotin.core;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.github.oleksandrkukotin.core.annotation.Snowflake;

import java.util.List;
import java.util.logging.Logger;

public class SnowflakeScanner {

    private static final Logger logger = Logger.getLogger(SnowflakeScanner.class.getName());

    private final ClassGraph classGraph = new ClassGraph().enableAllInfo();

    public SnowflakeScanner() {
        classGraph.addClassLoader(SnowflakeScanner.class.getClassLoader());
    }

    public Class<?>[] getClasses(String packageName) {
        logger.fine("Starting classpath scan in package: " + packageName);
        try (ScanResult scanResult = classGraph.acceptPackages(packageName).scan()) {
            List<Class<?>> classes = scanResult.getClassesWithAnnotation(Snowflake.class.getName())
                    .loadClasses();
            classes.forEach(clazz -> logger.fine("Discovered @Snowflake: " + clazz.getName()));
            return classes.toArray(new Class<?>[0]);
        }
    }
}
