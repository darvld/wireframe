package io.github.darvld.wireframe.resolvers

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.ProcessingEnvironment
import io.github.darvld.wireframe.ResolversDsl
import io.github.darvld.wireframe.WireframeCompilerPlugin
import io.github.darvld.wireframe.extensions.*
import io.github.darvld.wireframe.output
import io.github.darvld.wireframe.routing.ResolverScope
import io.github.darvld.wireframe.routing.Resolvers

public class ResolversPlugin : WireframeCompilerPlugin {
    private val ProcessingEnvironment.resolversPackage
        get() = "$basePackage.resolvers"

    private fun ProcessingEnvironment.scopeClassName(type: GraphQLObjectType): ClassName {
        return ClassName(resolversPackage, "${type.name}Resolvers")
    }

    override fun processType(type: GraphQLNamedType, environment: ProcessingEnvironment) {
        // TODO: Also process interfaces and unions, adding a special `resolveType` method, and scalars
        // ...

        // Generate decoders for input types
        if (type is GraphQLInputObjectType) {
            val generatedName = environment.resolve(type)
            val decoder = buildDecoder(generatedName, type, environment)

            environment.output(
                packageName = generatedName.packageName,
                fileName = generatedName.simpleName,
                spec = decoder,
            )
        }

        // Only process relevant types (Query, Mutation, Subscription, and custom output types)
        if (type !is GraphQLObjectType) return

        // Generate a scope for each type
        val scopeTypeName = environment.scopeClassName(type)
        val scopeClassSpec = buildClass(scopeTypeName) {
            markAsGenerated()
            addAnnotation(DSL_MARKER)

            // Use an inline class to reduce overhead
            addAnnotation(JVM_INLINE)
            addModifiers(VALUE)

            // Wrapped value (`private val resolvers: Resolvers`)
            primaryConstructor(buildConstructor {
                val property = PropertySpec.builder(WRAPPED_RESOLVERS_PROP_NAME, RESOLVERS, PRIVATE)
                    .initializer(WRAPPED_RESOLVERS_PROP_NAME)

                addProperty(property.build())
                addParameter(WRAPPED_RESOLVERS_PROP_NAME, RESOLVERS)
            })

            // Create resolvers for each field
            for (field in type.fields) addFunction(buildFunction(field.name) {
                markAsGenerated()
                addAnnotation(DSL_MARKER)

                // Create resolver lambda signature
                val lambdaType = LambdaTypeName.get(
                    receiver = RESOLVER_SCOPE,
                    returnType = environment.typeNameFor(field.type),
                    parameters = buildList {
                        // Fields in custom types (i.e. not Query, Mutation, or Subscription)
                        // will receive an extra parameter: `parent`, to access the resolved value
                        // for the parent type
                        if (!type.isRouteType()) add(
                            ParameterSpec(
                                name = PARENT_PARAM_NAME,
                                type = environment.typeNameFor(type).nonNullable(),
                            )
                        )

                        // Add field arguments to the lambda
                        field.arguments.forEach { add(ParameterSpec(it.name, environment.typeNameFor(it.type))) }
                    }).copy(suspending = true)

                addParameter(RESOLVER_LAMBDA_PARAM_NAME, lambdaType)

                // Resolver DSL body
                addCode {
                    // Use `resolver`, pass in the field path
                    // Unpack arguments using extensions
                    // Invoke lambda parameter
                    beginControlFlow("%L.resolver(%S)", WRAPPED_RESOLVERS_PROP_NAME, "${type.name}.${field.name}")

                    // Unwrap arguments
                    field.arguments.forEach { argument ->
                        val unwrapper = environment.buildFieldExtractor(
                            unwrap = { CodeBlock.of("env.getArgument<%T>(%S)", it, argument.name) },
                            argument.type,
                        )

                        addStatement("val·%L = %L", argument.name, unwrapper)
                    }

                    // Invoke resolver parameter with unwrapped arguments
                    add("\n")
                    addStatement(
                        "%L(%L%L)",
                        RESOLVER_LAMBDA_PARAM_NAME,
                        // Fields in custom types receive the resolved value for the parent as first parameter
                        "env.getSource(),".takeUnless { type.isRouteType() }.orEmpty(),
                        // Pass the unwrapped arguments to the resolver
                        field.arguments.joinToString { it.name }
                    )

                    endControlFlow()
                }
            })
        }

        // Generate extensions on `Resolvers` for all types (e.g. `query`, `mutation`, `customType`)
        val resolverExtensionSpec = generateRouteExtension(type, environment)

        environment.output(scopeTypeName.packageName, scopeTypeName.simpleName) {
            addType(scopeClassSpec)
            addFunction(resolverExtensionSpec)
        }
    }

    private fun generateRouteExtension(type: GraphQLObjectType, environment: ProcessingEnvironment): FunSpec {
        return buildFunction(type.name.replaceFirstChar(Char::lowercaseChar)) {
            markAsGenerated()
            addAnnotation(DSL_MARKER)

            receiver(RESOLVERS)
            addModifiers(INLINE)

            val scopeType = environment.scopeClassName(type)
            addParameter(ROUTING_PARAM_NAME, LambdaTypeName.get(scopeType, returnType = UNIT))
            addCode("%T(this).%L()", scopeType, ROUTING_PARAM_NAME)
        }
    }

    private companion object {
        val JVM_INLINE = JvmInline::class.asClassName()
        val DSL_MARKER = ResolversDsl::class.asClassName()

        val RESOLVERS = Resolvers::class.asClassName()
        val RESOLVER_SCOPE = ResolverScope::class.asClassName()

        const val WRAPPED_RESOLVERS_PROP_NAME = "resolvers"
        const val RESOLVER_LAMBDA_PARAM_NAME = "resolver"
        const val PARENT_PARAM_NAME = "parent"
        const val ROUTING_PARAM_NAME = "resolvers"
    }
}