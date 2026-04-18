package com.example.bikenew.data.dao

import androidx.room.*
import com.example.bikenew.data.entities.Bike
import com.example.bikenew.data.entities.Client
import com.example.bikenew.data.entities.ServiceOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface BikenewDao {
    // Clients
    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<Client>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: Client): Long

    @Delete
    suspend fun deleteClient(client: Client)

    // Bikes
    @Query("SELECT * FROM bikes WHERE clientId = :clientId")
    fun getBikesForClient(clientId: Long): Flow<List<Bike>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBike(bike: Bike): Long

    @Delete
    suspend fun deleteBike(bike: Bike)

    // Service Orders
    @Query("SELECT * FROM service_orders ORDER BY date DESC")
    fun getAllServiceOrders(): Flow<List<ServiceOrder>>

    @Query("""
        SELECT * FROM service_orders 
        INNER JOIN bikes ON service_orders.bikeId = bikes.id 
        WHERE bikes.clientId = :clientId
    """)
    fun getServiceOrdersForClient(clientId: Long): Flow<List<ServiceOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceOrder(serviceOrder: ServiceOrder): Long

    @Update
    suspend fun updateServiceOrder(serviceOrder: ServiceOrder)

    @Delete
    suspend fun deleteServiceOrder(serviceOrder: ServiceOrder)

    // Users
    @Query("SELECT * FROM users ORDER BY username ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Delete
    suspend fun deleteUser(user: User)

    // Catalog Items
    @Query("SELECT * FROM catalog_items ORDER BY name ASC")
    fun getAllCatalogItems(): Flow<List<CatalogItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalogItem(item: CatalogItem): Long

    @Delete
    suspend fun deleteCatalogItem(item: CatalogItem)

    // Service Order Items
    @Query("SELECT * FROM service_order_items WHERE serviceOrderId = :orderId")
    fun getItemsForServiceOrder(orderId: Long): Flow<List<ServiceOrderItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceOrderItem(item: ServiceOrderItem): Long

    @Delete
    suspend fun deleteServiceOrderItem(item: ServiceOrderItem)
}
