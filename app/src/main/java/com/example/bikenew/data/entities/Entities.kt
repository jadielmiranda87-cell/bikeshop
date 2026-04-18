package com.example.bikenew.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val password: String, // Em um app real, use hash
    val role: String, // ADMIN, MECHANIC
    val canAccessClients: Boolean = true,
    val canAccessBikes: Boolean = true,
    val canAccessOrders: Boolean = true,
    val canAccessFinance: Boolean = false,
    val canGiveDiscountProducts: Boolean = false,
    val canGiveDiscountServices: Boolean = false
)

@Entity(tableName = "catalog_items")
data class CatalogItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // PRODUCT, SERVICE
    val price: Double
)

@Entity(
    tableName = "service_order_items",
    foreignKeys = [
        ForeignKey(
            entity = ServiceOrder::class,
            parentColumns = ["id"],
            childColumns = ["serviceOrderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CatalogItem::class,
            parentColumns = ["id"],
            childColumns = ["catalogItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ServiceOrderItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serviceOrderId: Long,
    val catalogItemId: Long,
    val quantity: Int = 1,
    val unitPrice: Double,
    val discount: Double = 0.0
)

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String? = null
)

@Entity(
    tableName = "bikes",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Bike(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientId: Long,
    val brand: String,
    val model: String,
    val color: String,
    val frameNumber: String? = null
)

@Entity(
    tableName = "service_orders",
    foreignKeys = [
        ForeignKey(
            entity = Bike::class,
            parentColumns = ["id"],
            childColumns = ["bikeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ServiceOrder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bikeId: Long,
    val description: String,
    val status: String, // PENDING, IN_PROGRESS, COMPLETED
    val price: Double,
    val date: Long = System.currentTimeMillis()
)
