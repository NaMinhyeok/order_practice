package gc.cafe.api.controller.product.request

import gc.cafe.api.service.product.request.ProductCreateServiceRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class ProductCreateRequest(
    @Size(max = 20, message = "상품명은 20자 이하여야 합니다.")
    @NotBlank(message = "상품명은 필수입니다.")
    val name: String?,
    @Size(max = 50, message = "카테고리는 50자 이하여야 합니다.")
    @NotBlank(message = "카테고리는 필수입니다.")
    val category: String?,
    @Positive(message = "가격은 양수이어야 합니다.") @NotNull(message = "가격은 필수입니다.")
    val price: Long?,
    @Size(max = 500, message = "상품 설명은 500자 이하여야 합니다.")
    @NotBlank(message = "상품 설명은 필수입니다.")
    val description: String?
) {
    fun toServiceRequest(): ProductCreateServiceRequest {
        return ProductCreateServiceRequest(
            name = name!!,
            category = category!!,
            price = price!!,
            description = description!!
        )
    }
}
