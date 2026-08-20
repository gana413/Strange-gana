package com.example.camera

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraPreview(
    cameraManager: CameraManager,
    modifier: Modifier = Modifier,
    onPreviewViewReady: (PreviewView) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        }
    }

    DisposableEffect(lifecycleOwner) {
        cameraManager.startCamera(lifecycleOwner, previewView) {
            onPreviewViewReady(previewView)
        }

        onDispose {
            // Camera cleanup handled by lifecycle
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier.fillMaxSize()
    )
}
