package com.dennis.imagepicker.model

import java.io.Serializable

class SelectedImageInfo(imageInfo: ImageInfo) : ImageInfo(), Serializable {
    var isSelected: Boolean = false

    init {
        this.displayName = imageInfo.displayName
        this.path = imageInfo.path
        this.state = imageInfo.state
        this.time = imageInfo.time
    }
}