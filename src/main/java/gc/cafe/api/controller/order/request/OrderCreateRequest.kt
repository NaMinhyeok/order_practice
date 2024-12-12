package gc.cafe.api.controller.order.request

import gc.cafe.api.service.order.request.OrderCreateServiceRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class OrderCreateRequest(
    @Email(message = "이메일 형식이어야 합니다.")
    @Size(max = 50, message = "이메일은 50자 이하여야 합니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    val email: String?,
    @Size(max = 200, message = "주소는 200자 이하여야 합니다.")
    @NotBlank(message = "주소는 필수입니다.")
    val address: String?,
    @Size(max = 20, message = "우편번호는 20자 이하여야 합니다.")
    @NotBlank(message = "우편번호는 필수입니다.")
    val postcode: String?,
    @Valid
    @NotNull(message = "주문 할 상품은 필수입니다.")
    val orderProductsQuantity: List<OrderProductQuantity>?
) {

    fun toServiceRequest(): OrderCreateServiceRequest {
        return OrderCreateServiceRequest(email!!, address!!, postcode!!, orderProductsQuantity!!)
    }

}
