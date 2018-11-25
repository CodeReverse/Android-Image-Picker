package com.dennis.imagepicker.model

import java.io.Serializable

class ImageFolder :Serializable {
    var dirPath: String? = null
    var firstImagePath: String? = null
    var folderName: String? = null
    var count: Int = 0
    var id: String? = null
    var pictures: ArrayList<ImageInfo>? = null

}