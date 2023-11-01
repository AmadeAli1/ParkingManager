@file:OptIn(ExperimentalMaterialApi::class)

import androidx.compose.animation.*
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseOutSine
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.github.sarxos.webcam.Webcam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.ParkingSpot
import viewmodel.Clock
import viewmodel.ParkingViewModel
import viewmodel.PaymentState

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen() {
    val viewModel = remember { ParkingViewModel() }
    val parkingSpots = viewModel.slots
    val uiState by viewModel.uiState.collectAsState()
    val clockState by viewModel.clockState.collectAsState()
    var cancella by remember { mutableStateOf(false) }
    var scanner by remember { mutableStateOf(false) }
    var webcam by remember { mutableStateOf<Webcam?>(null) }
    val scannerState by viewModel.scannerState.collectAsState()
    val paymentState by viewModel.paymentState.collectAsState()

    LaunchedEffect(scanner) {
        launch(Dispatchers.IO) {
            webcam = if (scanner) {
                Webcam.getDefault()
            } else {
                viewModel.clear()
                webcam?.lock?.unlock()
                webcam?.close()
                null
            }
        }
    }

    LaunchedEffect(uiState) {
        cancella = true
        delay(2000)
        cancella = false
    }


    Scaffold(
        topBar = {
            ClosedParking(uiState.isClosed, clockState)
        },
    ) { padding ->
        Box(
            Modifier.fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ParkingInfo(
                        color = Color.Green,
                        text = "Available spots",
                        value = uiState.available
                    )
                    Spacer(Modifier.width(16.dp))
                    ParkingInfo(color = Color.Red, text = "Occupied spots", value = uiState.occupied)
                    Spacer(Modifier.width(16.dp))
                    Info("QR Scanner", onClick = {
                        scanner = !scanner
                    })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    var rowWidth by remember { mutableStateOf(0.dp) }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        modifier = Modifier.weight(0.5f),
                    ) {

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .onSizeChanged { size ->
                                    rowWidth = size.width.dp
                                },
                        ) {
                            for (item in parkingSpots.toList()) {
                                if (item.first in 1..5) {
                                    Spot(item.second) {

                                    }
                                }
                            }
                        }


                        LineParking(rowWidth, 1)


                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            for (item in parkingSpots.toList()) {

                                if (item.first in 6..10) {
                                    Spot(item.second)
                                }
                            }
                        }


                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        modifier = Modifier.weight(0.5f),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            for (item in parkingSpots.toList()) {

                                if (item.first in 11..15) {
                                    Spot(item.second)
                                }
                            }
                        }
                        LineParking(rowWidth, 2)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            for (item in parkingSpots.toList()) {

                                if (item.first in 16..20) {
                                    Spot(item.second)
                                }
                            }
                        }


                    }
                }

            }

            if (scanner) {
                Popup(
                    alignment = Alignment.Center,
                    onDismissRequest = { scanner = false },
                ) {
                    Card(colors = CardDefaults.cardColors(Color.DarkGray)) {
                        Column(
                            modifier = Modifier.width(300.dp)
                                .heightIn(250.dp)
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            webcam?.let { it1 ->
                                QrCodePanel(
                                    webcam = it1,
                                    modifier = Modifier.fillMaxWidth().height(240.dp)
                                ) { qr ->
                                    qr?.let { code ->
                                        if (scannerState.utenteId == null) {
                                            viewModel.readQrCode(code)
                                        }
                                    }
                                }
                            }

                            if (scannerState.isLoading) {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(5.dp))
                            } else if (scannerState.parkingDetail != null) {
                                val detail = scannerState.parkingDetail ?: return@Column
                                Text(
                                    text = "Entrance time ${detail.entranceTime}",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Exit time ${detail.exitTime}",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.Bold,
                                )

                                Text(
                                    text = "UtenteId ${detail.utenteId}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Amount per hour ${detail.amountPerHour} Mt",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold

                                )

                                Text(
                                    text = "Discount ${detail.discount} Mt",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Amount ${detail.amount} Mt",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(1.dp))


                                if (paymentState is PaymentState.Show) {
                                    Button(
                                        onClick = {
                                            //TODO DOWNLOAD FILE
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(16.dp),
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text(text = "Download Recibo")
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = null,
                                            modifier = Modifier.padding(start = 5.dp)
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = viewModel::pay,
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(16.dp),
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text(text = "Pagar")
                                        Icon(
                                            imageVector = Icons.Default.MonetizationOn,
                                            contentDescription = null,
                                            modifier = Modifier.padding(start = 5.dp)
                                        )
                                    }
                                    if (paymentState is PaymentState.Failure) {
                                        Text(
                                            text = "Error: ${(paymentState as PaymentState.Failure).message}",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }

                                }
                            } else if (scannerState.errorMessage != null) {
                                scannerState.errorMessage?.let { msg ->
                                    Text(
                                        msg,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    scanner = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(16.dp),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(text = "Close")
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    modifier = Modifier.padding(start = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Spot(
    parkingSpot: ParkingSpot,
    onClick: () -> Unit = {},
) {
    val sizeModifier = Modifier.height(250.dp).width(130.dp)
    val image = remember { "car1.svg" }
    val color by animateColorAsState(
        targetValue = if (parkingSpot.spot.available) Color.Green.copy(0.5f) else Color.Red.copy(0.3f),
        animationSpec = tween(
            durationMillis = 300, easing = EaseOutSine
        )
    )
    TooltipArea(
        tooltip = {
            if (parkingSpot.parking?.utente == null) {
                return@TooltipArea
            }
            Card {
                Text(
                    parkingSpot.parking.utente.nome,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 2.dp
                    )
                )

                Text(
                    text = parkingSpot.parking.entranceTime,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                )
            }
        },
        content = {

            Box(
                modifier = sizeModifier
                    .border(0.5.dp, Color.White, MaterialTheme.shapes.medium)
                    .clip(MaterialTheme.shapes.medium)
                    .background(color)
                    .onClick { onClick() },
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = !parkingSpot.spot.available,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(400, easing = EaseInBounce)),
                    exit = shrinkOut(tween(1000, easing = EaseInElastic))
                ) {
                    Image(
                        painter = painterResource(image),
                        contentDescription = null,
                        modifier = sizeModifier
                            .clip(MaterialTheme.shapes.large)
                    )
                }
                Text(
                    "P${parkingSpot.spot.number}",
                    modifier = Modifier.align(Alignment.BottomCenter).padding(4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
            }
        })
}

@Composable
fun ColumnScope.LineParking(rowWidth: Dp, parkingNumber: Int) {
    Row(
        modifier = Modifier
            .width(rowWidth)
            .height(50.dp)
            .padding(end = 8.dp)
            .align(if (parkingNumber == 1) Alignment.Start else Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.height(1.dp).weight(0.42f).background(Color.Black).clip(MaterialTheme.shapes.medium))
        Text(
            text = "Parking N$parkingNumber",
            modifier = Modifier.padding(horizontal = 4.dp),
            style = MaterialTheme.typography.titleMedium,
        )
        Box(Modifier.height(1.dp).weight(0.42f).background(Color.Black).clip(MaterialTheme.shapes.medium))

    }

}

@Composable
private fun ParkingInfo(color: Color, text: String, value: Int) {
    Surface(tonalElevation = 2.dp, shadowElevation = 2.dp, shape = MaterialTheme.shapes.medium) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = painterResource(resourcePath = "carInfo.svg"),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)

            )
            Text(
                text = "$text $value", fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Info(text: String, onClick: () -> Unit) {
    Surface(
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        onClick = onClick, shape = MaterialTheme.shapes.medium
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = text, fontWeight = FontWeight.SemiBold,
            )
        }
    }
}


@Composable
private fun ClosedParking(isClosed: Boolean, clock: Clock) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(8.dp)
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    ) {
        ProvideTextStyle(
            MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold
            )
        ) {
            Text(clock.day)
            Text(clock.time)
            if (isClosed) {
                Text("Fechado", color = MaterialTheme.colorScheme.error)
            }
        }
    }

}