package com.example.bikenew.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bikenew.data.entities.CatalogItem
import com.example.bikenew.ui.viewmodel.BikenewViewModel

@Composable
fun CatalogScreen(viewModel: BikenewViewModel) {
    val items by viewModel.allCatalogItems.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Item")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(
                text = "Catálogo de Peças e Serviços",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn {
                items(items) { item ->
                    CatalogItemRow(item, onDelete = { viewModel.deleteCatalogItem(it) })
                }
            }
        }

        if (showDialog) {
            AddCatalogItemDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name, type, price ->
                    viewModel.insertCatalogItem(name, type, price)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun CatalogItemRow(item: CatalogItem, onDelete: (CatalogItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = if (item.type == "PRODUCT") "Peça/Produto" else "Serviço",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "R$ ${String.format("%.2f", item.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = { onDelete(item) }) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir")
            }
        }
    }
}

@Composable
fun AddCatalogItemDialog(onDismiss: () -> Unit, onConfirm: (String, String, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("PRODUCT") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Item no Catálogo") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Nome do Item") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = price,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) price = it },
                    label = { Text("Preço (R$)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Tipo:", style = MaterialTheme.typography.labelLarge)
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(selected = type == "PRODUCT", onClick = { type = "PRODUCT" })
                    Text("Peça")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = type == "SERVICE", onClick = { type = "SERVICE" })
                    Text("Serviço")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, type, price.toDoubleOrNull() ?: 0.0) },
                enabled = name.isNotBlank() && price.isNotBlank()
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
