package screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen() {
    val viewModel = remember { DashboardViewModel() }
    val payments by viewModel.paymentChart.collectAsState()
    val cars by viewModel.cars.collectAsState()
    val discounts by viewModel.discounts.collectAsState()

    Scaffold {
        FlowRow(
            modifier = Modifier.fillMaxWidth()
                .padding(it)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Surface {
                BarChart(payments)
            }

            CarChart(cars)

            DiscountChart(discounts)

        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BarChart(map: Map<String, Double>) = Surface(tonalElevation = 2.dp, shape = MaterialTheme.shapes.small) {
    var width by remember { mutableStateOf(0.dp) }

    val colors = remember {
        listOf(
            Purple200,
            Purple500,
            Teal200,
            Purple700,
            Blue,
            Color.Yellow,
            Color.Blue,
            Color.Red,
            Color.Green,
            Color.Magenta,
            Color.DarkGray,
            Color.Cyan
        )
    }

    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .height(330.dp)
                .onSizeChanged {
                    width = it.width.dp
                },
        ) {
            map.asSequence().forEachIndexed { index, entry ->
                Box(
                    modifier = Modifier.height(330.dp),
                    contentAlignment = Alignment.BottomCenter
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
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Box(
                                Modifier.heightIn(min = 0.dp, max = 300.dp)
                                    .height((entry.value).dp)
                                    .width(20.dp)
                                    .clip(MaterialTheme.shapes.large).background(colors[index])
                            )
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

        OutlinedButton(
            onClick = {},
            enabled = true,
            modifier = Modifier.width(width),
            shape = MaterialTheme.shapes.small
        ) {
            Icon(Icons.Default.AttachMoney, contentDescription = null)
            Text("Valor total ${map.values.sum().toCurrency()}")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarChart(map: Map<String, Int>) = Surface(tonalElevation = 2.dp, shape = MaterialTheme.shapes.small) {
    var width by remember { mutableStateOf(0.dp) }
    val colors = remember {
        listOf(
            Purple200,
            Purple500,
            Teal200,
            Purple700,
            Blue,
            Color.Yellow,
            Color.Blue,
            Color.Red,
            Color.Green,
            Color.Magenta,
            Color.DarkGray,
            Color.Cyan
        ).shuffled()
    }
    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(330.dp).onSizeChanged {
                width = it.width.dp
            }
        ) {
            map.asSequence().forEachIndexed { index, entry ->
                Box(
                    modifier = Modifier.height(330.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    TooltipArea(tooltip = {
                        Card {
                            Text(
                                text = entry.value.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(4.dp)
                            )
                        }
                    }) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Box(
                                Modifier.heightIn(min = 0.dp, max = 300.dp)
                                    .height((entry.value).dp)
                                    .width(20.dp)
                                    .clip(MaterialTheme.shapes.large).background(colors[index])
                            )
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
        OutlinedButton(
            onClick = {}, enabled = true,
            modifier = Modifier.width(width),
            shape = MaterialTheme.shapes.small
        ) {
            Icon(Icons.Default.ElectricCar, contentDescription = null)
            Text("Total de carros ${map.values.sum()}")
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscountChart(map: Map<String, Double>) = Surface(tonalElevation = 2.dp, shape = MaterialTheme.shapes.small) {
    var width by remember { mutableStateOf(0.dp) }

    val colors = remember {
        listOf(
            Purple200,
            Purple500,
            Teal200,
            Purple700,
            Blue,
            Color.Yellow,
            Color.Blue,
            Color.Red,
            Color.Green,
            Color.Magenta,
            Color.DarkGray,
            Color.Cyan
        ).shuffled()
    }
    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .height(330.dp)
                .onSizeChanged {
                    width = it.width.dp
                },
        ) {
            map.asSequence().forEachIndexed { index, entry ->
                Box(
                    modifier = Modifier.height(330.dp),
                    contentAlignment = Alignment.BottomCenter
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
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Box(
                                Modifier.heightIn(min = 0.dp, max = 300.dp)
                                    .height((entry.value).dp)
                                    .width(20.dp)
                                    .clip(MaterialTheme.shapes.large).background(colors[index])
                            )
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
        OutlinedButton(
            onClick = {},
            enabled = true,
            modifier = Modifier.width(width),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Total perdido em descontos ${map.values.sum().toCurrency()}")
        }
    }
}

fun Number.toCurrency(): String {
    val xs = NumberFormat.getCurrencyInstance(Locale("pt", "MZ"))
    return xs.format(this)
}
