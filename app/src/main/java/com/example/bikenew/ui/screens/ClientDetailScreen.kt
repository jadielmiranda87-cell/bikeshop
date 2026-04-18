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
import com.example.bikenew.data.entities.Bike
import com.example.bikenew.data.entities.Client
import com.example.bikenew.ui.viewmodel.BikenewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(
    clientId: Long,
    viewModel: BikenewViewModel,
    onBack: () -> Unit
) {
    val clients by viewModel.allClients.collectAsState()
    val client = clients.find { it.id == clientId }
    val bikes by viewModel.getBikesForClient(clientId).collectAsState(initial = emptyList())
    var showAddBikeDialog by remember { mutableStateOf(false) }
    var selectedBikeForOS by remember { mutableStateOf<Bike?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(client?.name ?: "Detalhes do Cliente") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddBikeDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Bicicleta")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (client != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Informações de Contato", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Telefone: ${client.phone}")
                        client.email?.let { Text(text = "E-mail: $it") }
                    }
                }
            }

            Text(
                text = "Bicicletas",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            if (bikes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text(text = "Nenhuma bicicleta cadastrada.")
                }
            } else {
                LazyColumn {
                    items(bikes) { bike ->
                        BikeItem(
                            bike = bike,
                            onCreateOS = { selectedBikeForOS = bike }
                        )
                    }
                }
            }
        }

        if (showAddBikeDialog) {
            AddBikeDialog(
                onDismiss = { showAddBikeDialog = false },
                onConfirm = { brand, model, color, frame ->
                    viewModel.insertBike(clientId, brand, model, color, frame)
                    showAddBikeDialog = false
                }
            )
        }

        if (selectedBikeForOS != null) {
            AddServiceOrderDialog(
                bike = selectedBikeForOS!!,
                onDismiss = { selectedBikeForOS = null },
                onConfirm = { description, price ->
                    viewModel.insertServiceOrder(selectedBikeForOS!!.id, description, price)
                    selectedBikeForOS = null
                }
            )
        }
    }
}

@Composable
fun BikeItem(bike: Bike, onCreateOS: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "${bike.brand} ${bike.model}", style = MaterialTheme.typography.titleLarge)
                    Text(text = "Cor: ${bike.color}", style = MaterialTheme.typography.bodyMedium)
                }
                Button(onClick = onCreateOS) {
                    Text("Nova OS")
                }
            }
            bike.frameNumber?.let {
                if (it.isNotBlank()) {
                    Text(text = "Nº Quadro: $it", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun AddServiceOrderDialog(bike: Bike, onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova OS para ${bike.model}") },
        text = {
            Column {
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição do Problema/Serviço") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = price,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) price = it },
                    label = { Text("Preço Estimado (R$)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val priceValue = price.toDoubleOrNull() ?: 0.0
                    onConfirm(description, priceValue)
                },
                enabled = description.isNotBlank()
            ) {
                Text("Gerar Ordem")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddBikeDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, String?) -> Unit) {
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var frame by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Bicicleta") },
        text = {
            Column {
                TextField(value = brand, onValueChange = { brand = it }, label = { Text("Marca (ex: Caloi)") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = model, onValueChange = { model = it }, label = { Text("Modelo (ex: Elite)") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = color, onValueChange = { color = it }, label = { Text("Cor") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = frame, onValueChange = { frame = it }, label = { Text("Nº Quadro (opcional)") })
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (brand.isNotBlank() && model.isNotBlank()) onConfirm(brand, model, color, frame) },
                enabled = brand.isNotBlank() && model.isNotBlank()
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
