package gc.cafe.api.controller.product

import gc.cafe.ControllerTestSupport
import gc.cafe.api.controller.product.request.ProductCreateRequest
import gc.cafe.api.controller.product.request.ProductUpdateRequest
import gc.cafe.api.service.product.request.ProductCreateServiceRequest
import gc.cafe.api.service.product.request.ProductUpdateServiceRequest
import gc.cafe.api.service.product.response.ProductResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

internal class ProductControllerTest : ControllerTestSupport() {
    @DisplayName("신규 상품을 등록한다.")
    @Test
    @Throws(Exception::class)
    fun createProduct() {
        val request: ProductCreateRequest = ProductCreateRequest("스타벅스 원두", "원두", 50000L, "에티오피아산")


        BDDMockito.given<ProductResponse>(
            productService!!.createProduct(
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
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("201"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("스타벅스 원두"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.category").value("원두"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.price").value(50000L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").value("에티오피아산"))
    }

    @DisplayName("신규 상품을 등록 할 때 이름은 필수값이다.")
    @Test
    @Throws(Exception::class)
    fun createProductWithoutName() {
        val request: ProductCreateRequest = ProductCreateRequest(null, "원두", 50000L, "에티오피아산")


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/products")
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품명은 필수입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 상품을 등록 할 때 이름은 최대 20자이다.")
    @Test
    @Throws(Exception::class)
    fun createProductWhenNameLengthIsOver20() {
        val request: ProductCreateRequest = ProductCreateRequest(generateFixedLengthString(21), "원두", 50000L, "에티오피아산")


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/products")
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품명은 20자 이하여야 합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 상품을 등록 할 때 이름은 최대 20자이다.")
    @Test
    @Throws(Exception::class)
    fun createProductWhenNameLengthIs20() {
        val request: ProductCreateRequest = ProductCreateRequest(generateFixedLengthString(20), "원두", 50000L, "에티오피아산")


        BDDMockito.given<ProductResponse>(
            productService!!.createProduct(
                ArgumentMatchers.any<ProductCreateServiceRequest>(
                    ProductCreateServiceRequest::class.java
                )
            )
        )
            .willReturn(
                ProductResponse(
                    1L,
                    generateFixedLengthString(20),
                    "원두",
                    50000L,
                    "에티오피아산"
                )
            )

        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/products")
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("201"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.category").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.price").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").isString())
    }

    @DisplayName("신규 상품을 등록 할 때 카테고리는 필수값이다.")
    @Test
    @Throws(Exception::class)
    fun createProductWithoutCategory() {
        val request: ProductCreateRequest = ProductCreateRequest("스타벅스 원두", null, 50000L, "에티오피아산")


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/products")
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("카테고리는 필수입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 상품을 등록 할 때 카테고리의 길이는 최대 50자이다.")
    @Test
    @Throws(
        Exception::class
    )
    fun createProductWhenCategoryLengthIs50() {
        val request: ProductCreateRequest =
            ProductCreateRequest("스타벅스 원두", generateFixedLengthString(51), 50000L, "에티오피아산")


        BDDMockito.given<ProductResponse>(
            productService!!.createProduct(
                ArgumentMatchers.any<ProductCreateServiceRequest>(
                    ProductCreateServiceRequest::class.java
                )
            )
        )
            .willReturn(
                ProductResponse(
                    1L,
                    "스타벅스 원두",
                    generateFixedLengthString(50),
                    50000L,
                    "에티오피아산"
                )
            )

        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/products")
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("201"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.category").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.price").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").isString())
    }

    @DisplayName("신규 상품을 등록 할 때 카테고리의 길이는 최대 50자이다.")
    @Test
    @Throws(
        Exception::class
    )
    fun createProductWhenCategoryLengthIsOver50() {
        val request: ProductCreateRequest =
            ProductCreateRequest("스타벅스 원두", generateFixedLengthString(51), 50000L, "에티오피아산")


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/products")
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("카테고리는 50자 이하여야 합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 상품을 등록 할 때 상품 설명은 필수값이다.")
    @Test
    @Throws(Exception::class)
    fun createProductWithoutDescription() {
        val request: ProductCreateRequest = ProductCreateRequest("스타벅스 원두", "원두", 50000L, null)


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/products")
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 설명은 필수입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 상품을 등록 할 때 상품 설명의 길이는 최대 500자이다.")
    @Test
    @Throws(
        Exception::class
    )
    fun createProductWhenDescriptionLengthIs500() {
        val request: ProductCreateRequest =
            ProductCreateRequest("스타벅스 원두", "원두", 50000L, generateFixedLengthString(500))


        BDDMockito.given<ProductResponse>(
            productService!!.createProduct(
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
                    generateFixedLengthString(500)
                )
            )

        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/products")
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("201"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.category").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.price").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").isString())
    }

    @DisplayName("신규 상품을 등록 할 때 상품 설명의 길이는 최대 500자이다")
    @Test
    @Throws(
        Exception::class
    )
    fun createProductWhenDescriptionLengthIsOver500() {
        val request: ProductCreateRequest =
            ProductCreateRequest("스타벅스 원두", "원두", 50000L, generateFixedLengthString(501))


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/products")
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 설명은 500자 이하여야 합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 상품을 등록 할 때 가격은 필수값이다.")
    @Test
    @Throws(Exception::class)
    fun createProductWithoutPrice() {
        val request: ProductCreateRequest = ProductCreateRequest("스타벅스 원두", "원두", null, "에티오피아산")


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/products")
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("가격은 필수입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("신규 상품을 등록 할 때 상품 가격은 양수이어야 한다.")
    @Test
    @Throws(Exception::class)
    fun createProductWhenPriceIsOverZero() {
        val request: ProductCreateRequest = ProductCreateRequest("스타벅스 원두", "원두", 1L, "에티오피아산")


        BDDMockito.given<ProductResponse>(
            productService!!.createProduct(
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
                    1L,
                    "에티오피아산"
                )
            )

        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/products")
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("201"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("CREATED"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.category").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.price").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").isString())
    }

    @DisplayName("신규 상품을 등록 할 때 상품 가격은 0이 아니어야 한다.")
    @Test
    @Throws(
        Exception::class
    )
    fun createProductWhenPriceIsZero() {
        val request: ProductCreateRequest = ProductCreateRequest("스타벅스 원두", "원두", 0L, "에티오피아산")


        mockMvc!!.perform(
            RestDocumentationRequestBuilders.post("/api/v1/products")
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("가격은 양수이어야 합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @Throws(Exception::class)
    @Test
    @DisplayName("상품 ID를 통해 상품에 대한 상세정보를 조회한다.")
    fun getProductByProductId() {
        //given
        val pathValue = 1L

        BDDMockito.given<ProductResponse>(productService!!.getProduct(pathValue))
            .willReturn(
                ProductResponse(
                    pathValue,
                    "스타벅스 원두",
                    "원두",
                    50000L,
                    "에티오피아산"
                )
            )

        //when
        //then
        mockMvc!!.perform(
            MockMvcRequestBuilders.get("/api/v1/products/{id}", pathValue)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.category").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.price").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").isString())
    }

    @DisplayName("상품 ID를 통해 해당 상품을 삭제한다.")
    @Test
    @Throws(Exception::class)
    fun deleteProductByProductId() {
        //given
        val id = 1L

        BDDMockito.given(productService!!.deleteProduct(id))
            .willReturn(id)

        //when
        //then
        mockMvc!!.perform(
            MockMvcRequestBuilders.delete("/api/v1/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNumber())
    }

    @DisplayName("상품 ID를 통해 상품정보를 변경한다.")
    @Test
    @Throws(Exception::class)
    fun updateProduct() {
        val productId = 1L

        val request: ProductUpdateRequest = ProductUpdateRequest("이디야 커피", "커피", 40000L, "국산")


        BDDMockito.given<ProductResponse>(
            productService!!.updateProduct(
                ArgumentMatchers.eq<Long>(productId), ArgumentMatchers.any<ProductUpdateServiceRequest>(
                    ProductUpdateServiceRequest::class.java
                )
            )
        )
            .willReturn(
                ProductResponse(
                    productId,
                    "이디야 커피",
                    "커피",
                    40000L,
                    "국산"
                )
            )

        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/products/{id}", productId)
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.category").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.price").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").isString())
    }

    @DisplayName("상품 ID를 통해 상품정보를 변경 할 때 이름은 필수값이다.")
    @Test
    @Throws(
        Exception::class
    )
    fun updateProductWithoutName() {
        val productId = 1L

        val request: ProductUpdateRequest = ProductUpdateRequest(null, "커피", 40000L, "국산")


        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/products/{id}", productId)
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품명은 필수입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("상품 ID를 통해 상품정보를 변경 할 때 이름은 최대 20자이다.")
    @Test
    @Throws(
        Exception::class
    )
    fun updateProductWhenNameLengthIsOver20() {
        val productId = 1L

        val request: ProductUpdateRequest = ProductUpdateRequest(generateFixedLengthString(21), "커피", 40000L, "국산")


        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/products/{id}", productId)
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품명은 20자 이하여야 합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("상품 ID를 통해 상품정보를 변경 할 때 이름은 최대 20자이다.")
    @Test
    @Throws(
        Exception::class
    )
    fun updateProductWhenNameLengthIs20() {
        val productId = 1L

        val request: ProductUpdateRequest = ProductUpdateRequest(generateFixedLengthString(20), "커피", 40000L, "국산")


        BDDMockito.given<ProductResponse>(
            productService!!.updateProduct(
                ArgumentMatchers.eq<Long>(productId), ArgumentMatchers.any<ProductUpdateServiceRequest>(
                    ProductUpdateServiceRequest::class.java
                )
            )
        )
            .willReturn(
                ProductResponse(
                    productId,
                    generateFixedLengthString(20),
                    "커피",
                    40000L,
                    "국산"
                )
            )

        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/products/{id}", productId)
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.category").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.price").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").isString())
    }

    @DisplayName("상품 ID를 통해 상품정보를 변경 할 때 카테고리는 필수값이다.")
    @Test
    @Throws(
        Exception::class
    )
    fun updateProductWithoutCategory() {
        val productId = 1L

        val request: ProductUpdateRequest = ProductUpdateRequest("이디야 커피", null, 40000L, "국산")


        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/products/{id}", productId)
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("카테고리는 필수입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("상품 ID를 통해 상품정보를 변경 할 때 카테고리의 길이는 최대 50자이다.")
    @Test
    @Throws(
        Exception::class
    )
    fun updateProductWhenCategoryLengthIs50() {
        val productId = 1L

        val request: ProductUpdateRequest = ProductUpdateRequest("이디야 커피", generateFixedLengthString(50), 40000L, "국산")


        BDDMockito.given<ProductResponse>(
            productService!!.updateProduct(
                ArgumentMatchers.eq<Long>(productId), ArgumentMatchers.any<ProductUpdateServiceRequest>(
                    ProductUpdateServiceRequest::class.java
                )
            )
        )
            .willReturn(
                ProductResponse(
                    productId,
                    "이디야 커피",
                    generateFixedLengthString(50),
                    40000L,
                    "국산"
                )
            )

        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/products/{id}", productId)
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.category").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.price").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").isString())
    }

    @DisplayName("상품 ID를 통해 상품정보를 변경 할 때 카테고리의 길이는 최대 50자이다.")
    @Test
    @Throws(
        Exception::class
    )
    fun updateProductWhenCategoryLengthIsOver50() {
        val productId = 1L

        val request: ProductUpdateRequest = ProductUpdateRequest("이디야 커피", generateFixedLengthString(51), 40000L, "국산")


        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/products/{id}", productId)
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("카테고리는 50자 이하여야 합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("상품 ID를 통해 상품정보를 변경 할 때 상품 설명은 필수값이다.")
    @Test
    @Throws(
        Exception::class
    )
    fun updateProductWithoutDescription() {
        val productId = 1L

        val request: ProductUpdateRequest = ProductUpdateRequest("이디야 커피", "커피", 40000L, null)


        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/products/{id}", productId)
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 설명은 필수입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("상품 ID를 통해 상품정보를 변경 할 때 상품 설명의 길이는 최대 500자이다.")
    @Test
    @Throws(
        Exception::class
    )
    fun updateProductWhenDescriptionLengthIs500() {
        val productId = 1L


        val request: ProductUpdateRequest = ProductUpdateRequest("이디야 커피", "커피", 40000L, generateFixedLengthString(500))


        BDDMockito.given<ProductResponse>(
            productService!!.updateProduct(
                ArgumentMatchers.eq<Long>(productId), ArgumentMatchers.any<ProductUpdateServiceRequest>(
                    ProductUpdateServiceRequest::class.java
                )
            )
        )
            .willReturn(
                ProductResponse(
                    productId,
                    "이디야 커피",
                    "커피",
                    40000L,
                    generateFixedLengthString(500)
                )
            )

        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/products/{id}", productId)
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.category").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.price").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").isString())
    }

    @DisplayName("상품 ID를 통해 상품정보를 변경 할 때 상품 설명의 길이는 최대 500자이다")
    @Test
    @Throws(
        Exception::class
    )
    fun updateProductWhenDescriptionLengthIsOver500() {
        val productId = 1L

        val request: ProductUpdateRequest = ProductUpdateRequest("이디야 커피", "커피", 40000L, generateFixedLengthString(501))


        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/products/{id}", productId)
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상품 설명은 500자 이하여야 합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("상품 ID를 통해 상품정보를 변경 할 때 가격은 필수값이다.")
    @Test
    @Throws(
        Exception::class
    )
    fun updateProductWithoutPrice() {
        val productId = 1L

        val request: ProductUpdateRequest = ProductUpdateRequest("이디야 커피", "커피", null, "국산")

        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/products/{id}", productId)
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("가격은 필수입니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    @DisplayName("상품 ID를 통해 상품정보를 변경 할 때 상품 가격은 양수이어야 한다.")
    @Test
    @Throws(
        Exception::class
    )
    fun updateProductWhenPriceIsOverZero() {
        val productId = 1L

        val request: ProductUpdateRequest = ProductUpdateRequest("이디야 커피", "커피", 1L, "국산")

        BDDMockito.given<ProductResponse>(
            productService!!.updateProduct(
                ArgumentMatchers.eq<Long>(productId), ArgumentMatchers.any<ProductUpdateServiceRequest>(
                    ProductUpdateServiceRequest::class.java
                )
            )
        )
            .willReturn(
                ProductResponse(
                    productId,
                    "이디야 커피",
                    "커피",
                    1L,
                    "국산"
                )
            )

        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/products/{id}", productId)
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("OK"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.category").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.price").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").isString())
    }

    @DisplayName("상품 ID를 통해 상품정보를 변경 할 때 상품 가격은 0이 아니어야 한다.")
    @Test
    @Throws(
        Exception::class
    )
    fun updateProductWhenPriceIsZero() {
        val productId = 1L

        val request = ProductUpdateRequest("이디야 커피", "커피", 0L, "국산")


        mockMvc!!.perform(
            MockMvcRequestBuilders.put("/api/v1/products/{id}", productId)
                .content(objectMapper!!.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BAD_REQUEST"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("가격은 양수이어야 합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
    }

    private fun create(name: String, category: String, price: Long, description: String): ProductCreateRequest {
        return ProductCreateRequest(name, category, price, description)
    }

    companion object {
        private fun generateFixedLengthString(length: Int): String {
            return "나".repeat(length)
        }
    }
}