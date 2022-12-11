package io.github.darvld.wireframe.ktor

import graphql.GraphQL
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import io.ktor.server.routing.*

internal fun buildGraph(route: Route, path: String, config: GraphQLConfig): GraphQL {
    if (config.resolvers == null) route.application.environment.log.warn(
        "No resolvers were defined for the graph at $path. All fields will return null." +
            " Make sure you are calling the `resolvers` function in the endpoint configuration."
    )

    if (config.sdl.isBlank()) error(
        "A non-blank SDL was used for endpoint $path. Check your endpoint configuration" +
            "and ensure you are calling `sdl` to define the GraphQL schema."
    )

    val registry: TypeDefinitionRegistry = SchemaParser().parse(config.sdl)
    val wiring: RuntimeWiring = RuntimeWiring.newRuntimeWiring()
        .wiringFactory(KtorResolvers().apply(config.resolvers ?: { /*empty*/ }).buildWiringFactory())
        .build()

    val schema: GraphQLSchema = SchemaGenerator().makeExecutableSchema(registry, wiring)
    return GraphQL.newGraphQL(schema)
        .defaultDataFetcherExceptionHandler(DefaultExceptionHandler)
        .build()
}