package com.dennis.imagepicker.loader

import android.graphics.Bitmap
import com.dennis.imagepicker.CommonUtils
import com.squareup.picasso.Transformation

class BitmapTransform :Transformation {

    override fun transform(source: Bitmap): Bitmap {
        val targetWidth = CommonUtils.getDisplayWidth()

        val aspectRatio = source.height.toDouble() / source.width.toDouble()
        val targetHeight = (targetWidth * aspectRatio).toInt()
        val result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false)
        if (result != source) {
            // Same bitmap is returned if sizes are the same
            source.recycle()
        }
        return result
    }

    override fun key(): String {
        return "transformation" + " desiredWidth"
    }

}