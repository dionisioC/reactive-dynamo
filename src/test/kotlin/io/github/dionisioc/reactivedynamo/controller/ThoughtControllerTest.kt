package io.github.dionisioc.reactivedynamo.controller

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer
import io.github.dionisioc.reactivedynamo.configuration.DynamoDbConfiguration
import io.github.dionisioc.reactivedynamo.controller.request.ThoughtRequest
import io.github.dionisioc.reactivedynamo.controller.response.ThoughtResponse
import io.github.dionisioc.reactivedynamo.entity.Thought
import io.github.dionisioc.reactivedynamo.enums.Kind
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput
import java.util.*

private const val DB_HOST = "localhost"
private const val DB_PORT = 4566
private const val DB_URI = "http://$DB_HOST:$DB_PORT"
private const val DB_REGION = "eu-south-2"
private val args = arrayOf("-sharedDb", "-disableTelemetry", "-inMemory", "-port", DB_PORT.toString())
private lateinit var dbServer: DynamoDBProxyServer
private lateinit var dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
private lateinit var thoughtTable: DynamoDbAsyncTable<Thought>

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ThoughtControllerTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Test
    fun `Should save a thought`() {
        val expectedContent = "Test content"
        val expectedKind = Kind.POSITIVE
        var savedThoughtId = ""

        webClient
            .post()
            .uri("/thoughts")
            .body(BodyInserters.fromValue(ThoughtRequest(null, expectedContent, expectedKind)))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.id").value<String> { id -> savedThoughtId = id }
            .jsonPath("$.content").isEqualTo(expectedContent)
            .jsonPath("$.kind").isEqualTo(expectedKind.name)

        val deletedThought = thoughtTable.deleteItem(Key.builder().partitionValue(savedThoughtId).build()).join()
        assertEquals(savedThoughtId, deletedThought.id)
        assertEquals(expectedContent, deletedThought.content)
        assertEquals(expectedKind, deletedThought.kind)
    }

    @Test
    fun `Should find a thought by id`() {
        val expectedContent = "Test content"
        val expectedKind = Kind.POSITIVE
        val savedThoughtId = UUID.randomUUID().toString()

        thoughtTable.putItem(Thought(savedThoughtId, expectedContent, expectedKind)).join()

        webClient
            .get()
            .uri("/thoughts/{id}", savedThoughtId)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(savedThoughtId)
            .jsonPath("$.content").isEqualTo(expectedContent)
            .jsonPath("$.kind").isEqualTo(expectedKind.name)


        val deletedThought = thoughtTable.deleteItem(Key.builder().partitionValue(savedThoughtId).build()).join()
        assertEquals(savedThoughtId, deletedThought.id)
        assertEquals(expectedContent, deletedThought.content)
        assertEquals(expectedKind, deletedThought.kind)
    }

    @Test
    fun `Should update an existing thought`() {
        val expectedContent = "Updated content"
        val expectedKind = Kind.NEGATIVE
        val savedThoughtId = UUID.randomUUID().toString()

        thoughtTable.putItem(Thought(savedThoughtId, "Doesnt matter it is going to be updated", Kind.POSITIVE)).join()

        webClient
            .put()
            .uri("/thoughts")
            .body(
                BodyInserters.fromValue(
                    ThoughtRequest(
                        savedThoughtId,
                        "Updated content",
                        Kind.NEGATIVE
                    )
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(savedThoughtId)
            .jsonPath("$.content").isEqualTo(expectedContent)
            .jsonPath("$.kind").isEqualTo(expectedKind.name)

        val databaseThought = thoughtTable.getItem(Key.builder().partitionValue(savedThoughtId).build()).join()
        assertEquals(savedThoughtId, databaseThought.id)
        assertEquals(expectedContent, databaseThought.content)
        assertEquals(expectedKind, databaseThought.kind)

        val deletedThought = thoughtTable.deleteItem(Key.builder().partitionValue(savedThoughtId).build()).join()
        assertEquals(savedThoughtId, deletedThought.id)
        assertEquals(expectedContent, deletedThought.content)
        assertEquals(expectedKind, deletedThought.kind)

    }

    @Test
    fun `Should delete an existing thought`() {
        val expectedContent = "Test content"
        val expectedKind = Kind.POSITIVE
        val savedThoughtId = UUID.randomUUID().toString()

        thoughtTable.putItem(Thought(savedThoughtId, expectedContent, expectedKind)).join()

        webClient
            .delete()
            .uri("/thoughts/{id}", savedThoughtId)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(savedThoughtId)
            .jsonPath("$.content").isEqualTo(expectedContent)
            .jsonPath("$.kind").isEqualTo(expectedKind.name)

        val databaseThought = thoughtTable.getItem(Key.builder().partitionValue(savedThoughtId).build()).join()
        assertNull(databaseThought)

    }

    @Test
    fun `Should get several existing thoughts`() {

        val expectedContent = "Test content"
        val expectedKind = Kind.POSITIVE
        val savedThoughtId = UUID.randomUUID().toString()

        val expectedContent2 = "Test content"
        val expectedKind2 = Kind.POSITIVE
        val savedThoughtId2 = UUID.randomUUID().toString()

        thoughtTable.putItem(Thought(savedThoughtId, expectedContent, expectedKind)).join()
        thoughtTable.putItem(Thought(savedThoughtId2, expectedContent2, expectedKind2)).join()

        webClient
            .get()
            .uri("/thoughts", savedThoughtId)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$").isArray
            .jsonPath("$", hasSize<Array<ThoughtResponse>>(2))

    }


    companion object {

        @JvmStatic
        @BeforeAll
        fun init() {
            print("Starting DynamoDB Local...")
            dbServer = ServerRunner.createServerFromCommandLineArgs(args)
            dbServer.start()

            val dbConf = DynamoDbConfiguration(DB_URI, DB_REGION)
            dynamoDbEnhancedAsyncClient = dbConf.getDynamoDbEnhancedClient()

            thoughtTable = dynamoDbEnhancedAsyncClient.table(Thought.TABLE_NAME, Thought.TABLE_SCHEMA)

            thoughtTable.createTable { builder: CreateTableEnhancedRequest.Builder ->
                builder
                    .provisionedThroughput { b: ProvisionedThroughput.Builder ->
                        b
                            .readCapacityUnits(10L)
                            .writeCapacityUnits(10L)
                            .build()
                    }

            }.join()
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            print("Stopping DynamoDB Local...")
            dbServer.stop()
        }

    }

}
