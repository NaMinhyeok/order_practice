package gc.cafe.docs

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.restdocs.snippet.Attributes

@TestConfiguration
class RestDocsConfig {
    companion object {
        fun field(
            key: String?,
            value: String?
        ): Attributes.Attribute {
            return Attributes.Attribute(key, value)
        }
    }
}
