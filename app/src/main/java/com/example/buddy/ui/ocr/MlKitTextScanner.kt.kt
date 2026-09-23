package com.example.buddy.ui.ocr

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object `MlKitTextScanner` {

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    /**
     * Processes an in-memory bitmap using on-device ML Kit Text Recognition v2,
     * then analyzes the returned text blocks with ProductTextParser.
     */
    suspend fun processBitmap(bitmap: Bitmap): ParseResult {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val visionText: Text = suspendCancellableCoroutine { continuation ->
                recognizer.process(image)
                    .addOnSuccessListener { result ->
                        if (continuation.isActive) {
                            continuation.resume(result)
                        }
                    }
                    .addOnFailureListener { exception ->
                        if (continuation.isActive) {
                            continuation.resumeWithException(exception)
                        }
                    }
            }

            val rawDetectedText = visionText.text
            ProductTextParser.parse(rawDetectedText)
        } catch (e: Exception) {
            ParseResult.Unclear(
                message = "Couldn't read clearly, please take another photo.",
                rawSnippet = e.message ?: "Optical processing error"
            )
        }
    }

    /**
     * Direct text-based parser invocation (useful for manual simulated tests or fallbacks)
     */
    fun parseRawText(text: String): ParseResult {
        return ProductTextParser.parse(text)
    }

    /**
     * Creates a synthetic bitmap containing label text for on-device testing and emulator usage.
     */
    fun createSampleLabelBitmap(text: String, isBlurry: Boolean = false): Bitmap {
        val width = 640
        val height = 480
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(Color.WHITE)

        val paint = Paint().apply {
            color = if (isBlurry) Color.LTGRAY else Color.BLACK
            textSize = if (isBlurry) 14f else 32f
            isAntiAlias = !isBlurry
            style = Paint.Style.FILL
        }

        val lines = text.split("\n")
        var y = 100f
        for (line in lines) {
            canvas.drawText(line, 60f, y, paint)
            y += if (isBlurry) 25f else 50f
        }

        return bitmap
    }
}
