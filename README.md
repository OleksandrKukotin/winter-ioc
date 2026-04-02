# Winter ❄️

A tiny dependency injection container for Java.
Built for learning how Spring works under the hood.

Concepts:
- Snowflakes instead of Beans
- Minimal API
- No magic
- Readable source code

Goal: understand IoC, not compete with Spring

---

## Roadmap — Challenges & Quests

A progressive list of features to implement. Each one teaches a real concept from Spring internals.

### Stage 1 — Foundations
- [x] `@Snowflake` annotation for component marking
- [x] Classpath scanning with ClassGraph
- [x] Constructor-based dependency injection
- [x] Singleton and Prototype scope support
- [x] `SnowflakeDefinition` as bean metadata holder

### Stage 2 — Container Improvements
- [x] **Field injection** — resolve dependencies annotated with `@Melt` (like `@Autowired`) directly on fields
- [x] **Setter injection** — support setter methods as an injection point
- [x] **Qualifier support** — allow choosing between multiple implementations of the same type
- [x] **Circular dependency detection** — detect and report cycles instead of crashing with a stack overflow
- [ ] **Lazy initialization** — instantiate beans only when first requested, not at scan time

### Stage 3 — Lifecycle
- [ ] **`@Freeze` / `@Thaw` lifecycle hooks** — run logic after construction and before destruction (like `@PostConstruct` / `@PreDestroy`)
- [ ] **Ordered initialization** — control the order beans are created when one depends on another non-injected bean
- [ ] **Prototype scope with injection** — handle the case where a singleton depends on a prototype-scoped bean

### Stage 4 — Configuration
- [ ] **`@IceBlock` configuration class** — define beans via methods in a config class (like `@Configuration` + `@Bean`)
- [ ] **Properties injection** — load values from a `.properties` file and inject them with `@Frost("key")`
- [ ] **Profiles** — activate different bean sets based on an environment profile

### Stage 5 — Advanced
- [ ] **AOP proxy support** — wrap beans with dynamic proxies to apply cross-cutting concerns (logging, transactions)
- [ ] **Event system** — publish and listen to application events between beans
- [ ] **Custom scopes** — allow registering new scopes beyond Singleton and Prototype
