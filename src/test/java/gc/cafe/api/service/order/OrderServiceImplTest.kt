package gc.cafe.api.service.order

import gc.cafe.IntegrationTestSupport
import gc.cafe.api.controller.order.request.OrderProductQuantity
import gc.cafe.api.service.order.request.OrderCreateServiceRequest
import gc.cafe.config.AsyncTestConfig
import gc.cafe.domain.order.Order
import gc.cafe.domain.order.OrderRepository
import gc.cafe.domain.order.OrderStatus
import gc.cafe.domain.orderproduct.OrderProduct
import gc.cafe.domain.orderproduct.OrderProductRepository
import gc.cafe.domain.product.Product
import gc.cafe.domain.product.ProductRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import java.util.List

@ContextConfiguration(classes = [AsyncTestConfig::class])
@Transactional
internal class OrderServiceImplTest : IntegrationTestSupport() {
    @Autowired
    private val orderService: OrderServiceImpl? = null

    @Autowired
    private val orderRepository: OrderRepository? = null

    @Autowired
    private val productRepository: ProductRepository? = null

    @Autowired
    private val orderProductRepository: OrderProductRepository? = null


    @DisplayName("상품을 주문한다.")
    @Test
    fun createOrder() {
        //given
        val product1 = Product.create("스타벅스 원두", "원두", 50000L, "에티오피아산")
        val product2 = Product.create("스타벅스 라떼", "음료", 3000L, "에스프레소")

        val givenProducts = productRepository!!.saveAll(List.of(product1, product2))

        val request = OrderCreateServiceRequest(
            email = "test@gmail.com",
            address = "서울시 강남구",
            postcode = "125454",
            orderProductQuantity = List.of(
                OrderProductQuantity(
                    productId = givenProducts[0].id,
                    quantity = 1
                ),
                OrderProductQuantity(
                    productId = givenProducts[1].id,
                    quantity = 2
                )
            )
        )

        //when
        val response = orderService!!.createOrder(request)

        //then
        val orders = orderRepository!!.findAll()
        val orderProducts = orderProductRepository!!.findAll()

        Assertions.assertThat(orders).hasSize(1)
            .extracting("id", "email", "address.address", "address.postcode", "orderStatus")
            .containsExactlyInAnyOrder(
                Assertions.tuple(orders[0].id, "test@gmail.com", "서울시 강남구", "125454", OrderStatus.ORDERED)
            )

        Assertions.assertThat(orderProducts).hasSize(2)
            .extracting("order.id", "product.id", "quantity")
            .containsExactlyInAnyOrder(
                Assertions.tuple(orders[0].id, product1.id, 1),
                Assertions.tuple(orders[0].id, product2.id, 2)
            )

        Assertions.assertThat(response)
            .extracting("id", "email", "address", "postcode", "orderStatus")
            .containsExactlyInAnyOrder(
                response.id, "test@gmail.com", "서울시 강남구", "125454", OrderStatus.ORDERED
            )

        Assertions.assertThat(response.orderDetails)
            .hasSize(2)
            .extracting("category", "price", "quantity")
            .containsExactlyInAnyOrder(
                Assertions.tuple("원두", 50000L, 1),
                Assertions.tuple("음료", 3000L, 2)
            )
    }

