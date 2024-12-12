package gc.cafe.api.service.product

import gc.cafe.IntegrationTestSupport
import gc.cafe.api.service.product.request.ProductCreateServiceRequest
import gc.cafe.api.service.product.request.ProductUpdateServiceRequest
import gc.cafe.domain.product.Product
import gc.cafe.domain.product.ProductRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@Transactional
internal class ProductServiceImplTest : IntegrationTestSupport() {
    @Autowired
    private val productService: ProductServiceImpl? = null

    @Autowired
    private val productRepository: ProductRepository? = null

    @DisplayName("신규 상품을 등록한다.")
    @Test
    fun createProductTest() {
        //given
        val request: ProductCreateServiceRequest =         ProductCreateServiceRequest("스타벅스 원두", "원두", 50000L, "에티오피아산")


        //when
        val productResponse = productService!!.createProduct(request)

        val products = productRepository!!.findAll()

        //then
        Assertions.assertThat(products).hasSize(1)
            .extracting("id", "name", "category", "price", "description")
            .containsExactlyInAnyOrder(
                Assertions.tuple(products[0].id, "스타벅스 원두", "원두", 50000L, "에티오피아산")
            )

        Assertions.assertThat(productResponse)
            .extracting("id", "name", "category", "price", "description")
            .containsExactlyInAnyOrder(products[0].id, "스타벅스 원두", "원두", 50000L, "에티오피아산")
    }

    @Test
    @DisplayName("상품 ID를 통해 상품에 대한 상세정보를 조회한다.")
    fun getProductByProductId() {
            //given
            val product = createProduct("스타벅스 원두", "원두", 50000L, "에티오피아산")

            val savedProduct =
                productRepository!!.save(product)

            //when
            val productResponse = productService!!.getProduct(savedProduct.id)

            //then
            Assertions.assertThat(productResponse)
                .extracting("id", "name", "category", "price", "description")
                .containsExactlyInAnyOrder(savedProduct.id, "스타벅스 원두", "원두", 50000L, "에티오피아산")
        }

    @Test
    @DisplayName("상품 ID를 통해 상품에 대한 상세정보를 조회 할 때 해당 ID의 상품이 존재하지 않을 때 상품을 조회 할 수 없다.")
    fun getProductByProductIdWhenProductIsNull() {
            //given
            val productId = 1L

            //when
            //then
            Assertions.assertThatThrownBy {
                productService!!.getProduct(
                    productId
                )
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("해당 id : " + productId + "를 가진 상품을 찾을 수 없습니다.")
        }

    @DisplayName("상품 ID를 통해 해당 상품을 삭제 할 수 있다.")
    @Test
    fun deleteProductByProductId() {
        //given=
        val product = createProduct("스타벅스 원두", "원두", 50000L, "에티오피아산")

        val savedProduct = productRepository!!.save(product)

        //when
        val deletedProductId = productService!!.deleteProduct(savedProduct.id)
        val products = productRepository.findAll()

        //then
        Assertions.assertThat(products).hasSize(0)
        Assertions.assertThat(deletedProductId).isEqualTo(savedProduct.id)
    }

    @DisplayName("상품 ID를 통해 해당 상품을 삭제 할 때 해당 상품이 존재하지 않으면 상품을 삭제 할 수 없다.")
    @Test
    fun deleteProductByProductIdWhenProductIsNull() {
        //given
        val productId = 1L

        //when
        //then
        Assertions.assertThatThrownBy { productService!!.deleteProduct(productId) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("해당 id : " + productId + "를 가진 상품을 찾을 수 없습니다.")
    }

    @DisplayName("상품 ID를 통해 해당 상품의 정보를 수정 할 수 있다.")
    @Test
    fun updateProductByProductId() {
        //given
        val product = createProduct("스타벅스 원두", "원두", 50000L, "에티오피아산")

        val savedProduct = productRepository!!.save(product)

        val request: ProductUpdateServiceRequest =         ProductUpdateServiceRequest("이디야 커피", "커피", 40000L, "국산")


        //when
        val response = productService!!.updateProduct(savedProduct.id, request)

        //then
        Assertions.assertThat(response)
            .extracting("id", "name", "category", "price", "description")
            .containsExactlyInAnyOrder(savedProduct.id, "이디야 커피", "커피", 40000L, "국산")
    }

    @DisplayName("상품 ID를 통해 해당 상품의 정보를 수정 할 때 해당 상품이 존재하지 않으면 상품을 삭제 할 수 없다.")
    @Test
    fun updateProductByProductIdWhenProductIsNull() {
        //given
        val productId = 1L

        val request: ProductUpdateServiceRequest =         ProductUpdateServiceRequest("이디야 커피", "커피", 40000L, "국산")


        //when
        //then
        Assertions.assertThatThrownBy { productService!!.updateProduct(productId, request) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("해당 id : " + productId + "를 가진 상품을 찾을 수 없습니다.")
    }

    private fun createProduct(name: String, category: String, price: Long, description: String): Product {
        return Product(
            name = name,
            category = category,
            price = price,
            description = description
        )
    }
}