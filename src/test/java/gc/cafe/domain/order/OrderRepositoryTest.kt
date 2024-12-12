package gc.cafe.domain.order

import gc.cafe.IntegrationTestSupport
import gc.cafe.domain.orderproduct.OrderProduct
import gc.cafe.domain.orderproduct.OrderProductRepository
import gc.cafe.domain.product.Product
import gc.cafe.domain.product.ProductRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.util.List

@Transactional
internal class OrderRepositoryTest : IntegrationTestSupport() {
    @Autowired
    private val orderRepository: OrderRepository? = null

    @Autowired
    private val productRepository: ProductRepository? = null

    @Autowired
    private val orderProductRepository: OrderProductRepository? = null

    @DisplayName("이메일을 통해 주문 목록을 조회한다.")
    @Test
    fun findOrdersByEmail() {
        //given
        val email = "test@gmail.com"

        val product1 = Product.create("스타벅스 원두", "원두", 50000L, "에티오피아산")
        val product2 = Product.create("스타벅스 라떼", "음료", 3000L, "에스프레소")
        val product3 = Product.create("스타벅스 베이글", "베이커리", 5000L, "베이글")
        val products = productRepository!!.saveAll(List.of(product1, product2, product3))

        val order1 = Order.create(email, "서울시 강남구", "125454")
        val order2 = Order.create(email, "서울시 강남구", "125454")
        orderRepository!!.saveAll(List.of(order1, order2))

        val orderProduct1 = createOrderProduct(order1, product1, 1)
        val orderProduct2 = createOrderProduct(order1, product2, 2)
        val orderProduct3 = createOrderProduct(order2, product2, 2)
        val orderProduct4 = createOrderProduct(order2, product3, 4)
        orderProductRepository!!.saveAll(List.of(orderProduct1, orderProduct2, orderProduct3, orderProduct4))

        //when
        val findOrdersByEmail = orderRepository.findByEmail(email)

        //then
        Assertions.assertThat(findOrdersByEmail).hasSize(2)
            .extracting("email", "address.address", "address.postcode", "orderStatus")
            .containsExactlyInAnyOrder(
                Assertions.tuple("test@gmail.com", "서울시 강남구", "125454", OrderStatus.ORDERED),
                Assertions.tuple("test@gmail.com", "서울시 강남구", "125454", OrderStatus.ORDERED)
            )
        Assertions.assertThat(findOrdersByEmail[0].orderProducts).hasSize(2)
            .extracting("quantity")
            .contains(1, 2)
        Assertions.assertThat(findOrdersByEmail[1].orderProducts).hasSize(2)
            .extracting("quantity")
            .contains(2, 4)
    }

    @DisplayName("이메일을 통해 주문 목록을 조회할 때 일치하는 이메일이 없으면 빈 목록을 반환한다.")
    @Test
    fun findOrdersByEmailWhenNotFoundEmail() {
        //given
        val email = "test@gmail.com"

        val product1 = Product.create("스타벅스 원두", "원두", 50000L, "에티오피아산")
        val product2 = Product.create("스타벅스 라떼", "음료", 3000L, "에스프레소")
        val product3 = Product.create("스타벅스 베이글", "베이커리", 5000L, "베이글")
        val products = productRepository!!.saveAll(List.of(product1, product2, product3))

        val order1 = Order.create(email, "서울시 강남구", "125454")
        val order2 = Order.create(email, "서울시 강남구", "125454")
        orderRepository!!.saveAll(List.of(order1, order2))

        val orderProduct1 = createOrderProduct(order1, product1, 1)
        val orderProduct2 = createOrderProduct(order1, product2, 2)
        val orderProduct3 = createOrderProduct(order2, product2, 2)
        val orderProduct4 = createOrderProduct(order2, product3, 4)
        orderProductRepository!!.saveAll(List.of(orderProduct1, orderProduct2, orderProduct3, orderProduct4))

        //when
        val findOrdersByEmail = orderRepository.findByEmail("h@d.c")

        //then
        Assertions.assertThat(findOrdersByEmail).hasSize(0)
    }

    @DisplayName("주문 상태로 주문 목록을 조회한다.")
    @Test
    fun findOrdersByOrderStatus() {
        //given
        val product1 = Product.create("스타벅스 원두", "원두", 50000L, "에티오피아산")
        val product2 = Product.create("스타벅스 라떼", "음료", 3000L, "에스프레소")
        val product3 = Product.create("스타벅스 베이글", "베이커리", 5000L, "베이글")

        val products = productRepository!!.saveAll(List.of(product1, product2, product3))
        val order1 = Order.create("test@gmail.com", "서울시 강남구", "125454")
        val order2 = Order.create("test@gmail.com", "서울시 강남구", "125454")


        orderRepository!!.saveAll(List.of(order1, order2))

        val orderProduct1 = createOrderProduct(order1, product1, 1)
        val orderProduct2 = createOrderProduct(order1, product2, 2)
        val orderProduct3 = createOrderProduct(order2, product2, 2)
        val orderProduct4 = createOrderProduct(order2, product3, 4)

        orderProductRepository!!.saveAll(
            List.of(
                orderProduct1,
                orderProduct2,
                orderProduct3,
                orderProduct4
            )
        )

        //when
        val findOrdersByOrderStatus = orderRepository.findByOrderStatus(OrderStatus.ORDERED)
        //then
        Assertions.assertThat(findOrdersByOrderStatus).hasSize(2)
            .extracting("email", "address.address", "address.postcode", "orderStatus")
            .containsExactlyInAnyOrder(
                Assertions.tuple("test@gmail.com", "서울시 강남구", "125454", OrderStatus.ORDERED),
                Assertions.tuple("test@gmail.com", "서울시 강남구", "125454", OrderStatus.ORDERED)
            )

        Assertions.assertThat(findOrdersByOrderStatus[0].orderProducts).hasSize(2)
            .extracting("quantity")
            .contains(
                1, 2
            )

        Assertions.assertThat(findOrdersByOrderStatus[1].orderProducts).hasSize(2)
            .extracting("quantity")
            .containsExactlyInAnyOrder(
                2, 4
            )
    }

    private fun createOrderProduct(order: Order, product: Product, quantity: Int): OrderProduct {
        return OrderProduct(order = order, product = product, quantity = quantity)
    }
}