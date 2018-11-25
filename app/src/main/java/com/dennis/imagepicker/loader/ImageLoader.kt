package com.dennis.imagepicker.loader

import android.widget.ImageView
import com.dennis.imagepicker.R

class ImageLoader private constructor(builder: Builder) {
    private var type: Int = 0  // (Big,Medium,small)
    private var url: String? = null //url to parse
    private var placeHolder: Int = 0//placeholder when fail to load pics
    private var imgView: ImageView? = null //ImageView instantce
    private var isFromStorage: Boolean = false// 是否加载的是本地图片
    private var tag: String? = null

    init {
        this.type = builder.type
        this.url = builder.url
        this.placeHolder = builder.placeHolder
        this.imgView = builder.imgView
        this.isFromStorage = builder.isFromStorage
        this.tag = builder.tag
    }

    fun getType(): Int {
        return type
    }

    fun getUrl(): String? {
        return url
    }

    fun getPlaceHolder(): Int {
        return placeHolder
    }

    fun getImgView(): ImageView? {
        return imgView
    }

    fun getIsFromStorage(): Boolean {
        return isFromStorage
    }

    fun getTag(): String? {
        return tag
    }

    class Builder {
        internal var type: Int = 0
        internal var url: String? = null
        internal var placeHolder: Int = 0
        internal var imgView: ImageView? = null
        internal var isFromStorage: Boolean = false
        internal var tag: String? = null

        init {
            this.type = ImageLoaderUtil.PIC_NORMAL
            this.url = ""
            this.placeHolder = R.drawable.pic_default
            this.imgView = null
            this.isFromStorage = false
            this.tag = ""
        }

        fun type(type: Int): Builder {
            this.type = type
            return this
        }

        fun url(url: String?): Builder {
            this.url = url
            return this
        }

        fun placeHolder(placeHolder: Int): Builder {
            this.placeHolder = placeHolder
            return this
        }

        fun imgView(imgView: ImageView): Builder {
            this.imgView = imgView
            return this
        }

        fun fromStorage(isFromStorage: Boolean): Builder {
            this.isFromStorage = isFromStorage
            return this
        }

        fun tag(tag: String): Builder {
            this.tag = tag
            return this
        }

        fun build(): ImageLoader {
            return ImageLoader(this)
        }

    }

}