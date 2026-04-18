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
import com.example.bikenew.data.entities.User
import com.example.bikenew.ui.viewmodel.BikenewViewModel

@Composable
fun AdminUsersScreen(viewModel: BikenewViewModel) {
    val users by viewModel.allUsers.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Mecânico")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(
                text = "Gerenciar Mecânicos",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn {
                items(users) { user ->
                    UserItem(user, onDelete = { viewModel.deleteUser(it) })
                }
            }
        }

        if (showDialog) {
            AddUserDialog(
                onDismiss = { showDialog = false },
                onConfirm = { user ->
                    viewModel.insertUser(user)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun UserItem(user: User, onDelete: (User) -> Unit) {
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
                Text(text = user.username, style = MaterialTheme.typography.titleLarge)
                Text(text = "Cargo: ${user.role}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Acessos: " + listOfNotNull(
                        if (user.canAccessClients) "Clientes" else null,
                        if (user.canAccessOrders) "Ordens" else null,
                        if (user.canAccessFinance) "Financeiro" else null,
                        if (user.canGiveDiscountProducts) "Desc. Peças" else null,
                        if (user.canGiveDiscountServices) "Desc. Serviços" else null
                    ).joinToString(", "),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (user.role != "ADMIN") {
                IconButton(onClick = { onDelete(user) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir")
                }
            }
        }
    }
}

@Composable
fun AddUserDialog(onDismiss: () -> Unit, onConfirm: (User) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var canAccessClients by remember { mutableStateOf(true) }
    var canAccessOrders by remember { mutableStateOf(true) }
    var canAccessFinance by remember { mutableStateOf(false) }
    var canGiveDiscountProducts by remember { mutableStateOf(false) }
    var canGiveDiscountServices by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Mecânico") },
        text = {
            Column {
                TextField(value = username, onValueChange = { username = it }, label = { Text("Usuário") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = password, onValueChange = { password = it }, label = { Text("Senha") })
                Spacer(modifier = Modifier.height(16.dp))
                Text("Permissões:", style = MaterialTheme.typography.labelLarge)
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(checked = canAccessClients, onCheckedChange = { canAccessClients = it })
                    Text("Ver Clientes")
                }
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(checked = canAccessOrders, onCheckedChange = { canAccessOrders = it })
                    Text("Ver Ordens de Serviço")
                }
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(checked = canAccessFinance, onCheckedChange = { canAccessFinance = it })
                    Text("Ver Financeiro")
                }
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(checked = canGiveDiscountProducts, onCheckedChange = { canGiveDiscountProducts = it })
                    Text("Permitir Desconto em Peças")
                }
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(checked = canGiveDiscountServices, onCheckedChange = { canGiveDiscountServices = it })
                    Text("Permitir Desconto em Serviços")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(User(
                        username = username,
                        password = password,
                        role = "MECHANIC",
                        canAccessClients = canAccessClients,
                        canAccessOrders = canAccessOrders,
                        canAccessFinance = canAccessFinance,
                        canGiveDiscountProducts = canGiveDiscountProducts,
                        canGiveDiscountServices = canGiveDiscountServices
                    ))
                },
                enabled = username.isNotBlank() && password.isNotBlank()
            ) {
                Text("Cadastrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
