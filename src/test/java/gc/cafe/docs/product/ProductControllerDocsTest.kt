package gc.cafe.docs.product

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import gc.cafe.api.controller.product.ProductController
import gc.cafe.api.controller.product.request.ProductCreateRequest
import gc.cafe.api.controller.product.request.ProductUpdateRequest
import gc.cafe.api.service.product.ProductService
import gc.cafe.api.service.product.request.ProductCreateServiceRequest
import gc.cafe.api.service.product.request.ProductUpdateServiceRequest
import gc.cafe.api.service.product.response.ProductResponse
import gc.cafe.docs.RestDocsConfig
import gc.cafe.docs.RestDocsSupport
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class ProductControllerDocsTest : RestDocsSupport() {
    private val productService: ProductService = Mockito.mock(ProductService::class.java)

    override fun initController(): Any? {
        return ProductController(productService)
    }

    @DisplayName("신규 상품을 등록하는 API")
    @Test
    @Throws(Exception::class)
    fun createProduct() {
        val request = ProductCreateRequest("스타벅스 원두", "원두", 50000L, "에티오피아산")

        BDDMockito.given<ProductResponse>(
            productService.createProduct(
                ArgumentMatchers.any<ProductCreateServiceRequest>(
                    ProductCreateServiceRequest::class.java
                )
            )
        )
            .willReturn(
                ProductResponse(
                    1L,
                    "스타벅스 원두",
                    "원두",
                    50000L,
                    "에티오피아산"
                )
            )

        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/products")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                document(
                    "product-create",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("name").type(JsonFieldType.STRING)
                            .description("상품 이름")
                            .attributes(RestDocsConfig.field("constraints", "최대 20자")),
                        PayloadDocumentation.fieldWithPath("category").type(JsonFieldType.STRING)
                            .description("상품 카테고리")
                            .attributes(RestDocsConfig.field("constraints", "최대 50자")),
                        PayloadDocumentation.fieldWithPath("price").type(JsonFieldType.NUMBER)
                            .description("상품 가격"),
                        PayloadDocumentation.fieldWithPath("description").type(JsonFieldType.STRING)
                            .description("상품 상세 설명")
                            .attributes(RestDocsConfig.field("constraints", "최대 500자"))
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.NUMBER)
                            .description("코드"),
                        PayloadDocumentation.fieldWithPath("status").type(JsonFieldType.STRING)
                            .description("상태"),
                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메시지"),
                        PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("응답 데이터"),
                        PayloadDocumentation.fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                            .description("상품 ID"),
                        PayloadDocumentation.fieldWithPath("data.name").type(JsonFieldType.STRING)
                            .description("상품 이름"),
                        PayloadDocumentation.fieldWithPath("data.category").type(JsonFieldType.STRING)
                            .description("상품 카테고리"),
                        PayloadDocumentation.fieldWithPath("data.price").type(JsonFieldType.NUMBER)
                            .description("상품 가격"),
                        PayloadDocumentation.fieldWithPath("data.description").type(JsonFieldType.STRING)
                            .description("상품 상세 설명")
                    )
                )
            )
    }

    @DisplayName("상품을 삭제하는 API")
    @Test
    @Throws(Exception::class)
    fun deleteProduct() {
        val id = 1L

        BDDMockito.given(productService.deleteProduct(id))
            .willReturn(id)

        mockMvc!!.perform(
            RestDocumentationRequestBuilders.delete("/api/v1/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                document(
                    "product-delete",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id")
                            .description("삭제할 상품 ID")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.NUMBER)
                            .description("코드"),
                        PayloadDocumentation.fieldWithPath("status").type(JsonFieldType.STRING)
                            .description("상태"),
                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메시지"),
                        PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.NUMBER)
                            .description("삭제된 상품 ID")
                    )
                )
            )
    }

    @DisplayName("상품을 수정하는 API")
    @Test
    @Throws(Exception::class)
    fun updateProduct() {
        val id = 1L

        val request = ProductUpdateRequest("스타벅스원두", "원두", 50000L, "에티오피아산")

        BDDMockito.given<ProductResponse>(
            productService.updateProduct(
                ArgumentMatchers.eq(id), ArgumentMatchers.any<ProductUpdateServiceRequest>(
                    ProductUpdateServiceRequest::class.java
                )
            )
        )
            .willReturn(
                ProductResponse(
                    id,
                    "이디야 커피",
                    "커피",
                    40000L,
                    "국산"
                )
            )

        mockMvc!!.perform(
            RestDocumentationRequestBuilders.put("/api/v1/products/{id}", id)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                document(
                    "product-update",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("name").type(JsonFieldType.STRING)
                            .description("상품 이름")
                            .attributes(RestDocsConfig.field("constraints", "최대 20자")),
                        PayloadDocumentation.fieldWithPath("category").type(JsonFieldType.STRING)
                            .description("상품 카테고리")
                            .attributes(RestDocsConfig.field("constraints", "최대 50자")),
                        PayloadDocumentation.fieldWithPath("price").type(JsonFieldType.NUMBER)
                            .description("상품 가격")
                            .attributes(RestDocsConfig.field("constraints", "0 이상")),
                        PayloadDocumentation.fieldWithPath("description").type(JsonFieldType.STRING)
                            .description("상품 상세 설명")
                            .attributes(RestDocsConfig.field("constraints", "최대 500자"))
                    ),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id")
                            .description("수정할 상품 ID")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.NUMBER)
                            .description("코드"),
                        PayloadDocumentation.fieldWithPath("status").type(JsonFieldType.STRING)
                            .description("상태"),
                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메시지"),
                        PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("응답 데이터"),
                        PayloadDocumentation.fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                            .description("상품 ID"),
                        PayloadDocumentation.fieldWithPath("data.name").type(JsonFieldType.STRING)
                            .description("상품 이름"),
                        PayloadDocumentation.fieldWithPath("data.category").type(JsonFieldType.STRING)
                            .description("상품 카테고리"),
                        PayloadDocumentation.fieldWithPath("data.price").type(JsonFieldType.NUMBER)
                            .description("상품 가격"),
                        PayloadDocumentation.fieldWithPath("data.description").type(JsonFieldType.STRING)
                            .description("상품 상세 설명")
                    )
                )
            )
    }

    @DisplayName("상품을 수정하는 API")
    @Test
    @Throws(Exception::class)
    fun getProduct() {
            val id = 1L

            BDDMockito.given<ProductResponse>(productService.getProduct(id))
                .willReturn(
                    ProductResponse(
                        id,
                        "스타벅스 원두",
                        "원두",
                        50000L,
                        "에티오피아산"
                    )
                )


            mockMvc!!.perform(
                RestDocumentationRequestBuilders.get
                    ("/api/v1/products/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(
                    document(
                        "product-get",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        RequestDocumentation.pathParameters(
                            RequestDocumentation.parameterWithName("id")
                                .description("조회할 상품 ID")
                        ),
                        PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.NUMBER)
                                .description("코드"),
                            PayloadDocumentation.fieldWithPath("status").type(JsonFieldType.STRING)
                                .description("상태"),
                            PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("메시지"),
                            PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.OBJECT)
                                .description("응답 데이터"),
                            PayloadDocumentation.fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                .description("상품 ID"),
                            PayloadDocumentation.fieldWithPath("data.name").type(JsonFieldType.STRING)
                                .description("상품 이름"),
                            PayloadDocumentation.fieldWithPath("data.category").type(JsonFieldType.STRING)
                                .description("상품 카테고리"),
                            PayloadDocumentation.fieldWithPath("data.price").type(JsonFieldType.NUMBER)
                                .description("상품 가격"),
                            PayloadDocumentation.fieldWithPath("data.description").type(JsonFieldType.STRING)
                                .description("상품 상세 설명")
                        )
                    )
                )
        }
}
