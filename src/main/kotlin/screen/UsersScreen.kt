package screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.Utente
import viewmodel.UsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen() {
    val viewModel = remember { UsersViewModel() }
    val uiState by viewModel.users.collectAsState()
    val lazyGridState = rememberLazyGridState()
    val (value, change) = remember {
        mutableStateOf("")
    }
    Scaffold(
        topBar = {
            androidx.compose.material.TopAppBar(
                backgroundColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.contentColorFor(
                    MaterialTheme.colorScheme.background
                ), elevation = 0.dp,
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    ProvideTextStyle(MaterialTheme.typography.titleLarge) {
                        Text("Utentes")
                        TextField(
                            value = value,
                            onValueChange = {
                                change(it)
                                viewModel.findByName(it)
                            },
                            placeholder = { Text("Search") }, leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            }, shape = MaterialTheme.shapes.medium,
                            colors = TextFieldDefaults.textFieldColors(
                                unfocusedIndicatorColor = Color.Unspecified, focusedIndicatorColor = Color.Unspecified
                            )
                        )
                    }
                }
            }
        }
    ) {
        Box(Modifier.fillMaxSize().padding(it)) {
            LazyVerticalGrid(
                state = lazyGridState,
                columns = GridCells.Adaptive(250.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {

                items(uiState.data) { item ->
                    UtenteItem(utente = item, onClick = {})
                }

            }

            if (uiState.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UtenteItem(utente: Utente, onClick: () -> Unit) {
    Surface(
        tonalElevation = 2.dp, shadowElevation = 1.5.dp,
        shape = MaterialTheme.shapes.small, onClick = onClick
    ) {
        Column(
            modifier = Modifier.width(250.dp).heightIn(100.dp).padding(8.dp)
        ) {
            ProvideTextStyle(MaterialTheme.typography.titleLarge) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Person, contentDescription = null)
                    Text(utente.nome)
                }
                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.AlternateEmail, contentDescription = null)
                    Text(utente.email)
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CreditCard, contentDescription = null)
                    Text(utente.paymentType)
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Assistant, contentDescription = null)
                    Text(utente.type)
                }

            }
        }

    }
}