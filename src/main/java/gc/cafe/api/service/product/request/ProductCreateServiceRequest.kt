package gc.cafe.api.service.product.request

import gc.cafe.domain.product.Product

data class ProductCreateServiceRequest(
    val name: String,
    val category: String,
    val price: Long,
    val description: String

) {
    fun toEntity(): Product {
        return Product(
            name = name,
            category = category,
            price = price,
            description = description
        )
    }
}
