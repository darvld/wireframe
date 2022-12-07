package io.github.darvld.wireframe

import graphql.schema.DataFetcher

internal sealed interface Wiring {
    @JvmInline
    value class DataFetcherWiring(val fetcher: DataFetcher<Any?>) : Wiring
}