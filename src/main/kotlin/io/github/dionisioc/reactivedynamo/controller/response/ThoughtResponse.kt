package io.github.dionisioc.reactivedynamo.controller.response

import io.github.dionisioc.reactivedynamo.enums.Kind

data class ThoughtResponse(
    val id: String,
    val content: String,
    val kind: Kind
)
