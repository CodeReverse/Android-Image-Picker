package com.dennis.imagepicker

import android.util.DisplayMetrics
import android.util.TypedValue

class CommonUtils {
    companion object {
        /**
         * 像素转为dip
         *
         * @param pxValue
         * @return
         */
        fun pxToDip(pxValue: Float): Int {
            val scale = ContextHolder.getContext()!!.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        /**
         * dip转为像素
         *
         * @param dipValue
         * @return
         */
        fun dipToPx(dipValue: Float): Int {
            return (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dipValue,
                ContextHolder.getContext()!!.resources.displayMetrics
            ) + 0.5f).toInt()
        }

        fun getDisplayWidth(): Int {
            return getDisplayInfo().widthPixels
        }

        fun getDisplayHeight(): Int {
            return getDisplayInfo().heightPixels
        }

        private fun getDisplayInfo(): DisplayMetrics {
            return ContextHolder.getApplication()!!.resources.displayMetrics
        }
    }
}