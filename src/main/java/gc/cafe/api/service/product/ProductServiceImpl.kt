package gc.cafe.api.service.product

import gc.cafe.api.service.product.request.ProductCreateServiceRequest
import gc.cafe.api.service.product.request.ProductUpdateServiceRequest
import gc.cafe.api.service.product.response.ProductResponse
import gc.cafe.domain.product.Product
import gc.cafe.domain.product.ProductRepository
import gc.cafe.global.aop.Trace
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class ProductServiceImpl(private val productRepository: ProductRepository) : ProductService {

    @Trace
    override fun createProduct(request: ProductCreateServiceRequest): ProductResponse {
        val saveProduct = productRepository.save(request.toEntity())
        return ProductResponse.of(saveProduct)
    }

    @Trace
    override fun deleteProduct(id: Long): Long {
        val product = getProductById(id)
        productRepository.delete(product)
        return id
    }

    override fun updateProduct(id: Long, request: ProductUpdateServiceRequest): ProductResponse {
        val product = getProductById(id)
        product.updateProduct(request.name, request.category, request.price, request.description)
        return ProductResponse.of(product)
    }

    @Transactional(readOnly = true)
    override fun getProduct(id: Long): ProductResponse {
        val product = getProductById(id)
        return ProductResponse.of(product)
    }

    private fun getProductById(id: Long): Product {
        return productRepository.findById(id)
            .orElseThrow { IllegalArgumentException("해당 id : " + id + "를 가진 상품을 찾을 수 없습니다.") }
    }
}