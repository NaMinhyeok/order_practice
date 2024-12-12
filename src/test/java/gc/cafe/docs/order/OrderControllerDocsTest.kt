package gc.cafe.docs.order

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import gc.cafe.api.controller.order.OrderController
import gc.cafe.api.controller.order.request.OrderCreateRequest
import gc.cafe.api.controller.order.request.OrderProductQuantity
import gc.cafe.api.service.order.OrderService
import gc.cafe.api.service.order.request.OrderCreateServiceRequest
import gc.cafe.api.service.order.response.OrderDetailResponse
import gc.cafe.api.service.order.response.OrderResponse
import gc.cafe.docs.RestDocsConfig
import gc.cafe.docs.RestDocsSupport
import gc.cafe.domain.order.OrderStatus
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito
import org.mockito.BDDMockito.*
import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class OrderControllerDocsTest : RestDocsSupport() {
    private val orderService: OrderService = Mockito.mock(OrderService::class.java)

    override fun initController(): Any? {
        return OrderController(orderService)
    }

    @DisplayName("신규 주문을 생성하는 API")
    @Test
    @Throws(Exception::class)
    fun createOrder() {

        val request = OrderCreateRequest(
            "test@gamil.com",
            "서울시 강남구",
            "125454",
            listOf(
                OrderProductQuantity(
                    1L,
                    1
                ),
                OrderProductQuantity(
                    2L,
                    2
                )
            )
        )

        given<OrderResponse>(
            orderService.createOrder(
                any<OrderCreateServiceRequest>(
                    OrderCreateServiceRequest::class.java
                )
            )
        )
            .willReturn(
                OrderResponse(
                    1L,
                    "test@gmail.com",
                    "서울시 강남구",
                    "125454",
                    OrderStatus.ORDERED,
                    listOf(
                        OrderDetailResponse(
                            "원두",
                            1000L,
                            1
                        ),
                        OrderDetailResponse(
                            "음료",
                            2000L,
                            2
                        )
                    )
                )
            )

        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                document(
                    "order-create",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING)
                            .description("주문자 이메일")
                            .attributes(RestDocsConfig.field("constraints", "최대 50자"))
                            .attributes(RestDocsConfig.field("format", "XXXX@gamil.com 과 같은 이메일 형식")),
                        PayloadDocumentation.fieldWithPath("address").type(JsonFieldType.STRING)
                            .description("주문자 주소")
                            .attributes(RestDocsConfig.field("constraints", "최대 200자")),
                        PayloadDocumentation.fieldWithPath("postcode").type(JsonFieldType.STRING)
                            .description("주문자 우편번호")
                            .attributes(RestDocsConfig.field("constraints", "최대 20자")),
                        PayloadDocumentation.fieldWithPath("orderProductsQuantity").type(JsonFieldType.ARRAY)
                            .description("주문 상품 목록"),
                        PayloadDocumentation.fieldWithPath("orderProductsQuantity[].productId")
                            .type(JsonFieldType.NUMBER)
                            .description("상품 ID"),
                        PayloadDocumentation.fieldWithPath("orderProductsQuantity[].quantity")
                            .type(JsonFieldType.NUMBER)
                            .description("상품 수량")
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
                            .description("주문 ID"),
                        PayloadDocumentation.fieldWithPath("data.email").type(JsonFieldType.STRING)
                            .description("주문자 이메일"),
                        PayloadDocumentation.fieldWithPath("data.address").type(JsonFieldType.STRING)
                            .description("주문자 주소"),
                        PayloadDocumentation.fieldWithPath("data.postcode").type(JsonFieldType.STRING)
                            .description("주문자 우편번호"),
                        PayloadDocumentation.fieldWithPath("data.orderStatus").type(JsonFieldType.STRING)
                            .description("주문 상태"),
                        PayloadDocumentation.fieldWithPath("data.orderDetails").type(JsonFieldType.ARRAY)
                            .description("주문 상세"),
                        PayloadDocumentation.fieldWithPath("data.orderDetails[].category").type(JsonFieldType.STRING)
                            .description("주문한 상품의 카테고리"),
                        PayloadDocumentation.fieldWithPath("data.orderDetails[].price").type(JsonFieldType.NUMBER)
                            .description("주문한 상품의 가격"),
                        PayloadDocumentation.fieldWithPath("data.orderDetails[].quantity").type(JsonFieldType.NUMBER)
                            .description("주문한 상품의 수량")
                    )
                )
            )
    }

    @Throws(Exception::class)
    @Test
    @DisplayName("주문을 단권 조회하는 API")
    fun getOrder() {
        val id = 1L

        given(orderService.getOrder(id))
            .willReturn(
                OrderResponse(
                    id,
                    "test@gmail.com",
                    "서울시 강남구",
                    "125454",
                    OrderStatus.ORDERED,
                    listOf(
                        OrderDetailResponse(
                            "원두",
                            1000L,
                            1
                        ),
                        OrderDetailResponse(
                            "음료",
                            2000L,
                            2
                        )
                    )
                )
            )

        mockMvc!!.perform(
            RestDocumentationRequestBuilders.get("/api/v1/orders/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                document(
                    "order-get",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("id").description("주문 ID")
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
                            .description("주문 ID"),
                        PayloadDocumentation.fieldWithPath("data.email").type(JsonFieldType.STRING)
                            .description("주문자 이메일"),
                        PayloadDocumentation.fieldWithPath("data.address").type(JsonFieldType.STRING)
                            .description("주문자 주소"),
                        PayloadDocumentation.fieldWithPath("data.postcode").type(JsonFieldType.STRING)
                            .description("주문자 우편번호"),
                        PayloadDocumentation.fieldWithPath("data.orderStatus").type(JsonFieldType.STRING)
                            .description("주문 상태"),
                        PayloadDocumentation.fieldWithPath("data.orderDetails").type(JsonFieldType.ARRAY)
                            .description("주문 상세"),
                        PayloadDocumentation.fieldWithPath("data.orderDetails[].category")
                            .type(JsonFieldType.STRING)
                            .description("주문한 상품의 카테고리"),
                        PayloadDocumentation.fieldWithPath("data.orderDetails[].price").type(JsonFieldType.NUMBER)
                            .description("주문한 상품의 가격"),
                        PayloadDocumentation.fieldWithPath("data.orderDetails[].quantity")
                            .type(JsonFieldType.NUMBER)
                            .description("주문한 상품의 수량")
                    )
                )
            )
    }

    @Throws(Exception::class)
    @Test
    @DisplayName("이메일로 주문목록을 조회하는 API")
    fun getOrdersByEmail() {
        val email = "test@gmail.com"

        given(orderService.getOrdersByEmail(email))
            .willReturn(
                listOf(
                    OrderResponse(
                        1L,
                        email,
                        "서울시 강남구",
                        "125454",
                        OrderStatus.ORDERED,
                        listOf(
                            OrderDetailResponse(
                                "원두",
                                1000L,
                                1
                            ),
                            OrderDetailResponse(
                                "음료",
                                2000L,
                                2
                            )
                        )
                    ),
                    OrderResponse(
                        2L,
                        email,
                        "서울시 강남구",
                        "125454",
                        OrderStatus.ORDERED,
                        listOf(
                            OrderDetailResponse(
                                "원두",
                                1000L,
                                6
                            ),
                            OrderDetailResponse(
                                "음료",
                                2000L,
                                2
                            )
                        )
                    ),
                )
            )

        mockMvc!!.perform(
            RestDocumentationRequestBuilders.get("/api/v1/orders/")
                .param("email", email)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(
                document(
                    "orders-get-by-email",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("email").description("주문자 이메일")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.NUMBER)
                            .description("코드"),
                        PayloadDocumentation.fieldWithPath("status").type(JsonFieldType.STRING)
                            .description("상태"),
                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메시지"),
                        PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.ARRAY)
                            .description("응답 데이터"),
                        PayloadDocumentation.fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                            .description("주문 ID"),
                        PayloadDocumentation.fieldWithPath("data[].email").type(JsonFieldType.STRING)
                            .description("주문자 이메일"),
                        PayloadDocumentation.fieldWithPath("data[].address").type(JsonFieldType.STRING)
                            .description("주문자 주소"),
                        PayloadDocumentation.fieldWithPath("data[].postcode").type(JsonFieldType.STRING)
                            .description("주문자 우편번호"),
                        PayloadDocumentation.fieldWithPath("data[].orderStatus").type(JsonFieldType.STRING)
                            .description("주문 상태"),
                        PayloadDocumentation.fieldWithPath("data[].orderDetails").type(JsonFieldType.ARRAY)
                            .description("주문 상세"),
                        PayloadDocumentation.fieldWithPath("data[].orderDetails[].category")
                            .type(JsonFieldType.STRING)
                            .description("주문한 상품의 카테고리"),
                        PayloadDocumentation.fieldWithPath("data[].orderDetails[].price").type(JsonFieldType.NUMBER)
                            .description("주문한 상품의 가격"),
                        PayloadDocumentation.fieldWithPath("data[].orderDetails[].quantity")
                            .type(JsonFieldType.NUMBER)
                            .description("주문한 상품의 수량")
                    )
                )
            )
    }
}
