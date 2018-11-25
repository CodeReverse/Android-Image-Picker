package com.dennis.imagepicker.loader

class ImageLoaderUtil private constructor() {
    companion object {
        const val PIC_LARGE = 0// 大图
        const val PIC_NORMAL = 1
        const val PIC_THUMBNAIL = 2 // 缩略图
        const val PIC_ORIGIN = 3// 原图

        const val LOAD_STRATEGY_NORMAL = 0

        private var mInstance: ImageLoaderUtil? = null

        fun getInstance(): ImageLoaderUtil {
            if (mInstance == null) {
                synchronized(ImageLoaderUtil::class.java) {
                    if (mInstance == null) {
                        mInstance = ImageLoaderUtil()
                        return mInstance as ImageLoaderUtil
                    }
                }
            }
            return mInstance!!
        }
    }

    private var mStrategy: BaseImageLoaderStrategy? = null

    init {
        mStrategy = PicassoImageLoaderStrategy()
    }


    fun loadImage(img: ImageLoader) {
        mStrategy!!.loadImage(img)
    }

    fun setLoadImgStrategy(strategy: BaseImageLoaderStrategy) {
        mStrategy = strategy
    }
}