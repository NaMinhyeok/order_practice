package gc.cafe.api.controller.order

import gc.cafe.ControllerTestSupport
import gc.cafe.api.controller.order.request.OrderCreateRequest
import gc.cafe.api.controller.order.request.OrderProductQuantity
import gc.cafe.api.service.order.request.OrderCreateServiceRequest
import gc.cafe.api.service.order.response.OrderDetailResponse
import gc.cafe.api.service.order.response.OrderResponse
import gc.cafe.domain.order.OrderStatus
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

internal class OrderControllerTest : ControllerTestSupport() {
    @DisplayName("신규 주문을 생성한다.")
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

        BDDMockito.given<OrderResponse>(
            orderService!!.createOrder(
                ArgumentMatchers.any<OrderCreateServiceRequest>(
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
                    objectMapper!!.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("201"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value("ORDERED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("test@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.address").value("서울시 강남구"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.postcode").value("125454"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[0].price").value(1000L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[0].quantity").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[0].category").value("원두"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[1].price").value(2000L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[1].quantity").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[1].category").value("음료"))
    }

    @DisplayName("신규 주문을 생성 할 때 이메일은 이메일 형식이어야 한다.")
    @Test
    @Throws(
        Exception::class
    )
    fun createOrderWithEmailForm() {
        val request = OrderCreateRequest(
            "test",
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


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper!!.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이메일 형식이어야 합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 주문을 생성 할 때 이메일은 필수값이다.")
    @Test
    @Throws(Exception::class)
    fun createOrderWithoutEmail() {
        val request = OrderCreateRequest(
            null,
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


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper!!.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이메일은 필수입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 주문을 생성 할 때 이메일은 50자 이하여야 한다.")
    @Test
    @Throws(
        Exception::class
    )
    fun createOrderWhenEmailLengthIsOver50() {
        val request = OrderCreateRequest(
            generateFixedLengthString(50)+"@gmail.com",
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


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper!!.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이메일은 50자 이하여야 합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 주문을 생성 할 때 이메일은 50자 이하여야 한다.")
    @Test
    @Throws(
        Exception::class
    )
    fun createOrderWhenEmailLengthIs0() {
        val request = OrderCreateRequest(
            generateFixedLengthString(40)+"@gamil.com",
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

        BDDMockito.given<OrderResponse>(
            orderService!!.createOrder(
                ArgumentMatchers.any<OrderCreateServiceRequest>(
                    OrderCreateServiceRequest::class.java
                )
            )
        )
            .willReturn(
                OrderResponse(
                    1L,
                    generateFixedLengthString(40)+"@gmail.com",
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
                    objectMapper!!.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("201"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value("ORDERED"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.email").value(generateFixedLengthString(40) + "@gmail.com")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.address").value("서울시 강남구"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.postcode").value("125454"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[0].price").value(1000L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[0].quantity").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[0].category").value("원두"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[1].price").value(2000L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[1].quantity").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[1].category").value("음료"))
    }

    @DisplayName("신규 주문을 생성 할 때 주소는 필수값이다.")
    @Test
    @Throws(Exception::class)
    fun createOrderWithoutAddress() {
        val request = OrderCreateRequest(
            "test@gamil.com",
            null,
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


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper!!.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주소는 필수입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 주문을 생성 할 때 주소는 200자 이하여야 한다.")
    @Test
    @Throws(
        Exception::class
    )
    fun createOrderWhenAddressLengthIsOver200() {
        val request = OrderCreateRequest(
            "test@gamil.com",
            generateFixedLengthString(201),
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


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper!!.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주소는 200자 이하여야 합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 주문을 생성 할 때 주소는 200자 이하여야 한다.")
    @Test
    @Throws(
        Exception::class
    )
    fun createOrderWhenAddressLengthIs200() {
        val request = OrderCreateRequest(
            "test@gamil.com",
            generateFixedLengthString(200),
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

        BDDMockito.given<OrderResponse>(
            orderService!!.createOrder(
                ArgumentMatchers.any<OrderCreateServiceRequest>(
                    OrderCreateServiceRequest::class.java
                )
            )
        )
            .willReturn(
                OrderResponse(
                    1L,
                    "test@gmail.com",
                    generateFixedLengthString(200),
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
                    objectMapper!!.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("201"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value("ORDERED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("test@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.address").value(generateFixedLengthString(200)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.postcode").value("125454"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[0].price").value(1000L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[0].quantity").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[0].category").value("원두"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[1].price").value(2000L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[1].quantity").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[1].category").value("음료"))
    }

    @DisplayName("신규 주문을 생성 할 때 우편번호는 필수값이다.")
    @Test
    @Throws(Exception::class)
    fun createOrderWithoutPostcode() {
        val request = OrderCreateRequest(
            "test@gamil.com",
            "서울시 강남구",
            null,
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


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper!!.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("우편번호는 필수입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 주문을 생성 할 때 우편번호는 20자 이하여야 한다.")
    @Test
    @Throws(
        Exception::class
    )
    fun createOrderWhenPostcodeLengthIsOver20() {
        val request = OrderCreateRequest(
            "test@gamil.com",
            "서울시 강남구",
            generateFixedLengthString(21),
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


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper!!.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("우편번호는 20자 이하여야 합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 주문을 생성 할 때 우편번호는 20자 이하여야 한다.")
    @Test
    @Throws(
        Exception::class
    )
    fun createOrderWhenPostcodeLengthIs200() {
        val request = OrderCreateRequest(
            "test@gamil.com",
            "서울시 강남구",
            generateFixedLengthString(20),
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

        BDDMockito.given<OrderResponse>(
            orderService!!.createOrder(
                ArgumentMatchers.any<OrderCreateServiceRequest>(
                    OrderCreateServiceRequest::class.java
                )
            )
        )
            .willReturn(
                OrderResponse(
                    1L,
                    "test@gmail.com",
                    "서울시 강남구",
                    generateFixedLengthString(20),
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
                    objectMapper!!.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("201"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value("ORDERED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("test@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.address").value("서울시 강남구"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.postcode").value(generateFixedLengthString(20)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[0].price").value(1000L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[0].quantity").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[0].category").value("원두"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[1].price").value(2000L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[1].quantity").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[1].category").value("음료"))
    }

    @DisplayName("신규 주문을 생성 할 때 상품은 필수값이다.")
    @Test
    @Throws(Exception::class)
    fun createOrderWithoutProduct() {
        val request = OrderCreateRequest(
            "test@gamil.com",
            "서울시 강남구",
            "125454",
            null
        )

        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper!!.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문 할 상품은 필수입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 주문을 생성 할 때 상품ID는 필수값이다.")
    @Test
    @Throws(Exception::class)
    fun createOrderWithoutProductId() {
        val request = OrderCreateRequest(
            "test@gamil.com",
            "서울시 강남구",
            "125454",
            listOf(
                OrderProductQuantity(
                    null,
                    1
                ),
                OrderProductQuantity(
                    2L,
                    2
                )
            )
        )


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper!!.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 ID는 필수입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 주문을 생성 할 때 수량은 필수값이다.")
    @Test
    @Throws(Exception::class)
    fun createOrderWithoutProductQuantity() {
        val request = OrderCreateRequest(
            "test@gamil.com",
            "서울시 강남구",
            "125454",
            listOf(
                OrderProductQuantity(
                    1L,
                    null
                ),
                OrderProductQuantity(
                    2L,
                    2
                )
            )
        )


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper!!.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 수량은 1 이상이어야 합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 주문을 생성 할 때 상품 수량은 양의 정수이다.")
    @Test
    @Throws(Exception::class)
    fun createOrderWhenProductQuantityIsZero() {
        val request = OrderCreateRequest(
            "test@gamil.com",
            "서울시 강남구",
            "125454",
            listOf(
                OrderProductQuantity(
                    1L,
                    0
                ),
                OrderProductQuantity(
                    2L,
                    2
                )
            )
        )


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper!!.writeValueAsString(request)
                )
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 수량은 1 이상이어야 합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @Throws(Exception::class)
    @Test
    @DisplayName("주문ID로 주문을 조회한다.")
    fun getOrderByOrderId()
        {
            val pathValue = 1L

            BDDMockito.given<OrderResponse>(orderService!!.getOrder(pathValue))
                .willReturn(
                    OrderResponse(
                        pathValue,
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
                RestDocumentationRequestBuilders.get("/api/v1/orders/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value("ORDERED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("test@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address").value("서울시 강남구"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postcode").value("125454"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[0].price").value(1000L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[0].quantity").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[0].category").value("원두"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[1].price").value(2000L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[1].quantity").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderDetails[1].category").value("음료"))
        }

    @Throws(Exception::class)
    @Test
    @DisplayName("이메일을 통해 주문을 조회한다.")
    fun getOrderByEmail() {
            //given
            val email = "test@gmail.com"

            BDDMockito.given<List<OrderResponse>>(orderService!!.getOrdersByEmail(email))
                .willReturn(
                    java.util.List.of(
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
                        )
                    )
                )

            mockMvc!!.perform(
                RestDocumentationRequestBuilders.get("/api/v1/orders")
                    .param("email", email)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].orderStatus").value("ORDERED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].email").value("test@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].address").value("서울시 강남구"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].postcode").value("125454"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].orderDetails").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].orderDetails[0].price").value(1000L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].orderDetails[0].quantity").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].orderDetails[0].category").value("원두"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].orderDetails[1].price").value(2000L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].orderDetails[1].quantity").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].orderDetails[1].category").value("음료"))
        }


    companion object {
        private fun generateFixedLengthString(length: Int): String {
            return "나".repeat(length)
        }
    }
}