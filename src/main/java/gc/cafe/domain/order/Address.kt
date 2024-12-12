package gc.cafe.domain.order

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Address(
    @field:Column(nullable = false, length = 200)
    var address: String,

    @field:Column(nullable = false, length = 20)
    var postcode: String
)
