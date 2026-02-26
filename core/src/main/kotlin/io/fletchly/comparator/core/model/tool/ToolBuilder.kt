package io.fletchly.comparator.core.model.tool

import io.fletchly.comparator.core.exception.ToolDefinitionException
import io.fletchly.comparator.core.exception.ToolException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.serializer
import kotlin.collections.get
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.findAnnotation

class ToolBuilder(private val name: String) {
    var description = ""
    private var handler: KFunction<*>? = null

    fun executes(fn: KFunction<*>) {
        handler = fn
    }

    fun build(): ToolDefinition {
        val fn = requireNotNull(handler) { "handler function must be provided" }
        require(fn.isSuspend) { "handler function must be a suspend function" }
        validateReturnType(fn.returnType)
        val parameters: List<ToolParameter> = fn.parameters
            .filter { it.kind == KParameter.Kind.VALUE }
            .map { param ->
                ToolParameter(
                    name = param.name ?: error("parameter must be named"),
                    type = param.type.toToolParameterType(),
                    required = !param.type.isMarkedNullable && !param.isOptional,
                    description = param.findAnnotation<Description>()?.value
                        ?: error("parameter must have a description")
                )
            }

        return Tool(name, description, parameters) { args ->
            try {
                val mappedArgs = fn.parameters
                    .filter { it.kind == KParameter.Kind.VALUE }
                    .associateWith { param -> args[param.name] }

                val json = when (val result = fn.callSuspendBy(mappedArgs)) {
                    is JsonElement -> result
                    is String -> JsonPrimitive(result)
                    is Number -> JsonPrimitive(result)
                    is Boolean -> JsonPrimitive(result)
                    // serialization is guaranteed to succeed here by validateReturnType
                    else -> Json.encodeToJsonElement(result) // requires @Serializable on return type
                }

                ToolResult.Success(name, json)
            } catch (ex: ToolException) {
                ToolResult.Failure(
                    name,
                    ex.message ?: "An unknown error occurred"
                )
            }
        }
    }

    private fun validateReturnType(type: KType) {
        try {
            serializer(type)
        } catch (_: SerializationException) {
            throw ToolDefinitionException("Return type '$type' is not serializable. Annotate it with @Serializable.")
        }
    }
}

fun tool(name: String, block: ToolBuilder.() -> Unit): ToolDefinition =
    ToolBuilder(name).apply(block).build()

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Description(val value: String)
