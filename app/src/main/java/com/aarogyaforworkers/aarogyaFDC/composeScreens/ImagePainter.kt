package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.rememberDrawController

@Composable
fun ImagePainter(capturedImageBitmap: State<Bitmap?>)
{
//    val customPainter = remember {
//        object : Painter() {
//            override val intrinsicSize: androidx.compose.ui.geometry.Size
//                get() = androidx.compose.ui.geometry.Size(capturedImageBitmap.value!!.asImageBitmap().width.toFloat(), capturedImageBitmap.value!!.asImageBitmap().height.toFloat())
//
//            override fun DrawScope.onDraw() {
//                drawImage(capturedImageBitmap.value!!.asImageBitmap())
//                drawLine(
//                    color = Color.Red,
//                    start = Offset(0f, 0f),
//                    end = Offset(capturedImageBitmap.value!!.asImageBitmap().width.toFloat(), capturedImageBitmap.value!!.asImageBitmap().height.toFloat()),
//                    strokeWidth = 5f
//                )
//            }
//        }
//    }
//    Image(painter = customPainter, contentDescription = null)
    val controller = rememberDrawController()
    controller.reDo()
    Row(modifier = Modifier.fillMaxSize()) {
        DrawBox(
            drawController = controller,
            modifier = Modifier.fillMaxSize().weight(1f,true),
            backgroundColor = Color.White,
            bitmapCallback = { imageBitmap, throwable ->
                // Handle the bitmap or any errors
            },
            trackHistory = { undoCount, redoCount ->
                // Handle undo and redo count changes
            }
        )
    }
}