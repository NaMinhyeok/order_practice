package gc.cafe.domain.product

import gc.cafe.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "products")
class Product(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    val id: Long = 0,

    @field:Column(name = "product_name", nullable = false, length = 20)
    var name: String,

    @field:Column(nullable = false, length = 50)
    var category: String,

    @field:Column(nullable = false)
    var price: Long,

    @field:Column(nullable = false, length = 500)
    var description: String
) : BaseEntity() {

    fun updateProduct(name: String, category: String, price: Long, description: String) {
        this.name = name
        this.category = category
        this.price = price
        this.description = description
    }

    companion object {
        fun create(name: String, category: String, price: Long, description: String): Product {
            return Product(
                name = name,
                category = category,
                price = price,
                description = description
            )
        }
    }
}
