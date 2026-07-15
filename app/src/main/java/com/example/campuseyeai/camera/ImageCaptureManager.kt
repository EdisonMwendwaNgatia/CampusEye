package com.example.campuseyeai.camera

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import java.io.File

class ImageCaptureManager(
    private val context: Context,
    private val imageCapture: ImageCapture
) {

    fun captureImage(
        admissionNo: String,
        fileName: String,
        onSaved: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val studentFolder = File(
            context.filesDir,
            "students/$admissionNo"
        )

        // Add error check for directory creation
        if (!studentFolder.exists()) {
            val created = studentFolder.mkdirs()
            if (!created) {
                onError(Exception("Failed to create directory: ${studentFolder.absolutePath}"))
                return
            }
        }

        val photoFile = File(
            studentFolder,
            "$fileName.jpg"
        )

        val outputOptions =
            ImageCapture.OutputFileOptions.Builder(photoFile)
                .build()

        imageCapture.takePicture(
            outputOptions,
            androidx.core.content.ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(
                    outputFileResults: ImageCapture.OutputFileResults
                ) {
                    onSaved(photoFile.absolutePath)
                }

                override fun onError(
                    exception: ImageCaptureException
                ) {
                    onError(exception)
                }
            }
        )
    }
}