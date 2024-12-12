package gc.cafe.domain.product

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class ProductTest {
    @DisplayName("상품을 수정한다.")
    @Test
    fun updateProduct() {
        //given
        val product = Product.create("스타벅스 원두", "원두", 50000L, "에티오피아산")
        //when
        product.updateProduct("스타벅스 라떼", "음료", 3000L, "에스프레소")
        //then
        Assertions.assertThat(product)
            .extracting("name", "category", "price", "description")
            .containsExactlyInAnyOrder("스타벅스 라떼", "음료", 3000L, "에스프레소")
    }
}