package io.github.dionisioc.reactivedynamo.controller.request

import io.github.dionisioc.reactivedynamo.enums.Kind
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ThoughtRequest(
    val id: String?,
    @NotBlank
    val content: String,
    @NotNull
    val kind: Kind
)
