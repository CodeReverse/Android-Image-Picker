package com.dennis.imagepicker.event

import com.dennis.imagepicker.model.ImageFolder

class OnFolderFinish constructor(folder: ImageFolder){
    var folder: ImageFolder? = null
    init {
        this.folder = folder
    }
}