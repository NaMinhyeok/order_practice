package gc.cafe.domain.orderproduct;

import gc.cafe.domain.BaseEntity;
import gc.cafe.domain.order.Order;
import gc.cafe.domain.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order_items")
public class OrderProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Builder
    private OrderProduct(Order order, Product product, int quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        order.addOrderProduct(this);
    }

    public static OrderProduct create(Order order, Product product, int quantity) {
        return OrderProduct.builder()
            .order(order)
            .product(product)
            .quantity(quantity)
            .build();
    }

}
