package gc.cafe.domain.order

import gc.cafe.domain.BaseEntity
import gc.cafe.domain.orderproduct.OrderProduct
import jakarta.persistence.*

@Table(name = "orders")
@Entity
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    val id: Long = 0,
    @field:Column(nullable = false, length = 50)
    var email: String,

    deliveryAddress: String,

    postcode: String,

    @Embedded
    val address: Address = Address(deliveryAddress, postcode),

    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus = OrderStatus.ORDERED,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val orderProducts: MutableList<OrderProduct> = ArrayList()

) : BaseEntity() {

    fun updateStatus(orderStatus: OrderStatus) {
        this.orderStatus = orderStatus
    }

    fun addOrderProduct(orderProduct: OrderProduct) {
        orderProducts.add(orderProduct)
    }

    companion object {
        fun create(email: String, address: String, postcode: String): Order {
            return Order(
                email = email,
                deliveryAddress = address,
                postcode = postcode
            )
        }
    }
}
