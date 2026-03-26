package org.github.oleksandrkukotin.core.exception;

/**
 * Thrown when the container detects a circular dependency chain during bean creation.
 *
 * Example cycle: A → B → C → A
 *   When creating A, the container starts creating B, which needs C,
 *   which needs A again — but A is still being constructed. Without detection,
 *   this causes infinite recursion and a StackOverflowError.
 *
 * Detection approach (using a "currently creating" set):
 *   1. Keep a Set<String> creationStack (beans currently being instantiated)
 *   2. In createInstance(), before creating a bean:
 *        - If its name is already in the set → throw CircularDependencyException
 *        - Otherwise add it to the set
 *   3. After the instance is fully created, remove it from the set
 *
 * The exception message should include the full cycle path so it's easy to diagnose,
 * e.g. "Circular dependency detected: A → B → C → A"
 */
public class CircularDependencyException extends RuntimeException {

    public CircularDependencyException(String message) {
        super(message);
    }
}
