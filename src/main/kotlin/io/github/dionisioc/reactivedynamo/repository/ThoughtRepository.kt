package io.github.dionisioc.reactivedynamo.repository

import io.github.dionisioc.reactivedynamo.entity.Thought
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest

@Repository
class ThoughtRepository(dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient) {

    private var table: DynamoDbAsyncTable<Thought> =
        dynamoDbEnhancedAsyncClient.table(Thought.TABLE_NAME, Thought.TABLE_SCHEMA)

    fun save(thought: Thought): Mono<Thought> {
        val putItemRequest =
            PutItemEnhancedRequest.builder(Thought::class.java).item(thought).build()
        return Mono.fromCompletionStage(table.putItemWithResponse(putItemRequest).thenApply { thought })
    }

    fun findById(id: String): Mono<Thought> {
        return Mono.fromCompletionStage(table.getItem(buildKey(id)))
    }

    fun update(thought: Thought): Mono<Thought> {
        val updateRequest = UpdateItemEnhancedRequest.builder(Thought::class.java).item(thought).build()
        return Mono.fromCompletionStage(table.updateItem(updateRequest))
    }

    fun delete(id: String): Mono<Thought> {
        return Mono.fromCompletionStage(table.deleteItem(Key.builder().partitionValue(id).build()))
    }

    fun findAll(): Flux<Thought> {
        return Flux.from(table.scan().items())
    }

    private fun buildKey(id: String): Key {
        return Key.builder().partitionValue(id).build()
    }

}