package io.github.darvld.wireframe.sample.resolvers

import io.github.darvld.wireframe.resolvers.artist
import io.github.darvld.wireframe.resolvers.mutation
import io.github.darvld.wireframe.resolvers.query
import io.github.darvld.wireframe.routing.Resolvers
import io.github.darvld.wireframe.sample.plugins.log
import io.github.darvld.wireframe.schema.ArtistDto
import java.util.*

fun Resolvers.artistResolvers() {
    // We can break down resolvers into different
    // extensions according to their type
    artistQueries()
    artistMutations()
    artistFields()
}

private fun Resolvers.artistQueries() = query {
    // Queries are the basic entry point for our API
    artist { id ->
        // Field arguments are provided to the lambda
        log.info("Requested artist with id <$id>")

        // We can use the GraphQL context for this call
        // to provide values to nested resolvers
        context.put("origin", "`artist` query")

        // The response must match the schema type
        ArtistDto(id, "Bob")
    }
}

private fun Resolvers.artistMutations() = mutation {
    // Mutations are identical to queries, but by convention,
    // they result in changes to the data or the state of the app
    createArtist { artist ->
        // Complex input arguments are also type-safe
        log.info("Created artist: $artist")

        context.put("origin", "`createArtist` mutation")
        ArtistDto(UUID.randomUUID().toString(), artist.name, artist.bio)
    }
}

private fun Resolvers.artistFields() = artist {
    // We can add custom resolvers to all types
    bio { artist ->
        // Here we can retrieve the context value set in parent fields
        val origin = context.get<String>("origin")
        val role = context.get<String>("role")

        // Fields in non-route types (i.e. types other than Query,
        // Mutation, or Subscription) may access the value resolved for
        // their parent field as the first resolver argument
        "[$role] ${artist.name} doesn't have a bio. (data resolved from $origin)"
    }
}