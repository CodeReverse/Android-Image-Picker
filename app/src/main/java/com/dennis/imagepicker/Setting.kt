package com.dennis.imagepicker

class Setting {
    companion object {
        const val newestAlbumName = "所有图片"

        /**最新图片集合的最大数量 */
        var MAX_SELECTED = 9

        var needCamera = false

        var iconSource = arrayOf(
            R.drawable.icon_selected1,
            R.drawable.icon_selected2,
            R.drawable.icon_selected3,
            R.drawable.icon_selected4,
            R.drawable.icon_selected5,
            R.drawable.icon_selected6,
            R.drawable.icon_selected7,
            R.drawable.icon_selected8,
            R.drawable.icon_selected9
        )
    }
}