import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WaterfallChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen() {
    val navList = remember { listOf(NavItem.Users, NavItem.DailyStatics, NavItem.Money) }
    NavigationRail(
        header = {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (item in navList) {
                    NavigationRailItem(
                        selected = false,
                        onClick = {},
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {

        }
    }

}

sealed class NavItem(val label: String, val icon: ImageVector) {
    object Users : NavItem("Users", Icons.Default.Person)
    object DailyStatics : NavItem("Daily Statics", Icons.Default.WaterfallChart)
    object Money : NavItem("Money", Icons.Default.Money)


}