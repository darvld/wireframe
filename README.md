# Wireframe

[![Maven Central](https://img.shields.io/maven-central/v/io.github.darvld.wireframe/runtime.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.darvld.wireframe%22%20AND%20a:%22runtime%22)

> Wireframe is currently in a proof-of-concept stage. It should not be used in production environments. Feel free to try
> it out while the API gets polished and new features are added, but be aware that breaking changes are likely to occur.

Wireframe is a GraphQL library for Kotlin, built on top of [GraphQL-Java](https://github.com/graphql-java/graphql-java).
It provides support for coroutines and integrates with some popular web frameworks and serialization libraries.

## Features

| Feature                               | Status             |
|---------------------------------------|--------------------|
| Generate type-safe resolvers          | Implemented ✅      |
| Routing integration                   | Implemented ✅      |
| Custom context builders               | Implemented ✅      |
| Subscriptions support                 | Not implemented 🚧 |
| Batch requests support                | Not implemented 🚧 |
| Support GraphQL interfaces and unions | Not implemented 🚧 |
| Custom type mappings                  | Implemented ✅      |

# Quick start

This guide explains how to install and setup your GraphQL application using a Ktor server backend. The full code can be
found in the [sample project](https://github.com/darvld/wireframe/blob/main/sample/README).

## Installation

Apply the gradle plugin on your project's build script:

```kotlin
// build.gradle.kts
id("io.github.darvld.wireframe")
```

Add runtime dependencies:

```kotlin
// build.gradle.kts

// Ktor integration
implementation("io.github.darvld.wireframe:runtime-ktor:<version>")

// Serialization provider
implementation("io.github.darvld.wireframe:transport-kotlinx:<version>")
```

## Usage

Suppose you have the following GraphQL schema:

```graphql
type Query {
    artist(id: ID!): Artist
}

type Artist {
    id: ID!
    name: String!
    bio: String
}
```

We can point the code generator to our SDL files using the Gradle plugin:

```kotlin
// build.gradle.kts
wiring {
    sourcesRoot.set("src/main/graphql")
}
```

Then we can use the `generateWiring` task to generate the code for our resolvers.

```commandline
gradle generateWiring
```

The processor will generate a type-safe DSL that we can use to define our GraphQL API:

```kotlin
// AccountResolvers.kt

fun Resolvers.accountResolvers() = query {
    artist { id -> ArtistDto(id, "Bob") }
}
```

# Integrations

[TBD]

# Features

[TBD]

## Context plugins

[TBD]

## Custom mappings

[TBD]