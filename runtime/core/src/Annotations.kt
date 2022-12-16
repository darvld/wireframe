package io.github.darvld.wireframe

@DslMarker
public annotation class ResolversDsl

@RequiresOptIn(
    "This declaration is part of Wireframe's internal API," +
        " it should not be used in general code unless you're" +
        " developing custom integrations."
)
public annotation class WireframeInternal