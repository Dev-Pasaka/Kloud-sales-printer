package presentation.screens.dashboard.routes.logsScreen

import AppColors
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import data.repository.ReceiptRepositoryImpl
import java.io.File
import java.io.InputStream
import org.jetbrains.skia.Image


fun loadImageBitmap(inputStream: InputStream): ImageBitmap {
    val image = Image.makeFromEncoded(inputStream.readBytes())
    return image.toComposeImageBitmap()
}

@Composable
fun ViewReceipt(
    actionClose: () -> Unit,
    image: String,
    actionPrint: (String) -> Unit
) {
    println("Image: $image")
    val imagePath = ReceiptRepositoryImpl().getReceiptsFolderPath()

    val file = File("$imagePath/receipts-with-qr/$image")
    val imageBitmap: ImageBitmap = remember(file) {
        loadImageBitmap(file.inputStream())
    }
    println("ImageBitmap: $imageBitmap")

    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = actionClose
    ) {
        Card(
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Button(
                        onClick = {
                            actionPrint(image)
                            println("Printing receipt: ${file.path}")
                        },
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppColors.primaryColor,
                            contentColor = Color.White
                        )
                    ){
                        Text("Print")
                    }
                    IconButton(
                        onClick = actionClose,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Receipt Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}