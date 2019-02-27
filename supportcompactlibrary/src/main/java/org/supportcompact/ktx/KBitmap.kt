package org.supportcompact.ktx

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.support.annotation.ColorInt
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

