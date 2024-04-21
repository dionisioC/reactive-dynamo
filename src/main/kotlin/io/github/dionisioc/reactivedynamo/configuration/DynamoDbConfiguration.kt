package io.github.dionisioc.reactivedynamo.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import java.net.URI

@Configuration
class DynamoDbConfiguration(
    @Value("\${aws.dynamodb.url}") private val url: String,
    @Value("\${aws.dynamodb.region}") private val region: String
) {

    @Bean
    fun getDynamoDbClient(): DynamoDbAsyncClient {

        val awsCredentials = AwsBasicCredentials.create("dummy", "dummy")

        val credentialsProvider = StaticCredentialsProvider.create(awsCredentials)

        return DynamoDbAsyncClient.builder()
            .credentialsProvider(credentialsProvider)
            .endpointOverride(URI.create(url))
            .region(Region.of(region))
            .build()
    }


    @Bean
    fun getDynamoDbEnhancedClient(): DynamoDbEnhancedAsyncClient {
        return DynamoDbEnhancedAsyncClient.builder().dynamoDbClient(getDynamoDbClient()).build()
    }


}