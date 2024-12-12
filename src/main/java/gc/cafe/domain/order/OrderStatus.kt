package gc.cafe.domain.order

import lombok.Getter
import lombok.RequiredArgsConstructor

enum class OrderStatus(
    val description: String
) {
    ORDERED("주문 완료"),
    DELIVERING("배송 진행 중")
    ;
}
