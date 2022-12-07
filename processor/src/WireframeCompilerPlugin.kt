package io.github.darvld.wireframe

import graphql.schema.GraphQLNamedType

public interface WireframeCompilerPlugin {
    public fun processType(type: GraphQLNamedType, environment: ProcessingEnvironment)

    public fun beforeProcessing(environment: ProcessingEnvironment) {
        // Unit
    }

    public fun afterProcessing(environment: ProcessingEnvironment) {
        // Unit
    }
}