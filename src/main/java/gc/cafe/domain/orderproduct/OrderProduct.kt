package gc.cafe.domain.orderproduct

import gc.cafe.domain.BaseEntity
import gc.cafe.domain.order.Order
import gc.cafe.domain.product.Product
import jakarta.persistence.*


@Entity
@Table(name = "order_items")
class OrderProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    val id: Long = 0,

    @field:JoinColumn(name = "order_id", nullable = false)
    @field:ManyToOne(fetch = FetchType.LAZY)
    val order: Order,

    @field:JoinColumn(name = "product_id", nullable = false)
    @field:ManyToOne(fetch = FetchType.LAZY)
    val product: Product,

    @field:Column(nullable = false)
    var quantity: Int
) : BaseEntity() {

    init {
        order.addOrderProduct(this)
    }

    companion object {
        @JvmStatic
        fun create(order: Order, product: Product, quantity: Int): OrderProduct {
            return OrderProduct(
                order = order,
                product = product,
                quantity = quantity
            )
        }
    }
}
