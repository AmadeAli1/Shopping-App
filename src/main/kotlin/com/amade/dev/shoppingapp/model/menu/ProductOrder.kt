package com.amade.dev.shoppingapp.model.menu

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.sql.Timestamp
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

@Table("ProductOrder")
data class ProductOrder(
    @Id @Column("uid") val uid: UUID? = null,
    @field:NotNull @field:Positive val unitPrice: Float,
    @field:NotNull @field:Positive val quantity: Int,
    @field:JsonProperty(access = JsonProperty.Access.READ_ONLY) val finalPrice: Float = unitPrice * quantity,
    @field:JsonIgnore @Column("date") val date: Timestamp = Timestamp.from(Instant.now()),
    @field:NotNull @Column("orderType") val orderType: OrderType,
    @field:NotNull @Column("orderState") val orderState: OrderState,
    @field:NotBlank @Column("userId") val userId: String,
    @field:NotNull @Column("productId") val productId: UUID,
    @field:NotNull @Column("companyId") val companyId: UUID,
) {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    fun getTime(): String {
        val localDateTime = date.toLocalDateTime()
        val timeFormatter =
            DateTimeFormatter.ofPattern(/* pattern = year-month-day:hour-minute-second */ "uuuu-MM-dd'T'HH:mm:ss")
        return localDateTime.format(timeFormatter)
    }

    enum class OrderState {
        Complete,
        Received,
        Submitted,
        Preparing
    }

    enum class OrderType {
        Delivery,
        InLocal
    }

}

@Table("ProductCart")
data class ProductCart(
    @Id @Column("id") val id: Int? = null,
    @field:NotBlank @Column("userId") val userId: String,
    @field:NotNull @Column("productId") val productId: UUID,
)