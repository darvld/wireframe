package io.github.darvld.wireframe.routing

import io.github.darvld.wireframe.WireframeInternal
import io.github.darvld.wireframe.execution.SuspendingDataFetcher

@OptIn(WireframeInternal::class)
internal sealed interface Wiring {
    @JvmInline
    value class DataFetcherWiring(val fetcher: SuspendingDataFetcher<Any?>) : Wiring
}