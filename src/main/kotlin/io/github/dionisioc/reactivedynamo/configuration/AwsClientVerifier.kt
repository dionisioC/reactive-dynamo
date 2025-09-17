package io.github.dionisioc.reactivedynamo.configuration

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.http.async.SdkAsyncHttpClient
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient

@Configuration
class AwsClientVerifier {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun verifyAwsHttpClient(sdkAsyncHttpClient: SdkAsyncHttpClient): CommandLineRunner {
        return CommandLineRunner {
            val clientClass = sdkAsyncHttpClient.javaClass
            val expectedClass = AwsCrtAsyncHttpClient::class.java

            logger.info("--- Verifying AWS HTTP Client ---")
            logger.info("Injected SdkAsyncHttpClient type: ${clientClass.name}")

            if (expectedClass.isAssignableFrom(clientClass)) {
                logger.info("✅ SUCCESS: The correct AwsCrtAsyncHttpClient is configured.")
            } else {
                logger.error("❌ FAILURE: The application is NOT using the expected AwsCrtAsyncHttpClient.")
                logger.error("Instead, it's using: ${clientClass.name}. Check your bean configuration.")
            }
            logger.info("------------------------------------")
        }
    }
}