/*
 * This file is part of comparator, licensed under the Apache License 2.0
 *
 * Copyright (c) 2026 fletchly <https://github.com/fletchly>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fletchly.comparator.tool

import io.fletchly.comparator.annotation.AllowedValues
import io.fletchly.comparator.annotation.ToolFunction
import io.fletchly.comparator.annotation.ToolParameter
import io.fletchly.comparator.exception.ToolException
import io.fletchly.comparator.model.tool.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.serializer
import java.lang.reflect.InvocationTargetException
import kotlin.collections.get
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.findAnnotation

/**
 * Creates a `Tool` instance from a handler function by validating its structure, annotations, and parameters.
 *
 * The function ensures that the handler function meets the required conditions:
 * - It must be annotated with [ToolFunction] to provide a name and description.
 * - Its parameters must be annotated with [ToolParameter] to define their metadata.
 * - If applicable, parameters can optionally use [AllowedValues] to constrain their valid input values.
 * - Its return type must be non-nullable and serializable.
 *
 * @sample io.fletchly.comparator.example.ToolBuilderExample.simpleToolDefinition
 *
 * @param fn The suspend function representing the tool's behavior. It must be annotated with `@ToolFunction` and its
 * parameters must be described using `@ToolParameter` annotations.
 * @return A [Tool] instance representing the validated and structured tool, ready for execution.
 */
fun tool(fn: KFunction<*>): Tool {
    require(fn.parameters.none { it.kind == KParameter.Kind.INSTANCE }) {
        "Handler function '${fn.name}' must be a bound reference. Use 'instance::method' instead of 'ClassName::method'"
    }

    val toolFunctionAnnotation = fn.findAnnotation<ToolFunction>()
        ?: error("Handler function '${fn.name}' must be annotated with @ToolFunction")

    val name = toolFunctionAnnotation.name.ifEmpty { fn.name }

    require(toolFunctionAnnotation.description.isNotBlank()) {
        "Tool '${name}' must have a non-blank description"
    }

    validateReturnType(fn)

    val parameters = fn.parameters
        .filter { it.kind == KParameter.Kind.VALUE }
        .map { param ->
            val paramAnnotation = param.findAnnotation<ToolParameter>()
                ?: error("Parameter '${param.name}' on tool '$name' must be annotated with @ToolParameter")

            require(paramAnnotation.description.isNotBlank()) {
                "Parameter '${param.name}' on tool '$name' must have a non-blank description"
            }

            val allowedValuesAnnotation = param.findAnnotation<AllowedValues>()
            val (type, elementType) = param.type.toToolParameterType()

            if (allowedValuesAnnotation != null) {
                require(type == Parameter.Type.STRING) {
                    "Parameter '${param.name}' on tool '$name': @AllowedValues is only valid on String parameters"
                }
                require(allowedValuesAnnotation.values.isNotEmpty()) {
                    "Parameter '${param.name}' on tool '$name': @AllowedValues must specify at least one value"
                }
            }

            Parameter(
                name = param.name ?: error("Parameter on tool '$name' must be named"),
                type = type,
                description = paramAnnotation.description,
                required = paramAnnotation.required,
                enum = allowedValuesAnnotation?.values?.toList(),
                elementType = elementType
            )
        }

    return Tool(name, toolFunctionAnnotation.description, parameters) { args ->
        try {
            val mappedArgs = fn.parameters
                .filter { it.kind == KParameter.Kind.VALUE }
                .associateWith { param -> args[param.name] }

            // safe to cast here because of the validateReturnType call
            @Suppress("UNCHECKED_CAST")
            val serializer = serializer(fn.returnType) as KSerializer<Any>
            val result = fn.callSuspendBy(mappedArgs) ?: error("Tool '$name' handler returned null unexpectedly")

            ToolResult.Success(name, result, serializer)
        } catch (ex: InvocationTargetException) {
            val cause = ex.cause
            if (cause is ToolException) {
                ToolResult.Failure(name, cause)
            } else {
                throw cause ?: ex
            }
        } catch (ex: ToolException) {
            ToolResult.Failure(name, ex)
        }
    }
}

private fun validateReturnType(fn: KFunction<*>) {
    require(!fn.returnType.isMarkedNullable) {
        "Return type of function '${fn.name}' must not be nullable"
    }
    try {
        serializer(fn.returnType)
    } catch (_: SerializationException) {
        error("Return type '${fn.returnType}' of function '${fn.name}' is not serializable. Annotate it with @Serializable.")
    }
}