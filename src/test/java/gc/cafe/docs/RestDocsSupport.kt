package gc.cafe.docs

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.web.filter.CharacterEncodingFilter

@ExtendWith(RestDocumentationExtension::class)
abstract class RestDocsSupport {
    @JvmField
    protected var mockMvc: MockMvc? = null
    @JvmField
    protected var objectMapper: ObjectMapper = ObjectMapper()

    @BeforeEach
    fun setUp(provider: RestDocumentationContextProvider?) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
            .apply<StandaloneMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(provider))
            .addFilters<StandaloneMockMvcBuilder>(CharacterEncodingFilter("UTF-8", true)) // 한글 깨짐 방지
            .build()
    }

    protected abstract fun initController(): Any?
}
