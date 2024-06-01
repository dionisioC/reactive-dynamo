package io.github.dionisioc.reactivedynamo.controller

import io.github.dionisioc.reactivedynamo.controller.request.ThoughtRequest
import io.github.dionisioc.reactivedynamo.controller.response.ThoughtResponse
import io.github.dionisioc.reactivedynamo.service.ThoughtService
import jakarta.validation.Valid
import org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@RestController
class ThoughtController(private val thoughtService: ThoughtService) {

    @PostMapping("/thoughts", produces = [APPLICATION_JSON_VALUE])
    fun saveThought(@Valid @RequestBody thought: ThoughtRequest): Mono<ThoughtResponse> {
        return thoughtService.save(thought)
    }

    @GetMapping("/thoughts/{id}", produces = [APPLICATION_JSON_VALUE])
    fun findThoughtById(@PathVariable id: String): Mono<ThoughtResponse> {
        return thoughtService.findById(id)
    }

    @PutMapping("/thoughts", produces = [APPLICATION_JSON_VALUE])
    fun updateThought(@Valid @RequestBody thought: ThoughtRequest): Mono<ThoughtResponse> {
        return thoughtService.update(thought)
    }

    @DeleteMapping("/thoughts/{id}", produces = [APPLICATION_JSON_VALUE])
    fun deleteThoughtById(@PathVariable id: String): Mono<ThoughtResponse> {
        return thoughtService.delete(id)
    }

    @GetMapping("/thoughts", produces = [APPLICATION_JSON_VALUE])
    fun findAllThoughts(): Flux<ThoughtResponse> {
        return thoughtService.findAll()
    }

}