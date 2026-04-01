# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Winter** is an educational lightweight IoC (Inversion of Control) container for Java, built to teach how Spring works internally. It uses a playful naming convention: "Snowflakes" instead of Beans, `@Snowflake` instead of `@Component`, `@Melt` instead of `@Autowired`.

## Build & Test Commands

```bash
./gradlew build       # Compile and package
./gradlew test        # Run tests (JUnit 5)
./gradlew clean       # Clean build artifacts
```

To run a single test class:
```bash
./gradlew test --tests "org.github.oleksandrkukotin.SomeTest"
```

## Architecture

### Core Framework (`org.github.oleksandrkukotin.core`)

- **`SimpleSnowflakeFactory`** — The main IoC container. Manages bean lifecycle, dependency resolution, and caching. Contains `singletonCache` map. After constructor injection, calls `injectMeltDependencies()` to handle field/setter injection. TODO stubs remain for `@Lazy` and circular dependency detection.
- **`SnowflakeScanner`** — Classpath scanner using ClassGraph library to discover `@Snowflake`-annotated classes within a given base package.
- **`SnowflakeDefinition`** — Metadata record for a bean (class, name, scope). Analogous to Spring's `BeanDefinition`.
- **`Scope`** — Enum with `SINGLETON` and `PROTOTYPE`.

### Annotations (`org.github.oleksandrkukotin.core.annotation`)

| Annotation | Spring Equivalent | Status |
|---|---|---|
| `@Snowflake` | `@Component` | Implemented |
| `@Melt` | `@Autowired` | Implemented (field and setter injection) |
| `@Qualifier` | `@Qualifier` | Implemented (constructor params, fields, setters) |
| `@Lazy` | `@Lazy` | Defined, TODO in factory |

### Dependency Flow

```
SimpleSnowflakeFactory
  ├── uses SnowflakeScanner to discover @Snowflake classes
  ├── builds SnowflakeDefinition registry (Map<String, SnowflakeDefinition>)
  └── resolves dependencies via constructor parameter types
```

`Winter.java` is the demo entry point — calls `scan()` then `getSnowflake()` to wire the example service graph.

### Example Services (`org.github.oleksandrkukotin.service`)

`MessageService` (interface) ← `EmailService` / `SmsService` (both `@Snowflake`)

Three services demonstrate all three injection modes, each disambiguating `MessageService` with `@Qualifier("SmsService")`:
- `UserService` — constructor injection
- `FieldInjectedService` — field injection (`@Melt @Qualifier` on field)
- `SetterInjectedService` — setter injection (`@Melt @Qualifier` on setter method)

## Development Roadmap

The project is organized into stages (see README.md for full detail):

- **Stage 1 (complete):** `@Snowflake`, classpath scanning, constructor injection, scopes, `SnowflakeDefinition`
- **Stage 2 (in progress):** Field/setter injection (`@Melt`) ✓, `@Qualifier` ✓, `@Lazy` (annotation defined, factory not implemented), circular dependency detection (`CircularDependencyException` exists but is not thrown)
- **Stage 3:** Lifecycle hooks (`@Freeze`/`@Thaw`), ordered initialization, prototype scope with injection
- **Stage 4:** `@IceBlock` configuration classes, properties injection (`@Frost`), environment profiles
- **Stage 5:** AOP proxies, event system, custom scopes

## Key Implementation Notes

- Dependency resolution uses simple class name (not fully-qualified) as the key in the bean registry.
- Constructor injection picks the constructor with the **most parameters** (greedy strategy) — `@Melt` on constructors is not checked.
- `resolveByType(Class<?>, AnnotatedElement)` handles all three injection modes: checks `@Qualifier` on the `AnnotatedElement` first (by name lookup in the registry), then falls back to assignability-based scan. Throws with actionable messages on zero or multiple matches.
- `injectMeltDependencies()` runs after constructor injection: scans fields for `@Melt` (sets accessible, injects via reflection), then scans methods for `@Melt` (validates single-parameter setter, invokes via reflection). Both respect `@Qualifier`.
- `CircularDependencyException` exists in the exception package but is not yet thrown — it will be used in Stage 2.
- There are currently no tests; the test source directory is empty.
- ClassGraph is the only non-test runtime dependency.
