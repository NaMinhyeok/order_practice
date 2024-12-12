package gc.cafe.domain.order

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    fun findByEmail(email: String?): List<Order>

    fun findByOrderStatus(orderStatus: OrderStatus?): List<Order>
}
