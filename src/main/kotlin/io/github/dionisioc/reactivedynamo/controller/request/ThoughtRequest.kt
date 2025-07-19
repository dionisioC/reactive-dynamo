package io.github.dionisioc.reactivedynamo.controller.request

import io.github.dionisioc.reactivedynamo.enums.Kind
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ThoughtRequest(
    val id: String?,
    @param:NotBlank
    val content: String,
    @param:NotNull
    val kind: Kind
)
