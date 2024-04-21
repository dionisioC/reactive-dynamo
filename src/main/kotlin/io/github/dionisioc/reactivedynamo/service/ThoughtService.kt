package io.github.dionisioc.reactivedynamo.service

import io.github.dionisioc.reactivedynamo.controller.request.ThoughtRequest
import io.github.dionisioc.reactivedynamo.controller.response.ThoughtResponse
import io.github.dionisioc.reactivedynamo.entity.Thought
import io.github.dionisioc.reactivedynamo.repository.ThoughtRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class ThoughtService(private val thoughtRepository: ThoughtRepository) {

    fun save(request: ThoughtRequest): Mono<ThoughtResponse> {
        val id = UUID.randomUUID().toString()
        val thought = Thought(id, request.content, request.kind)
        return thoughtRepository.save(thought).map { ThoughtResponse(it.id!!, it.content!!, it.kind!!) }
    }

    fun findById(id: String): Mono<ThoughtResponse> {
        return thoughtRepository.findById(id).map { ThoughtResponse(it.id!!, it.content!!, it.kind!!) }
    }

    fun update(request: ThoughtRequest): Mono<ThoughtResponse> {
        val thought = Thought(request.id, request.content, request.kind)
        return thoughtRepository.update(thought).map { ThoughtResponse(it.id!!, it.content!!, it.kind!!) }
    }

    fun delete(id: String): Mono<ThoughtResponse> {
        return thoughtRepository.delete(id).map { ThoughtResponse(it.id!!, it.content!!, it.kind!!) }
    }

    fun findAll(): Flux<ThoughtResponse> {
        return thoughtRepository.findAll().map { ThoughtResponse(it.id!!, it.content!!, it.kind!!) }
    }

}