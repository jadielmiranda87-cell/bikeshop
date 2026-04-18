package com.example.bikenew.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.List
import com.example.bikenew.ui.viewmodel.BikenewViewModel

sealed class Screen(val route: String, val label: String, val icon: @Composable () -> Unit) {
    object Clients : Screen("clients", "Clientes", { Icon(Icons.Default.Person, contentDescription = null) })
    object ClientDetail : Screen("client_detail/{clientId}", "Detalhes", { })
    object ServiceOrders : Screen("orders", "Ordens", { Icon(Icons.Default.Build, contentDescription = null) })
    object ServiceOrderDetail : Screen("order_detail/{orderId}", "Detalhes OS", { })
    object Catalog : Screen("catalog", "Catálogo", { Icon(Icons.Default.List, contentDescription = null) })
    object Admin : Screen("admin", "Mecânicos", { Icon(Icons.Default.Settings, contentDescription = null) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: BikenewViewModel = viewModel()) {
    val currentUser by viewModel.currentUser.collectAsState()
    val navController = rememberNavController()

    if (currentUser == null) {
        LoginScreen(viewModel)
    } else {
        val items = remember(currentUser) {
            val list = mutableListOf<Screen>()
            if (currentUser!!.canAccessClients) list.add(Screen.Clients)
            if (currentUser!!.canAccessOrders) list.add(Screen.ServiceOrders)
            if (currentUser!!.role == "ADMIN") {
                list.add(Screen.Catalog)
                list.add(Screen.Admin)
            }
            list
        }
// ... rest of the file ...

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Bikenew - ${currentUser!!.username}") },
                    actions = {
                        TextButton(onClick = { viewModel.logout() }) {
                            Text("Sair", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            },
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                if (currentDestination?.route in items.map { it.route }) {
                    NavigationBar {
                        items.forEach { screen ->
                            NavigationBarItem(
                                icon = screen.icon,
                                label = { Text(screen.label) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController, 
                startDestination = if (items.isNotEmpty()) items.first().route else Screen.Clients.route, 
                Modifier.padding(innerPadding)
            ) {
                composable(Screen.Clients.route) { 
                    ClientListScreen(
                        viewModel = viewModel,
                        onClientClick = { client ->
                            navController.navigate("client_detail/${client.id}")
                        }
                    ) 
                }
                composable(
                    route = Screen.ClientDetail.route,
                    arguments = listOf(androidx.navigation.navArgument("clientId") { type = androidx.navigation.NavType.LongType })
                ) { backStackEntry ->
                    val clientId = backStackEntry.arguments?.getLong("clientId") ?: 0L
                    ClientDetailScreen(
                        clientId = clientId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.ServiceOrders.route) { 
                    ServiceOrderListScreen(
                        viewModel = viewModel,
                        onOrderClick = { order ->
                            navController.navigate("order_detail/${order.id}")
                        }
                    ) 
                }
                composable(
                    route = Screen.ServiceOrderDetail.route,
                    arguments = listOf(androidx.navigation.navArgument("orderId") { type = androidx.navigation.NavType.LongType })
                ) { backStackEntry ->
                    val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L
                    ServiceOrderDetailScreen(
                        orderId = orderId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Catalog.route) { CatalogScreen(viewModel) }
                composable(Screen.Admin.route) { AdminUsersScreen(viewModel) }
            }
        }
    }
}
