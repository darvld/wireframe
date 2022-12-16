# Wireframe

[![Maven Central](https://img.shields.io/maven-central/v/io.github.darvld.wireframe/runtime.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.darvld.wireframe%22%20AND%20a:%22runtime%22)

A GraphQL server library for Kotlin.

At the moment, only Ktor support with schema-first approach is implemented, support for other server-side
frameworks and workflows will be added later.

## Roadmap

The following table shows the current state of features for the Ktor runtime:

| Feature                               | Status             |
|---------------------------------------|--------------------|
| Generate type-safe resolvers          | Implemented ✅      |
| Routing integration                   | Implemented ✅      |
| Custom context builders               | Implemented ✅      |
| Subscriptions support                 | Not implemented 🚧 |
| Batch requests support                | Not implemented 🚧 |
| Support GraphQL interfaces and unions | Not implemented 🚧 |
| Custom type mappings                  | Implemented ✅      |

## More information

- [Quick start](docs/quick-start.md)
- [Sample project](sample/README.md)