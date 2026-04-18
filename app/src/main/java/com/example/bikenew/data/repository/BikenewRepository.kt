package com.example.bikenew.data.repository

import com.example.bikenew.data.dao.BikenewDao
import com.example.bikenew.data.entities.Bike
import com.example.bikenew.data.entities.Client
import com.example.bikenew.data.entities.ServiceOrder
import kotlinx.coroutines.flow.Flow

class BikenewRepository(private val dao: BikenewDao) {
    val allClients: Flow<List<Client>> = dao.getAllClients()
    val allServiceOrders: Flow<List<ServiceOrder>> = dao.getAllServiceOrders()

    suspend fun insertClient(client: Client) = dao.insertClient(client)
    suspend fun deleteClient(client: Client) = dao.deleteClient(client)

    fun getBikesForClient(clientId: Long): Flow<List<Bike>> = dao.getBikesForClient(clientId)
    suspend fun insertBike(bike: Bike) = dao.insertBike(bike)
    suspend fun deleteBike(bike: Bike) = dao.deleteBike(bike)

    fun getServiceOrdersForClient(clientId: Long): Flow<List<ServiceOrder>> = dao.getServiceOrdersForClient(clientId)
    suspend fun insertServiceOrder(serviceOrder: ServiceOrder) = dao.insertServiceOrder(serviceOrder)
    suspend fun updateServiceOrder(serviceOrder: ServiceOrder) = dao.updateServiceOrder(serviceOrder)
    suspend fun deleteServiceOrder(serviceOrder: ServiceOrder) = dao.deleteServiceOrder(serviceOrder)

    // Users
    val allUsers: Flow<List<User>> = dao.getAllUsers()
    suspend fun getUserByUsername(username: String) = dao.getUserByUsername(username)
    suspend fun insertUser(user: User) = dao.insertUser(user)
    suspend fun deleteUser(user: User) = dao.deleteUser(user)

    // Catalog
    val allCatalogItems: Flow<List<CatalogItem>> = dao.getAllCatalogItems()
    suspend fun insertCatalogItem(item: CatalogItem) = dao.insertCatalogItem(item)
    suspend fun deleteCatalogItem(item: CatalogItem) = dao.deleteCatalogItem(item)

    // OS Items
    fun getItemsForServiceOrder(orderId: Long): Flow<List<ServiceOrderItem>> = dao.getItemsForServiceOrder(orderId)
    suspend fun insertServiceOrderItem(item: ServiceOrderItem) = dao.insertServiceOrderItem(item)
    suspend fun deleteServiceOrderItem(item: ServiceOrderItem) = dao.deleteServiceOrderItem(item)
}
