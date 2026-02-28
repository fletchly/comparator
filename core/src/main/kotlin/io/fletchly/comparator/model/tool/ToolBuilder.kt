package io.fletchly.comparator.model.tool

import io.fletchly.comparator.exception.ToolDefinitionException
import io.fletchly.comparator.exception.ToolException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.findAnnotation

/**
 * A builder class for creating and configuring instances of [Tool].
 *
 * This class provides a fluent interface for defining the structure, metadata,
 * and behavior of a tool. It facilitates the configuration of a tool's name,
 * description, parameters, and execution logic.
 *
 * @property name The unique name of the tool being built.
 */
class ToolBuilder(private val name: String) {
    var description = ""
    private var handler: KFunction<*>? = null

    fun executes(fn: KFunction<*>) {
        handler = fn
    }

    fun build(): Tool {
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
                    // Pass the serializer explicitly rather than letting Json infer it from the runtime
                    // type, as type inference loses information and fails for some class hierarchies
                    // serialization is guaranteed to succeed here by validateReturnType
                    else -> Json.encodeToJsonElement(
                        serializer(fn.returnType),
                        result
                    ) // requires @Serializable on return type
                }

                ToolResult.Success(name, json)
            } catch (ex: InvocationTargetException) {
                val cause = ex.cause
                if (cause is ToolException) {
                    ToolResult.Failure(name, cause.message)
                } else {
                    throw cause ?: ex
                }
            } catch (ex: ToolException) {
                ToolResult.Failure(name, ex.message)
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

/**
 * Creates and configures a tool definition using the provided name and configuration block.
 *
 * This function initializes a [ToolBuilder] with the specified name, applies the given
 * configuration block to define the tool's metadata, parameters, and behavior, and then
 * builds and returns a [Tool] instance.
 *
 * @param name The unique name of the tool to be created.
 * @param block A configuration block that is applied to the [ToolBuilder] to define the tool's attributes and behavior.
 * @return A [Tool] instance representing the configured tool.
 */
fun tool(name: String, block: ToolBuilder.() -> Unit): Tool =
    ToolBuilder(name).apply(block).build()

/**
 * An annotation used to provide a human-readable description for a tool parameter.
 *
 * This annotation can be applied to parameters of a function to define a description
 * that explains the parameter's purpose or usage.
 *
 * @property value The descriptive text explaining the parameter's purpose or usage.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Description(val value: String)
