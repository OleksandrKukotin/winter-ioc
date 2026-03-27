Do we n# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Winter** is an educational lightweight IoC (Inversion of Control) container for Java, built to teach how Spring works internally. It uses a playful naming convention: "Snowflakes" instead of Beans, `@Snowflake` instead of `@Component`, `@Melt` instead of `@Autowired`.

## Build & Test Commands

```bash
./gradlew build       # Compile and package
./gradlew test        # Run tests (JUnit 5)
./gradlew clean       # Clean build artifacts
./gradlew run         # Run the main application
```

To run a single test class:
```bash
./gradlew test --tests "org.github.oleksandrkukotin.SomeTest"
```

## Architecture

### Core Framework (`org.github.oleksandrkukotin.core`)

- **`SimpleSnowflakeFactory`** — The main IoC container. Manages bean lifecycle, dependency resolution, and caching. Contains `singletonCache` map and TODO stubs for planned features (circular dependency detection, `@Qualifier`, `@Lazy`).
- **`SnowflakeScanner`** — Classpath scanner using ClassGraph library to discover `@Snowflake`-annotated classes within a given base package.
- **`SnowflakeDefinition`** — Metadata record for a bean (class, name, scope). Analogous to Spring's `BeanDefinition`.
- **`Scope`** — Enum with `SINGLETON` and `PROTOTYPE`.

### Annotations (`org.github.oleksandrkukotin.core.annotation`)

| Annotation | Spring Equivalent | Status |
|---|---|---|
| `@Snowflake` | `@Component` | Implemented |
| `@Melt` | `@Autowired` | Defined, injection not yet wired |
| `@Qualifier` | `@Qualifier` | Defined, TODO in factory |
| `@Lazy` | `@Lazy` | Defined, TODO in factory |

### Dependency Flow

```
SimpleSnowflakeFactory
  ├── uses SnowflakeScanner to discover @Snowflake classes
  ├── builds SnowflakeDefinition registry (Map<String, SnowflakeDefinition>)
  └── resolves dependencies via constructor parameter types
```

### Example Services (`org.github.oleksandrkukotin.service`)

`UserService` → `MessageService` (interface) ← `EmailService` / `SmsService`
These demonstrate the `@Qualifier` use case (multiple implementations of one interface).

## Development Roadmap

The project is organized into stages (see README.md for full detail):

- **Stage 1 (complete):** `@Snowflake`, classpath scanning, constructor injection, scopes, `SnowflakeDefinition`
- **Stage 2:** Field/setter injection (`@Melt`), `@Qualifier`, `@Lazy`, circular dependency detection
- **Stage 3:** Lifecycle hooks (`@PostConstruct`, `@PreDestroy`), `@SnowflakeConfig`, `@Blizzard` (factory methods)
- **Stage 4:** Properties support, environment profiles
- **Stage 5:** AOP proxies, event system, custom scopes

## Key Implementation Notes

- Dependency resolution currently uses simple class name (not fully-qualified) as the key in the bean registry.
- `CircularDependencyException` exists in the exception package but is not yet thrown — it will be used in Stage 2.
- `@Qualifier` and `@Lazy` handling have explicit TODO comments inside `SimpleSnowflakeFactory` marking where the logic should be added.
- ClassGraph is the only non-test runtime dependency.
