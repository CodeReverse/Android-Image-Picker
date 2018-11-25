package com.dennis.imagepicker.loader

import android.graphics.Bitmap
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class PicassoImageLoaderStrategy:BaseImageLoaderStrategy {
    override fun loadImage(imageLoader: ImageLoader) {
        when {
            ImageLoaderUtil.PIC_LARGE == imageLoader.getType() -> loadBigPicture(imageLoader)
            ImageLoaderUtil.PIC_NORMAL == imageLoader.getType() -> loadNormal(imageLoader)
            ImageLoaderUtil.PIC_THUMBNAIL == imageLoader.getType() -> loadThumbnail(imageLoader)
            ImageLoaderUtil.PIC_ORIGIN == imageLoader.getType() -> loadOriginPicture(imageLoader)
        }
    }

    private fun loadOriginPicture(img: ImageLoader) {
        Picasso.get()
            .load(if (img.getIsFromStorage()) if (img.getUrl()!!.startsWith("file://")) img.getUrl() else "file://" + img.getUrl()!! else img.getUrl())
            .placeholder(img.getPlaceHolder())
            .tag(img.getTag()!!)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE) //禁止内存缓存
            .fit()
            .into(img.getImgView())
    }

    private fun loadBigPicture(img: ImageLoader) {
        Picasso.get()
            .load(if (img.getIsFromStorage()) if (img.getUrl()!!.startsWith("file://")) img.getUrl() else "file://" + img.getUrl()!! else img.getUrl())
            .placeholder(img.getPlaceHolder())
            .transform(BitmapTransform())
            .config(Bitmap.Config.RGB_565)
            .tag(img.getTag()!!)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE) //禁止内存缓存
            .into(img.getImgView())
    }


    private fun loadNormal(img: ImageLoader) {
        Picasso.get()
            .load(if (img.getIsFromStorage()) if (img.getUrl()!!.startsWith("file://")) img.getUrl() else "file://" + img.getUrl()!! else img.getUrl())
            .placeholder(img.getPlaceHolder())
            .fit()
            .tag(img.getTag()!!)
            .into(img.getImgView())
    }

    private fun loadThumbnail(img: ImageLoader) {
        Picasso.get()
            .load(if (img.getIsFromStorage()) if (img.getUrl()!!.startsWith("file://")) img.getUrl() else "file://" + img.getUrl()!! else img.getUrl())
            .placeholder(img.getPlaceHolder())
            .resize(200, 200)
            .onlyScaleDown()
            .tag(img.getTag()!!)
            .config(Bitmap.Config.RGB_565)
            .centerCrop()
            .into(img.getImgView())
    }

    /**
     * load cache image with Glide
     */
    private fun loadCache(img: ImageLoader) {
        Picasso.get().load(img.getUrl())
            .placeholder(img.getPlaceHolder())
            .fit()
            .networkPolicy(NetworkPolicy.OFFLINE)
            .into(img.getImgView())
    }
}