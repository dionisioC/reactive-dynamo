package io.github.dionisioc.reactivedynamo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReactiveDynamoApplication

fun main(args: Array<String>) {
    runApplication<ReactiveDynamoApplication>(*args)
}
