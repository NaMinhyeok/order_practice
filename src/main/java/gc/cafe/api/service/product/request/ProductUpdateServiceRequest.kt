package gc.cafe.api.service.product.request

data class ProductUpdateServiceRequest(
    val name: String,
    val category: String,
    val price: Long,
    val description: String
)
