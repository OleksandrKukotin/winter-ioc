package org.github.oleksandrkukotin.core;

import io.github.classgraph.ClassGraph;

public class SnowflakeScanner {

    private final ClassGraph classGraph = new ClassGraph().enableAllInfo();

    public SnowflakeScanner() {
        classGraph.addClassLoader(SnowflakeScanner.class.getClassLoader());
    }

    public Class<?>[] getClasses(String packageName) {
        // to implement
        return new Class<?>[]{};
    }
}
