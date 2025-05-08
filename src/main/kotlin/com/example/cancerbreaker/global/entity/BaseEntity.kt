package com.example.cancerbreaker.global.entity

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

    var updatedAt: LocalDateTime? = null

    @PreUpdate
    fun setUpdatedAt() {
        updatedAt = LocalDateTime.now()
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BaseEntity

        // Hibernate Proxy 클래스일 경우 실제 클래스 비교
        val thisClass = if (this is HibernateProxy) this.javaClass.superclass else this.javaClass
        val otherClass = if (other is HibernateProxy) other.javaClass.superclass else other.javaClass

        if (thisClass != otherClass) return false

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}