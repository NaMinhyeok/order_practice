package gc.cafe.api.service.product

import gc.cafe.api.service.product.request.ProductCreateServiceRequest
import gc.cafe.api.service.product.request.ProductUpdateServiceRequest
import gc.cafe.api.service.product.response.ProductResponse

interface ProductService {
    fun createProduct(request: ProductCreateServiceRequest): ProductResponse

    fun deleteProduct(id: Long): Long

    fun updateProduct(id: Long, request: ProductUpdateServiceRequest): ProductResponse

    fun getProduct(id: Long): ProductResponse
}
