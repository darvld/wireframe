package io.github.darvld.wireframe.routing

import io.github.darvld.wireframe.execution.SuspendingDataFetcher

internal sealed interface Wiring {
    @JvmInline
    value class DataFetcherWiring(val fetcher: SuspendingDataFetcher<Any?>) : Wiring
}