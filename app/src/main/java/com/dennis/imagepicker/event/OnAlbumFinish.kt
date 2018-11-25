package com.dennis.imagepicker.event

import com.dennis.imagepicker.model.ImageFolder

class OnAlbumFinish constructor(albums: ArrayList<ImageFolder>?) {
    var albums: ArrayList<ImageFolder>? = null

    init {
        this.albums = albums
    }
}