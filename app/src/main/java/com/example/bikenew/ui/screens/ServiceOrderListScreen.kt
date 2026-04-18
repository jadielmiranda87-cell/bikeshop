package com.example.bikenew.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bikenew.data.entities.ServiceOrder
import com.example.bikenew.ui.viewmodel.BikenewViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ServiceOrderListScreen(viewModel: BikenewViewModel, onOrderClick: (ServiceOrder) -> Unit) {
    val orders by viewModel.allServiceOrders.collectAsState()
    var selectedStatusFilter by remember { mutableStateOf("ALL") }

    val filteredOrders = remember(orders, selectedStatusFilter) {
        if (selectedStatusFilter == "ALL") orders
        else orders.filter { it.status == selectedStatusFilter }
    }

    val totalRevenue = remember(filteredOrders) {
        filteredOrders.sumOf { it.price }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Ordens de Serviço",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedStatusFilter == "ALL",
                onClick = { selectedStatusFilter = "ALL" },
                label = { Text("Tudo") }
            )
            FilterChip(
                selected = selectedStatusFilter == "PENDING",
                onClick = { selectedStatusFilter = "PENDING" },
                label = { Text("Pendentes") }
            )
            FilterChip(
                selected = selectedStatusFilter == "COMPLETED",
                onClick = { selectedStatusFilter = "COMPLETED" },
                label = { Text("Concluídas") }
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(text = "Total na lista:", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "R$ ${String.format("%.2f", totalRevenue)}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        if (filteredOrders.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text(text = "Nenhuma ordem encontrada.")
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(filteredOrders) { order ->
                    ServiceOrderItem(
                        order = order,
                        onStatusChange = { newStatus ->
                            viewModel.updateServiceOrderStatus(order, newStatus)
                        },
                        onClick = { onOrderClick(order) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceOrderItem(order: ServiceOrder, onStatusChange: (String) -> Unit, onClick: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "OS #${order.id}", style = MaterialTheme.typography.titleMedium)
                Text(text = "R$ ${String.format("%.2f", order.price)}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = order.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormat.format(Date(order.date)),
                    style = MaterialTheme.typography.bodySmall
                )
                
                StatusBadge(order.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (order.status != "COMPLETED") {
                    TextButton(onClick = { onStatusChange("COMPLETED") }) {
                        Text("Concluir")
                    }
                }
                if (order.status == "PENDING") {
                    TextButton(onClick = { onStatusChange("IN_PROGRESS") }) {
                        Text("Iniciar")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status) {
        "PENDING" -> MaterialTheme.colorScheme.error
        "IN_PROGRESS" -> MaterialTheme.colorScheme.secondary
        "COMPLETED" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }
    
    val text = when (status) {
        "PENDING" -> "Pendente"
        "IN_PROGRESS" -> "Em Andamento"
        "COMPLETED" -> "Concluído"
        else -> status
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
