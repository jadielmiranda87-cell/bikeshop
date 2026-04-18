package com.example.bikenew.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bikenew.data.entities.CatalogItem
import com.example.bikenew.data.entities.ServiceOrder
import com.example.bikenew.data.entities.ServiceOrderItem
import com.example.bikenew.ui.viewmodel.BikenewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceOrderDetailScreen(
    orderId: Long,
    viewModel: BikenewViewModel,
    onBack: () -> Unit
) {
    val orders by viewModel.allServiceOrders.collectAsState()
    val order = orders.find { it.id == orderId }
    val items by viewModel.getItemsForServiceOrder(orderId).collectAsState(initial = emptyList())
    val catalog by viewModel.allCatalogItems.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    
    var showAddItemDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Itens da OS #${orderId}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddItemDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Item")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (order != null) {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Descrição: ${order.description}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Status: ${order.status}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Text(text = "Peças e Serviços", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items) { item ->
                    val catalogItem = catalog.find { it.id == item.catalogItemId }
                    OrderItemRow(item, catalogItem)
                }
            }
            
            val total = items.sumOf { (it.unitPrice * it.quantity) - it.discount }
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Total da OS:", style = MaterialTheme.typography.titleLarge)
                    Text("R$ ${String.format("%.2f", total)}", style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        if (showAddItemDialog) {
            AddItemToOSDialog(
                catalog = catalog,
                canGiveDiscountProducts = currentUser?.canGiveDiscountProducts ?: false,
                canGiveDiscountServices = currentUser?.canGiveDiscountServices ?: false,
                onDismiss = { showAddItemDialog = false },
                onConfirm = { catalogItem, discount ->
                    viewModel.addOrderItem(orderId, catalogItem, discount)
                    showAddItemDialog = false
                }
            )
        }
    }
}

@Composable
fun OrderItemRow(item: ServiceOrderItem, catalogItem: CatalogItem?) {
    ListItem(
        headlineContent = { Text(catalogItem?.name ?: "Item desconhecido") },
        supportingContent = {
            Column {
                Text("Preço Unit: R$ ${String.format("%.2f", item.unitPrice)}")
                if (item.discount > 0) {
                    Text("Desconto: R$ ${String.format("%.2f", item.discount)}", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        trailingContent = {
            Text("Subtotal: R$ ${String.format("%.2f", (item.unitPrice * item.quantity) - item.discount)}")
        }
    )
}

@Composable
fun AddItemToOSDialog(
    catalog: List<CatalogItem>,
    canGiveDiscountProducts: Boolean,
    canGiveDiscountServices: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (CatalogItem, Double) -> Unit
) {
    var selectedItem by remember { mutableStateOf<CatalogItem?>(null) }
    var discountText by remember { mutableStateOf("0.0") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar Item") },
        text = {
            Column {
                Box {
                    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(selectedItem?.name ?: "Selecionar Peça/Serviço")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        catalog.forEach { item ->
                            DropdownMenuItem(
                                text = { Text("${item.name} - R$ ${item.price}") },
                                onClick = {
                                    selectedItem = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                selectedItem?.let { item ->
                    val canDiscount = if (item.type == "PRODUCT") canGiveDiscountProducts else canGiveDiscountServices
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Preço Sugerido: R$ ${item.price}")
                    
                    if (canDiscount) {
                        TextField(
                            value = discountText,
                            onValueChange = { discountText = it },
                            label = { Text("Desconto (R$)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            "Você não tem permissão para dar desconto neste item.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedItem?.let {
                        onConfirm(it, discountText.toDoubleOrNull() ?: 0.0)
                    }
                },
                enabled = selectedItem != null
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
