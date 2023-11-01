//import com.github.sarxos.webcam.Webcam
//import com.github.sarxos.webcam.WebcamPanel
//import com.google.zxing.*
//import com.google.zxing.client.j2se.BufferedImageLuminanceSource
//import com.google.zxing.client.j2se.MatrixToImageWriter
//import com.google.zxing.common.BitMatrix
//import com.google.zxing.common.HybridBinarizer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.github.sarxos.webcam.Webcam
import com.github.sarxos.webcam.WebcamPanel
import com.google.zxing.*
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import screen.MainScreen
import java.awt.image.BufferedImage
import java.io.File


fun main() = application {
    val (value, state) = remember { mutableStateOf("") }

    Window(
        onCloseRequest = ::exitApplication,
        icon = painterResource("icon.svg"),
        state = WindowState(placement = WindowPlacement.Maximized), title = "Parking"
    ) {
        //HomeScreen()
        MainScreen()
    }
}

@Composable
fun QrCodePanel(
    modifier: Modifier = Modifier
        .size(240.dp)
        .clipToBounds(),
    webcam: Webcam,
    onResult: (String?) -> Unit,
) {
    val webcamPanel = WebcamPanel(webcam)
    webcamPanel.isFPSDisplayed = true
    webcamPanel.isMirrored = true
    webcamPanel.drawMode = WebcamPanel.DrawMode.FILL
    LaunchedEffect(webcam.isOpen) {
        launch(Dispatchers.Default) {
            while (true) {
                try {
                    if (webcam.image == null || !webcam.isOpen) continue
                    val image: BufferedImage = webcam.image
                    val source: LuminanceSource = BufferedImageLuminanceSource(image)
                    val bitmap = BinaryBitmap(HybridBinarizer(source))
                    val result: Result = MultiFormatReader().decode(bitmap)
                    if (result.text != null) {
                        onResult(result.text)
                    }
                } catch (e: Exception) {
                    if (!webcam.isOpen) {
                        webcam.open()
                    }
                }
            }
        }

    }


    SwingPanel(
        background = Color.Unspecified,
        factory = {
            ComposePanel().add(webcamPanel)
        },
        modifier = modifier
    )
}

fun generateQRCODE(data: String, path: String, charset: String?, map: Map<*, *>?, h: Int, w: Int) {
    val matrix: BitMatrix = MultiFormatWriter().encode(
        String(
            data.toByteArray(charset(charset!!)), charset(
                charset
            )
        ), BarcodeFormat.QR_CODE, w, h
    )
    MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), File(path))
}


