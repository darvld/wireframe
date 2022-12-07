package io.github.darvld.wireframe

internal enum class WiringType(val prefix: String) {
    SchemaDirective("directive"),
    TypeResolver("resolver"),
    DataFetcher("fetcher"),
    Scalar("scalar"),
}