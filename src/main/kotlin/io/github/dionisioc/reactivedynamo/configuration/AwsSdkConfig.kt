package io.github.dionisioc.reactivedynamo.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.http.async.SdkAsyncHttpClient
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import java.net.URI

@Configuration
class AwsSdkConfig(
    @param:Value("\${aws.dynamodb.url}") private val url: String,
    @param:Value("\${aws.dynamodb.region}") private val region: String
) {

    // Bean #1: The high-performance CRT-based HTTP client
    @Bean
    fun sdkAsyncHttpClient(): SdkAsyncHttpClient {
        return AwsCrtAsyncHttpClient.builder()
            .maxConcurrency(200)
            .build()
    }

    // Bean #2: The standard DynamoDB Async Client, configured to USE the CRT client
    @Bean
    fun dynamoDbAsyncClient(sdkAsyncHttpClient: SdkAsyncHttpClient): DynamoDbAsyncClient {
        val awsCredentials = AwsBasicCredentials.create("dummy", "dummy")

        val credentialsProvider = StaticCredentialsProvider.create(awsCredentials)

        return DynamoDbAsyncClient.builder()
            .credentialsProvider(credentialsProvider)
            .endpointOverride(URI.create(url))
            .region(Region.of(region))
            .httpClient(sdkAsyncHttpClient)
            .build()
    }

    // Bean #3: The Enhanced DynamoDB client that the application will use
    @Bean
    fun dynamoDbEnhancedAsyncClient(dynamoDbAsyncClient: DynamoDbAsyncClient): DynamoDbEnhancedAsyncClient {
        return DynamoDbEnhancedAsyncClient.builder()
            .dynamoDbClient(dynamoDbAsyncClient)
            .build()
    }
}