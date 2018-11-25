package com.dennis.imagepicker.model

import java.io.Serializable

open class ImageInfo : Serializable {
    open var state: Int = 0
    open var path: String? = null
    open var time: Long = 0
    open var displayName: String? = null
}