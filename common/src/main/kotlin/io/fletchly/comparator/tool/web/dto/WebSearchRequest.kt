package io.fletchly.comparator.tool.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class WebSearchRequest(val query: String)
