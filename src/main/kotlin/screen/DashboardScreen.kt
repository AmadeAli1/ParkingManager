package screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val viewModel = remember { DashboardViewModel() }
    val payments by viewModel.paymentChart.collectAsState()


    Scaffold {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(it)
                .padding(16.dp)
        ) {

            //PieChart(data = payments)

            BarChart(payments)

        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BarChart(map: Map<String, Double>) {
    val colors = remember {
        listOf(
            Purple200,
            Purple500,
            Teal200,
            Purple700,
            Blue, Color.Yellow, Color.Blue, Color.Red, Color.Green, Color.Magenta, Color.DarkGray, Color.Cyan
        )
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().height(330.dp),
    ) {

        map.asSequence().forEachIndexed { index, entry ->
            Box(
                modifier = Modifier.height(330.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    TooltipArea(tooltip = {
                        Card {
                            Text(
                                text = entry.value.toCurrency(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(4.dp)
                            )
                        }
                    }) {
                        Box(
                            Modifier.heightIn(min = 0.dp, max = 300.dp)
                                .height((entry.value).dp)
                                .width(20.dp)
                                .clip(MaterialTheme.shapes.large).background(colors[index])
                        )
                    }
                    Text(
                        entry.key,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun Number.toCurrency(): String {
    val xs = NumberFormat.getCurrencyInstance(Locale("pt", "MZ"))
    return xs.format(this)
}
