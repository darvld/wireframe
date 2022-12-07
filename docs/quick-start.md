# Quick start

This guide explains how to install and setup your GraphQL application using a Ktor server backend. The full code can be
found in the [sample project](../sample/README.md).

## Installation

Apply the gradle plugin on your project's build script:

```kotlin
// build.gradle.kts
id("io.github.darvld.artemis") version "0.5.0"
```

Add a runtime dependency:

```kotlin
// build.gradle.kts
implementation("io.github.darvld.artemis:plugin-ktor:0.5.0")
```

## Usage

Suppose you have the following GraphQL schema:

```graphql
# my-project/graphql/schema.graphql

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
    sourcesRoot.set("my-project/graphql")
}
```

Then we can use the `generateWiring` task to generate the code for our resolvers.

```commandline
gradle generateWiring
```

The processor will generate a type-safe DSL that we can use with the Ktor runtime to define our GraphQL API:

```kotlin
// Routing.kt

fun Application.configureGraphQL() {
    // Required to send and receive JSON data
    install(ContentNegotiation) { json() }

    routing {
        graphQL {
            // Set the GraphQL schema definition
            sdl(loadSchema())

            // We can create our own context for the call based on the original HTTP request
            buildContext { context ->
                // Now "role" will be available to all resolvers
                // You can use this step to decode authorization headers, etc.
                context.put("role", "User")
            }

            // Like Ktor routes, we can define our resolvers as extensions and extract
            // them to a separate file, they will be merged to create the wiring
            resolvers {
                query {
                    artist { id ->
                        // fetch our value and return it
                    }
                }

                // Add other resolvers
                // ...
            }
        }

        // Configure other routes for your application
        // ...
    }
}

```

Check out the [sample project](../sample/README.md) to see the full code and how to provide an API explorer with your
server using GraphiQL.