package com.example.bikenew.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikenew.data.AppDatabase
import com.example.bikenew.data.entities.Bike
import com.example.bikenew.data.entities.CatalogItem
import com.example.bikenew.data.entities.Client
import com.example.bikenew.data.entities.ServiceOrder
import com.example.bikenew.data.entities.ServiceOrderItem
import com.example.bikenew.data.entities.User
import com.example.bikenew.data.repository.BikenewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BikenewViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BikenewRepository
    val allClients: StateFlow<List<Client>>
    val allServiceOrders: StateFlow<List<ServiceOrder>>
    val allUsers: StateFlow<List<User>>
    val allCatalogItems: StateFlow<List<CatalogItem>>

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).bikenewDao()
        repository = BikenewRepository(dao)
        allClients = repository.allClients.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        allServiceOrders = repository.allServiceOrders.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        allUsers = repository.allUsers.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        allCatalogItems = repository.allCatalogItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
        // Criar admin padrão se não houver usuários
        viewModelScope.launch {
            if (repository.getUserByUsername("admin") == null) {
                repository.insertUser(User(username = "admin", password = "123", role = "ADMIN", canAccessFinance = true, canGiveDiscount = true))
            }
        }
    }

    // Catalog functions
    fun insertCatalogItem(name: String, type: String, price: Double) {
        viewModelScope.launch {
            repository.insertCatalogItem(CatalogItem(name = name, type = type, price = price))
        }
    }

    fun deleteCatalogItem(item: CatalogItem) {
        viewModelScope.launch {
            repository.deleteCatalogItem(item)
        }
    }

    // OS Item functions
    fun getItemsForServiceOrder(orderId: Long) = repository.getItemsForServiceOrder(orderId)

    fun addOrderItem(orderId: Long, catalogItem: CatalogItem, discount: Double = 0.0) {
        viewModelScope.launch {
            repository.insertServiceOrderItem(
                ServiceOrderItem(
                    serviceOrderId = orderId,
                    catalogItemId = catalogItem.id,
                    unitPrice = catalogItem.price,
                    discount = discount
                )
            )
            // Atualizar preço total da OS (simples)
            // Em produção seria bom recalcular tudo
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username)
            if (user != null && user.password == password) {
                _currentUser.value = user
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun insertUser(user: User) {
        viewModelScope.launch {
            repository.insertUser(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }

    fun insertClient(name: String, phone: String, email: String?) {
        viewModelScope.launch {
            repository.insertClient(Client(name = name, phone = phone, email = email))
        }
    }

    fun deleteClient(client: Client) {
        viewModelScope.launch {
            repository.deleteClient(client)
        }
    }

    fun insertBike(clientId: Long, brand: String, model: String, color: String, frameNumber: String?) {
        viewModelScope.launch {
            repository.insertBike(Bike(clientId = clientId, brand = brand, model = model, color = color, frameNumber = frameNumber))
        }
    }

    fun getBikesForClient(clientId: Long) = repository.getBikesForClient(clientId)

    fun insertServiceOrder(bikeId: Long, description: String, price: Double) {
        viewModelScope.launch {
            repository.insertServiceOrder(
                ServiceOrder(
                    bikeId = bikeId,
                    description = description,
                    status = "PENDING",
                    price = price
                )
            )
        }
    }

    fun updateServiceOrderStatus(serviceOrder: ServiceOrder, newStatus: String) {
        viewModelScope.launch {
            repository.updateServiceOrder(serviceOrder.copy(status = newStatus))
        }
    }
}
