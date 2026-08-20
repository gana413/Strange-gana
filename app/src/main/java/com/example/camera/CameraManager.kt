package com.example.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs

data class MotionAnalysisResult(
    val luminance: Double = 0.0,
    val motionDelta: Double = 0.0,
    val isSuddenMotionDetected: Boolean = false
)

class CameraManager(private val context: Context) {

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var imageCapture: ImageCapture? = null

    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private val _lensFacing = MutableStateFlow(CameraSelector.LENS_FACING_FRONT)
    val lensFacing: StateFlow<Int> = _lensFacing.asStateFlow()

    private val _isTorchEnabled = MutableStateFlow(false)
    val isTorchEnabled: StateFlow<Boolean> = _isTorchEnabled.asStateFlow()

    private val _hasFlashUnit = MutableStateFlow(false)
    val hasFlashUnit: StateFlow<Boolean> = _hasFlashUnit.asStateFlow()

    private val _motionState = MutableStateFlow(MotionAnalysisResult())
    val motionState: StateFlow<MotionAnalysisResult> = _motionState.asStateFlow()

    private var previousFrameBuffer: ByteArray? = null

    fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onCameraReady: () -> Unit = {}
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases(lifecycleOwner, previewView)
                onCameraReady()
            } catch (e: Exception) {
                Log.e("CameraManager", "Error binding camera use cases", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun switchCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        _lensFacing.value = if (_lensFacing.value == CameraSelector.LENS_FACING_FRONT) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }
        _isTorchEnabled.value = false
        bindCameraUseCases(lifecycleOwner, previewView)
    }

    fun setLensFacing(facing: Int, lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        if (_lensFacing.value != facing) {
            _lensFacing.value = facing
            _isTorchEnabled.value = false
            bindCameraUseCases(lifecycleOwner, previewView)
        }
    }

    fun toggleTorch() {
        camera?.let { cam ->
            if (cam.cameraInfo.hasFlashUnit() && _lensFacing.value == CameraSelector.LENS_FACING_BACK) {
                val newState = !_isTorchEnabled.value
                cam.cameraControl.enableTorch(newState)
                _isTorchEnabled.value = newState
            }
        }
    }

    private fun bindCameraUseCases(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        val provider = cameraProvider ?: return

        provider.unbindAll()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(_lensFacing.value)
            .build()

        preview = Preview.Builder()
            .build()
            .also {
                it.surfaceProvider = previewView.surfaceProvider
            }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysis ->
                analysis.setAnalyzer(cameraExecutor, LuminanceAndMotionAnalyzer { result ->
                    _motionState.value = result
                })
            }

        try {
            camera = provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalysis
            )

            _hasFlashUnit.value = camera?.cameraInfo?.hasFlashUnit() == true &&
                    _lensFacing.value == CameraSelector.LENS_FACING_BACK

        } catch (exc: Exception) {
            Log.e("CameraManager", "Use case binding failed", exc)
        }
    }

    fun release() {
        cameraExecutor.shutdown()
        cameraProvider?.unbindAll()
    }

    private inner class LuminanceAndMotionAnalyzer(
        private val listener: (MotionAnalysisResult) -> Unit
    ) : ImageAnalysis.Analyzer {

        override fun analyze(image: ImageProxy) {
            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            var motionDelta = 0.0
            var isSuddenMotion = false

            previousFrameBuffer?.let { prev ->
                if (prev.size == data.size) {
                    var diffSum = 0L
                    val step = 16 // Subsample for fast processing
                    var sampledCount = 0
                    for (i in data.indices step step) {
                        val currVal = data[i].toInt() and 0xFF
                        val prevVal = prev[i].toInt() and 0xFF
                        diffSum += abs(currVal - prevVal)
                        sampledCount++
                    }
                    if (sampledCount > 0) {
                        motionDelta = diffSum.toDouble() / sampledCount.toDouble()
                        if (motionDelta > 18.0) {
                            isSuddenMotion = true
                        }
                    }
                }
            }

            previousFrameBuffer = data.clone()

            listener(
                MotionAnalysisResult(
                    luminance = luma,
                    motionDelta = motionDelta,
                    isSuddenMotionDetected = isSuddenMotion
                )
            )

            image.close()
        }

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()
            val data = ByteArray(remaining())
            get(data)
            return data
        }
    }
}
