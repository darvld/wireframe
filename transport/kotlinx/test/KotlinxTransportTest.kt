import graphql.ExecutionResultImpl
import io.github.darvld.wireframe.transport.KotlinxTransport
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class KotlinxTransportTest {
    @Test
    fun `should decode valid request`() {
        val request = """
        {
        "query": "query Artist(${'$'}id: ID!) {\n  artist(id: ${'$'}id) {\n    id\n    name\n    bio\n  }\n}",
        "variables": { "id": "14629046914156" },
        "operationName": "Artist"
        }
        """.trimIndent()

        val input = KotlinxTransport.decodeRequest(request)
        input.shouldNotBeNull()

        input.operationName shouldBe "Artist"
        input.query shouldBe "query Artist(\$id: ID!) {\n  artist(id: \$id) {\n    id\n    name\n    bio\n  }\n}"
        input.variables shouldBe mapOf("id" to "14629046914156")
    }

    @Test
    fun `should return null for invalid request`() {
        val request = """
        {
        "variables": { "id": "14629046914156" },
        "operationName": "Artist"
        }
        """.trimIndent()

        val input = KotlinxTransport.decodeRequest(request)
        input.shouldBeNull()
    }

    @Test
    fun `should return valid JSON for encoded response`() {
        val result = ExecutionResultImpl.newExecutionResult()
            .data(
                mapOf(
                    "artist" to mapOf(
                        "id" to "14629046914156",
                        "name" to "Bob",
                        "bio" to "..."
                    )
                )
            )
            .build()

        val encoded = KotlinxTransport.encodeResponse(result)

        encoded shouldBe """{"data":{"artist":{"id":"14629046914156","name":"Bob","bio":"..."}}}"""
    }
}