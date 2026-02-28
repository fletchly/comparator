package io.fletchly.comparator.adapter.ollama.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Runtime options that control text generation
 *
 * @see <a href="https://docs.ollama.com/api/chat">Ollama Chat API docs</a>
 *
 * @property seed random seed used for reproducible outputs
 * @property temperature controls randomness in generation (higher = more random)
 * @property topK limits next token selection to the K most likely
 * @property topP cumulative probability threshold for nucleus sampling
 * @property minP minimum probability threshold for token selection
 * @property stop stop sequences that will halt generation
 * @property numCtx context length size (number of tokens)
 * @property numPredict maximum number of tokens to generate
 */
@Serializable
data class ChatOptions(
    val seed: Long? = null,
    val temperature: Float? = null,
    @SerialName("top_K") val topK: Int? = null,
    @SerialName("top_p") val topP: Float? = null,
    @SerialName("min_p") val minP: Float? = null,
    val stop: String? = null,
    @SerialName("num_ctx") val numCtx: Int? = null,
    @SerialName("num_predict") val numPredict: Int? = null
    )
