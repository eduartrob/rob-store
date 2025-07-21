package com.robstore.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageUtils {

    private const val MAX_IMAGE_SIZE_PX = 1000 // Tamaño máximo del lado más largo en píxeles
    private const val COMPRESSION_QUALITY = 80 // Calidad de compresión JPEG (0-100)

    /**
     * Carga una imagen desde una Uri, la redimensiona y la comprime.
     *
     * @param context Contexto de la aplicación.
     * @param imageUri La Uri de la imagen original.
     * @return Un ByteArray de la imagen redimensionada y comprimida, o null si falla.
     */
    fun processImageForUpload(context: Context, imageUri: Uri): ByteArray? {
        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            if (originalBitmap == null) {
                Log.e("ImageUtils", "No se pudo decodificar el Bitmap desde la Uri: $imageUri")
                return null
            }

            val resizedBitmap = resizeBitmap(originalBitmap, MAX_IMAGE_SIZE_PX)
            val compressedByteArray = compressBitmap(resizedBitmap, COMPRESSION_QUALITY)

            originalBitmap.recycle() // Libera la memoria del bitmap original
            resizedBitmap.recycle() // Libera la memoria del bitmap redimensionado

            return compressedByteArray
        } catch (e: Exception) {
            Log.e("ImageUtils", "Error al procesar la imagen para subir: ${e.message}", e)
            return null
        } finally {
            inputStream?.close()
        }
    }

    /**
     * Redimensiona un Bitmap para que su lado más largo no exceda el tamaño máximo especificado,
     * manteniendo la relación de aspecto.
     *
     * @param bitmap El Bitmap original.
     * @param maxSize El tamaño máximo permitido para el lado más largo (en píxeles).
     * @return El Bitmap redimensionado.
     */
    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (bitmapRatio > 1) { // Horizontal
            newWidth = maxSize
            newHeight = (newWidth / bitmapRatio).toInt()
        } else { // Vertical o Cuadrado
            newHeight = maxSize
            newWidth = (newHeight * bitmapRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Comprime un Bitmap a un ByteArray en formato JPEG.
     *
     * @param bitmap El Bitmap a comprimir.
     * @param quality La calidad de compresión (0-100), donde 100 es la mejor calidad.
     * @return Un ByteArray de la imagen comprimida.
     */
    private fun compressBitmap(bitmap: Bitmap, quality: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }
}
