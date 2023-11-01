package screen

import HomeScreen
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import nav.Navigation

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    val screens = remember { Screen.values() }
    var selected by remember { mutableStateOf(screens.first()) }
    val currentScreen by Navigation.screen.collectAsState()

    Surface(color = MaterialTheme.colorScheme.background) {
        PermanentNavigationDrawer(
            drawerContent = {
                Card(
                    modifier = Modifier.width(350.dp)
                        .fillMaxHeight()
                        .padding(8.dp),
                    shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .height(68.dp)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    {
                        Text(text = "Parking", style = MaterialTheme.typography.headlineMedium)
                    }

                    Spacer(Modifier.height(100.dp))

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (item in screens) {
                            NavigationDrawerItem(
                                label = { Text(item.name) },
                                selected = selected == item,
                                onClick = {
                                    Navigation.navigate(item)
                                    selected = item
                                    //selected = item
                                },
                                icon = {
                                    Icon(item.icon, contentDescription = null)
                                },
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                }
            },
            content = {
                AnimatedContent (currentScreen) {
                    when (it) {
                        Screen.Panel -> HomeScreen()
                        Screen.Dashboard -> {
                            DashboardScreen()
                        }
                        Screen.Users -> UsersScreen()
                    }
                }

            }

        )
    }
}


enum class Screen(val icon: ImageVector) {
    Panel(Icons.Default.LocalParking),
    Dashboard(Icons.Default.Dashboard),
    Users(Icons.Default.Person),
}