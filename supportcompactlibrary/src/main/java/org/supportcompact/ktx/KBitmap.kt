package org.supportcompact.ktx

import android.graphics.*
import androidx.annotation.ColorInt
import android.util.Base64
import android.view.View
import org.supportcompact.CoreApp
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


fun View.toBitmap(): Bitmap {
    this.isDrawingCacheEnabled = true
    this.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
    this.layout(0, 0, this.measuredWidth, this.measuredHeight)
    this.buildDrawingCache(true)

    return this.drawingCache
}

fun Bitmap.base64(): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun Bitmap.resize(newWidth: Int, newHeight: Int): Bitmap {
    val width = this.width
    val height = this.height
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)
    val resizedBitmap = Bitmap.createScaledBitmap(this, width, height, false)
    resizedBitmap.recycle()
    return resizedBitmap
}

fun String.toBitmap(maxWidth: Int = 800, maxHeight: Int = 800): Bitmap {
    val decoded = Base64.decode(this, Base64.DEFAULT)

    // First decode with inJustDecodeBounds=true to check dimensions
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeByteArray(decoded, 0, decoded.count(), options)

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false
    return BitmapFactory.decodeByteArray(decoded, 0, decoded.count(), options)
}

fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    // Raw height and width of image
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        val halfHeight = height / 2
        val halfWidth = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}

fun makeTransparent(bit: Bitmap, transparentColor: Int): Bitmap? {
    val width = bit.width
    val height = bit.height
    val myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val allpixels = IntArray(myBitmap.height * myBitmap.width)
    bit.getPixels(allpixels, 0, myBitmap.width, 0, 0, myBitmap.width, myBitmap.height)
    myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height)

    for (i in 0 until myBitmap.height * myBitmap.width) {
        if (allpixels[i] == transparentColor)
            allpixels[i] = Color.alpha(Color.TRANSPARENT)
    }
    myBitmap.setPixels(allpixels, 0, myBitmap.width, 0, 0, myBitmap.width, myBitmap.height)
    return myBitmap
}

fun Bitmap.toTransparent(@ColorInt transparentColor: Int): Bitmap {
    val myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val allpixels = IntArray(myBitmap.height * myBitmap.width)
    getPixels(allpixels, 0, myBitmap.width, 0, 0, myBitmap.width, myBitmap.height)
    myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height)
    for (i in 0 until myBitmap.height * myBitmap.width) {
        if (allpixels[i] == transparentColor)
            allpixels[i] = Color.alpha(Color.TRANSPARENT)
    }
    myBitmap.setPixels(allpixels, 0, myBitmap.width, 0, 0, myBitmap.width, myBitmap.height)
    return myBitmap
}

fun Bitmap.toTransparent(@ColorInt colorToBeIgnors: ArrayList<Int>): Bitmap {
    val myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
    val allpixels = IntArray(myBitmap.height * myBitmap.width)
    getPixels(allpixels, 0, myBitmap.width, 0, 0, myBitmap.width, myBitmap.height)
    myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height)
    /*getPixels(allpixels, 0, myBitmap.width, 0, 0, myBitmap.width, myBitmap.height)
    myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height)
    for (i in 0 until myBitmap.height * myBitmap.width) {
        if (!colorToBeIgnors.contains(allpixels[i]))
            allpixels[i] = Color.alpha(Color.TRANSPARENT)
    }*/
    //myBitmap.setPixels(allpixels, 0, myBitmap.width, 0, 0, myBitmap.width, myBitmap.height)
    myBitmap.eraseColor(Color.TRANSPARENT)
    return myBitmap
}

fun File.toBitmap(): Bitmap? = BitmapFactory.decodeFile(absolutePath)

fun Bitmap.resizedBitmap(newHeight: Int, newWidth: Int): Bitmap {
    val width = width
    val height = height
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    // create a matrix for the manipulation
    val matrix = Matrix()
    // resize the bit map
    matrix.postScale(scaleWidth, scaleHeight)
    // recreate the new Bitmap
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
}

fun Bitmap.toFile(formator: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): File {
    val tempFile = File(CoreApp.getInstance().cacheDir, "signedImg.png")
    val outStream = FileOutputStream(tempFile)
    compress(formator, 100, outStream)
    outStream.flush()
    outStream.close()
    return tempFile
}

fun Bitmap.mergeBitmap(): Bitmap {
    val width = this.width + (this.width * 10 / 100)
    val height = this.height + (this.height * 10 / 100)

    val backgroundBitmap = Bitmap.createBitmap(width, height,
            Bitmap.Config.ARGB_8888)
    backgroundBitmap.eraseColor(Color.TRANSPARENT)

    val resultBitmap = Bitmap.createBitmap(backgroundBitmap.width, backgroundBitmap.height, backgroundBitmap.config)
    val canvas = Canvas(resultBitmap)
    canvas.drawBitmap(backgroundBitmap, Matrix(), null)
    canvas.drawBitmap(this, ((backgroundBitmap.width - this.width) / 2).toFloat(), ((backgroundBitmap.height - this.height) / 2).toFloat(), Paint(Paint.FILTER_BITMAP_FLAG))

    return resultBitmap
}

fun Bitmap.scaleBitmap(newWidth: Int, newHeight: Int): Bitmap {
    val resizedBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
    val scaleX = newWidth / this.width.toFloat()
    val scaleY = newHeight / this.height.toFloat()
    val pivotX = 0f
    val pivotY = 0f
    val scaleMatrix = Matrix()
    scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY)
    val canvas = Canvas(resizedBitmap)
    canvas.matrix = scaleMatrix
    canvas.drawBitmap(this, 0f, 0f, Paint(Paint.FILTER_BITMAP_FLAG))
    return resizedBitmap
}

fun Bitmap.scaleBitmap(x: Int = 0, y: Int = 0, newWidth: Int, newHeight: Int): Bitmap {
    val myBitmap = Bitmap.createBitmap(this, x, y, newWidth, newHeight)//.getCompressedSignatureBitmap(55)
    val resizedBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(resizedBitmap)
    val mPaint = Paint()
    mPaint.flags = Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG
    canvas.drawBitmap(myBitmap,
            ((resizedBitmap.width - myBitmap.width) / 2).toFloat(),
            ((resizedBitmap.height - myBitmap.height) / 2).toFloat(),
            mPaint)
    return resizedBitmap
}

/**
 * @param compressPercentage Hint to the compressor, 0-100 percent. 0 meaning compress for
 * small size, 100 meaning compress for max quality. Some
 * formats, like PNG which is lossless, will ignore the
 * quality setting
 */
fun Bitmap.getCompressedSignatureBitmap(compressPercentage: Int): Bitmap {
    var compressPercentage = compressPercentage

    if (compressPercentage < 0) {
        compressPercentage = 0
    } else if (compressPercentage > 100) {
        compressPercentage = 100
    }
    val originalBitmap = this
    val originalWidth = originalBitmap.width
    val originalHeight = originalBitmap.height

    val targetWidth = originalWidth * compressPercentage / 100 // your arbitrary fixed limit
    val targetHeight = (originalHeight * targetWidth / originalWidth.toDouble()).toInt()

    /* var whiteBgBitmap = Bitmap.createBitmap(originalWidth, originalHeight, Bitmap.Config.ARGB_8888)
     val canvas = Canvas(whiteBgBitmap)
     canvas.drawColor(Color.WHITE)
     canvas.drawBitmap(originalBitmap, 0f, 0f, null)*/
    var whiteBgBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
    return whiteBgBitmap
}