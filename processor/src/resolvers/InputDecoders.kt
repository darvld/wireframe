package io.github.darvld.wireframe.resolvers

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.schema.GraphQLInputObjectType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.extensions.buildFunction
import io.github.darvld.wireframe.extensions.markAsGenerated
import io.github.darvld.wireframe.extensions.nullable

private const val DECODER_MAP_PARAM = "map"

@OptIn(DelicateKotlinPoetApi::class)
internal fun buildDecoder(
    outputType: ClassName,
    definition: GraphQLInputObjectType,
    environment: ProcessingEnvironment,
): FunSpec {
    return buildFunction(outputType.simpleName) {
        markAsGenerated()
        addKdoc("Constructs a new ${outputType.simpleName} from an unsafe map.")

        // Suppress compiler warnings about unchecked casts from Any to Map<K, V>
        addAnnotation(AnnotationSpec.get(Suppress("unchecked_cast")))

        returns(outputType)
        addParameter(DECODER_MAP_PARAM, MAP.parameterizedBy(STRING, ANY.nullable()))

        addCode(buildCodeBlock {
            add("return·%T(\n", outputType)
            indent()

            for (field in definition.fields) {
                val extractor = environment.buildFieldExtractor(
                    unwrap = { CodeBlock.of("%L[%S]·as·%T", DECODER_MAP_PARAM, field.name, it) },
                    fieldType = field.type,
                )

                addStatement("%L = %L,", field.name, extractor)
            }

            unindent()
            add(")\n")
        })
    }
}