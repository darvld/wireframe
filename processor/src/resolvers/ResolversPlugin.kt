package io.github.darvld.wireframe.resolvers

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLObjectType
import io.github.darvld.wireframe.*
import io.github.darvld.wireframe.extensions.*
import io.github.darvld.wireframe.routing.ResolverScope
import io.github.darvld.wireframe.routing.Resolvers

public object ResolversPlugin : WireframeCompilerPlugin {
    private val JVM_INLINE = JvmInline::class.asClassName()
    private val DSL_MARKER = ResolversDsl::class.asClassName()

    private val RESOLVERS = Resolvers::class.asClassName()
    private val RESOLVER_SCOPE = ResolverScope::class.asClassName()

    private val OPT_IN = ClassName("kotlin", "OptIn")
    private val INTERNAL_API_MARKER = ClassName("io.github.darvld.wireframe", "WireframeInternal")

    private const val WRAPPED_RESOLVERS_PROP_NAME = "resolvers"
    private const val RESOLVER_LAMBDA_PARAM_NAME = "resolver"
    private const val PARENT_PARAM_NAME = "parent"
    private const val ROUTING_PARAM_NAME = "resolvers"

    private val ProcessingEnvironment.resolversPackage
        get() = "$basePackage.resolvers"

    private fun ProcessingEnvironment.scopeClassName(type: GraphQLObjectType): ClassName {
        return ClassName(resolversPackage, "${type.name}Resolvers")
    }

    override fun processType(type: GraphQLNamedType, environment: ProcessingEnvironment) {
        // TODO: Also process interfaces and unions, adding a special `resolveType` method
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
                addAnnotation(optInToInternalApi())
                addKdoc(field.description.orEmpty())

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
            addKdoc(type.description.orEmpty())

            receiver(RESOLVERS)
            addModifiers(INLINE)

            val scopeType = environment.scopeClassName(type)
            addParameter(ROUTING_PARAM_NAME, LambdaTypeName.get(scopeType, returnType = UNIT))
            addCode("%T(this).%L()", scopeType, ROUTING_PARAM_NAME)
        }
    }

    private fun optInToInternalApi(): AnnotationSpec {
        return AnnotationSpec
            .builder(OPT_IN)
            .addMember("%T::class", INTERNAL_API_MARKER)
            .build()
    }
}