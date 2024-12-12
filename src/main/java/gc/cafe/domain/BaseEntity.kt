package gc.cafe.domain

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime? = null
        protected set  // JPA만 수정할 수 있도록

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
        protected set  // JPA만 수정할 수 있도록
}