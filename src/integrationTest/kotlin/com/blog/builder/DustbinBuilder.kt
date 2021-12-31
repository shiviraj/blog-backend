package com.blog.builder

import com.blog.domain.Dustbin
import com.blog.domain.Location
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class DustbinBuilder(
    val id: ObjectId? = null,
    val dustbinId: String = "1",
    val location: Location = LocationBuilder().build(),
    val filledStatus: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val filledAt: LocalDateTime = LocalDateTime.now(),
    val alertSent: Boolean = false
) {
    fun build(): Dustbin {
        return Dustbin(
            id = id,
            dustbinId = dustbinId,
            location = location,
            filledStatus = filledStatus,
            createdAt = createdAt,
            filledAt = filledAt,
            alertSent = alertSent
        )
    }
}

data class LocationBuilder(
    val street: String = "",
    val address1: String = "",
    val address2: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val zipCode: Int = 0
) {
    fun build(): Location {
        return Location(
            street = street,
            address1 = address1,
            address2 = address2,
            city = city,
            state = state,
            country = country,
            zipCode = zipCode
        )
    }
}
