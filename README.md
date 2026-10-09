# `@Uses` with the default production bundle

Reproducer for: `@Uses(Icon.class)` has no effect when the app runs with the
pre-compiled default production bundle. Vaadin 25.3.1, Spring Boot 4.1.1.

All views show the same Grid. Its icons exist only in a `LitRenderer`
template (`vaadin:phone` and `lumo:angle-up`), so there is no Java
reference to `Icon` except the `@Uses` annotation.

| Route        | Eager | `@Uses(Icon.class)` | Other                     |
|--------------|-------|---------------------|---------------------------|
| `/`          | yes   | yes                 |                           |
| `/lazy`      | no    | yes                 |                           |
| `/lazy-lumo` | no    | yes                 | references `LumoIcon.class` |
| `/with-icon` | no    | no                  | attaches a real `Icon`    |

## Run

```bash
# Default bundle ("A production mode bundle build is not needed")
mvn clean package -DskipTests
java -jar target/uses-default-bundle-1.0-SNAPSHOT.jar --server.port=8091

# App-specific bundle
mvn clean package -DskipTests -DforceProductionBuild=true
java -jar target/uses-default-bundle-1.0-SNAPSHOT.jar --server.port=8091
rm -rf src/main/bundles   # otherwise the next default build reuses it
```

Check the icons in headless Chrome (each URL in a fresh browser):

```bash
node check-icons.mjs http://localhost:8091/ http://localhost:8091/lazy \
  http://localhost:8091/lazy-lumo http://localhost:8091/with-icon
```

## Results (2026-10-09)

`vaadin` = `vaadin:phone` rendered, `lumo` = `lumo:angle-up` rendered.

| Route        | Default bundle          | App-specific bundle     |
|--------------|-------------------------|-------------------------|
| `/`          | vaadin: no, lumo: no    | vaadin: yes, lumo: no   |
| `/lazy`      | vaadin: no, lumo: no    | vaadin: yes, lumo: no   |
| `/lazy-lumo` | vaadin: no, lumo: no    | vaadin: yes, lumo: yes  |
| `/with-icon` | vaadin: yes, lumo: yes  | vaadin: yes, lumo: no   |

With the default bundle, `/with-icon` loads chunk `chunk-c8efc5a6...`, the
chunk triggered by `com.vaadin.flow.component.icon.Icon`. It holds both
iconsets. No other route loads it.

### Development mode

`mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8092` uses the
default dev bundle ("A development mode bundle build is not needed"). All
icons render on all four routes. The dev bundle has no lazy chunks: its
`loadOnDemand` only returns `Promise.resolve(0)`, and all modules load at
startup.
