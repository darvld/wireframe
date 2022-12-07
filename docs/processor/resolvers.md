# Resolvers plugin

This plugin generates a DSL for resolvers based on the GraphQL schema.

## How it works

For every output type, a DSL builder is created as an extension on `Resolvers`, allowing
to define the resolvers for each field in the type. For example, if we have the following SDL:

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

The plugin will generate a DSL that can be used like this:

```kotlin
fun Resolvers.artistResolvers() {
    query {
        // Create the wiring for your resolvers here
        artist { id -> ArtistDAO.findById(id) }
    }

    artist {
        id { /*customize the resolver for the id field*/  }
    }
}
```