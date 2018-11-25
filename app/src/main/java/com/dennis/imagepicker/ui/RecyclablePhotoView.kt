package com.dennis.imagepicker.ui

import android.content.Context
import android.util.AttributeSet
import uk.co.senab.photoview.PhotoView

class RecyclablePhotoView : PhotoView {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    // 从屏幕中消失时回调，去掉drawable引用，能加快内存的回收
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setImageDrawable(null)
    }
}