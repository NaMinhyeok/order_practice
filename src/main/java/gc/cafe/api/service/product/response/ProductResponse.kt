package gc.cafe.api.service.product.response

import gc.cafe.domain.product.Product

data class ProductResponse(
    val id: Long,
    val name: String,
    val category: String,
    val price: Long,
    val description: String
) {
    companion object {
        fun of(product: Product): ProductResponse {
            return ProductResponse(product.id, product.name, product.category, product.price, product.description)
        }
    }
}
