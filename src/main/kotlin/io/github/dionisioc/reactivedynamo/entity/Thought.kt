package io.github.dionisioc.reactivedynamo.entity

import io.github.dionisioc.reactivedynamo.enums.Kind
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

@DynamoDbBean
data class Thought(
    @get:DynamoDbPartitionKey
    var id: String? = null,
    var content: String? = null,
    var kind: Kind? = null
) {
    companion object {
        @JvmStatic
        val TABLE_NAME = "THOUGHT"
        val TABLE_SCHEMA: BeanTableSchema<Thought> = TableSchema.fromBean(Thought::class.java)
    }
}