    @DisplayName("상품을 주문할 때 주문한 상품을 찾을 수 없는 경우 주문 할 수 없다.")
    @Test
    fun createOrderWhenProductNotFound() {
        //given
        val product1 = Product.create("스타벅스 원두", "원두", 50000L, "에티오피아산")
        val product2 = Product.create("스타벅스 라떼", "음료", 3000L, "에스프레소")

        productRepository!!.saveAll(List.of(product1, product2))

        val request = OrderCreateServiceRequest(
            email = "test@gmail.com",
            address = "서울시 강남구",
            postcode = "125454",
            orderProductQuantity = List.of(
                OrderProductQuantity(
                    productId = -1L,
                    quantity = 1
                ),
                OrderProductQuantity(
                    productId = 2L,
                    quantity = 2
                )
            )
        )

        //when
        //then
        Assertions.assertThatThrownBy { orderService!!.createOrder(request) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("주문 상품 id 중 존재하지 않는 상품이 존재합니다.")
    }


    @Test
    @DisplayName("주문 ID로 주문을 조회한다.")
    fun getOrderByOrderId()
        {
            //given
            val product1 = Product.create("스타벅스 원두", "원두", 50000L, "에티오피아산")
            val product2 = Product.create("스타벅스 라떼", "음료", 3000L, "에스프레소")

            val products =
                productRepository!!.saveAll(
                    List.of(
                        product1,
                        product2
                    )
                )

            val order = Order.create("test@gmail.com", "서울시 강남구", "125454")

            val savedOrder = orderRepository!!.save(order)

            val orderProduct1 = createOrderProduct(order, products[0], 1)
            val orderProduct2 = createOrderProduct(order, products[1], 2)

            orderProductRepository!!.saveAll(List.of(orderProduct1, orderProduct2))

            //when
            val response = orderService!!.getOrder(savedOrder.id)
            //then
            Assertions.assertThat(response)
                .extracting("id", "email", "address", "postcode", "orderStatus")
                .containsExactlyInAnyOrder(
                    savedOrder.id, "test@gmail.com", "서울시 강남구", "125454", OrderStatus.ORDERED
                )

            Assertions.assertThat(response.orderDetails)
                .hasSize(2)
                .extracting("category", "price", "quantity")
                .containsExactlyInAnyOrder(
                    Assertions.tuple("원두", 50000L, 1),
                    Assertions.tuple("음료", 3000L, 2)
                )
        }

    @Test
    @DisplayName("주문 ID를 통해 주문을 조회 할 때 해당 ID의 주문이 존재하지 않을 때 주문을 조회 할 수 없다.")
    fun getOrderByOrderIdWhenOrderIsNull()
         {
            //given
            val orderId = -1L

            //when
            //then
            Assertions.assertThatThrownBy {
                orderService!!.getOrder(
                    orderId
                )
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("해당 주문 id : " + orderId + "를 가진 주문이 존재하지 않습니다.")
        }

    @Test
    @DisplayName("이메일을 통해 주문 목록을 조회한다.")
    fun getOrdersByEmail() {
            //given
            val product1 = Product.create("스타벅스 원두", "원두", 50000L, "에티오피아산")
            val product2 = Product.create("스타벅스 라떼", "음료", 3000L, "에스프레소")

            val products =
                productRepository!!.saveAll(
                    List.of(
                        product1,
                        product2
                    )
                )

            val order1 = Order.create("test@gmail.com", "서울시 강남구", "125454")
            val order2 = Order.create("test@gmail.com", "서울시 강남구", "125454")

            orderRepository!!.saveAll(
                List.of(
                    order1,
                    order2
                )
            )

            val orderProduct1 = createOrderProduct(order1, products[0], 1)
            val orderProduct2 = createOrderProduct(order1, products[1], 2)
            val orderProduct3 = createOrderProduct(order2, products[0], 2)
            val orderProduct4 = createOrderProduct(order2, products[1], 4)

            orderProductRepository!!.saveAll(
                List.of(
                    orderProduct1,
                    orderProduct2,
                    orderProduct3,
                    orderProduct4
                )
            )

            //when
            val response = orderService!!.getOrdersByEmail("test@gmail.com")

            //then
            Assertions.assertThat(response).hasSize(2)
                .extracting("email", "address", "postcode", "orderStatus")
                .containsExactlyInAnyOrder(
                    Assertions.tuple("test@gmail.com", "서울시 강남구", "125454", OrderStatus.ORDERED),
                    Assertions.tuple("test@gmail.com", "서울시 강남구", "125454", OrderStatus.ORDERED)
                )

            Assertions.assertThat(response[0].orderDetails)
                .hasSize(2)
                .extracting("category", "price", "quantity")
                .containsExactlyInAnyOrder(
                    Assertions.tuple("원두", 50000L, 1),
                    Assertions.tuple("음료", 3000L, 2)
                )

            Assertions.assertThat(response[1].orderDetails)
                .hasSize(2)
                .extracting("category", "price", "quantity")
                .containsExactlyInAnyOrder(
                    Assertions.tuple("원두", 50000L, 2),
                    Assertions.tuple("음료", 3000L, 4)
                )
        }

    @DisplayName("매일 14시가 되면 주문 상태를 변경한다.")
    @Test
    fun updateOrderStatusByScheduler() {
        //given
        val product1 = Product.create("스타벅스 원두", "원두", 50000L, "에티오피아산")
        val product2 = Product.create("스타벅스 라떼", "음료", 3000L, "에스프레소")

        val products = productRepository!!.saveAll(List.of(product1, product2))


        val order1 = Order.create("test@gmail.com", "서울시 강남구", "125454")
        val order2 = Order.create("test@gmail.com", "서울시 강남구", "125454")


        orderRepository!!.saveAll(List.of(order1, order2))

        val orderProduct1 = createOrderProduct(order1, products[0], 1)
        val orderProduct2 = createOrderProduct(order1, products[1], 2)
        val orderProduct3 = createOrderProduct(order2, products[0], 2)
        val orderProduct4 = createOrderProduct(order2, products[1], 4)

        orderProductRepository!!.saveAll(
            List.of(
                orderProduct1,
                orderProduct2,
                orderProduct3,
                orderProduct4
            )
        )

        //when
        orderService!!.sendOrder()

        //then
        val orders = orderRepository.findAll()

        Assertions.assertThat(orders).hasSize(2)
            .extracting("orderStatus")
            .containsExactlyInAnyOrder(OrderStatus.DELIVERING, OrderStatus.DELIVERING)
    }


    private fun createOrderProduct(order: Order, product: Product, quantity: Int): OrderProduct {
        return OrderProduct.create(order, product, quantity)
    }
}