package com.dennis.imagepicker

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import com.dennis.imagepicker.event.OnAlbumFinish
import com.dennis.imagepicker.event.OnFolderFinish
import com.dennis.imagepicker.model.ImageFolder
import com.dennis.imagepicker.model.ImageInfo
import org.greenrobot.eventbus.EventBus
import java.io.File


class AlbumUtils constructor(context: Context) {
    private var mContext: Context = context

    fun getAlbums(): ArrayList<ImageFolder> {
        val albums: ArrayList<ImageFolder> = ArrayList()
        albums.add(getAllPhotos())
        val resolver: ContentResolver? = mContext.contentResolver
        if (resolver != null) {
            val cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(
                            MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.BUCKET_ID,
                            MediaStore.Images.Media.DATE_MODIFIED,
                            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                    ),
                    MediaStore.Images.Media.MIME_TYPE + "=? or " +
                            MediaStore.Images.Media.MIME_TYPE + "=? or " +
                            MediaStore.Images.Media.MIME_TYPE + "=?) " +
                            "group by (" + MediaStore.Images.ImageColumns.BUCKET_ID,
                    arrayOf("image/jpeg", "image/png", "image/jpg"),
                    MediaStore.Images.Media.DATE_MODIFIED + " desc")
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val file = File(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)))
                    val imageFolder = ImageFolder()
                    imageFolder.dirPath = file.parent
                    imageFolder.firstImagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    imageFolder.id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                    imageFolder.folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    val allFolders: Array<String> = file.parentFile.list { _, fileName ->
                        fileName.toLowerCase().endsWith(".png") || fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")
                    }
                    if (allFolders.isNotEmpty()) {
                        val arrayList = ArrayList<String>()
                        allFolders.iterator().forEach { fileName ->
                            val tempFile = File(file.parent + File.separator + fileName)
                            if (tempFile.length() > 0) {
                                arrayList.add(fileName)
                            }
                        }
                        imageFolder.count = arrayList.size
                        albums.add(imageFolder)
                    }
                }
                cursor.close()
            }
        }
        EventBus.getDefault().post(OnAlbumFinish(albums))
        return albums
    }

    fun getFolder(folder: ImageFolder?): ImageFolder? {
        val resolver: ContentResolver? = mContext.contentResolver
        if (folder?.pictures != null && folder.pictures?.size!! > 0) {
            EventBus.getDefault().post(OnFolderFinish(folder))
            return folder
        }
        if (folder == null) {
            return getAllPhotos()
        } else {
            if (resolver != null) {
                val cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(MediaStore.Images.Media.DATA,
                                MediaStore.Images.Media.DISPLAY_NAME,
                                MediaStore.Images.Media.DATE_MODIFIED,
                                MediaStore.Images.Media.SIZE
                        ),
                        MediaStore.Images.ImageColumns.BUCKET_ID + "=? and (" +
                                MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?) ",
                        arrayOf(folder.id, "image/jpeg", "image/png", "image/jpg"),
                        MediaStore.Images.Media.DATE_MODIFIED + " desc")
                val imageInfoList: ArrayList<ImageInfo> = ArrayList()
                folder.pictures = imageInfoList
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        if (cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)) <= 0) {
                            continue
                        }
                        val imageInfo = ImageInfo()
                        imageInfo.path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                        imageInfo.displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                        imageInfo.time = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))
                        imageInfoList.add(imageInfo)
                    }
                    cursor.close()
                }
                folder.count = imageInfoList.size
                EventBus.getDefault().post(OnFolderFinish(folder))
                return folder
            }
        }
        return null
    }


    // 获取所有的本地图片
    private fun getAllPhotos(): ImageFolder {
        val newestFolder = ImageFolder()
        newestFolder.folderName = Setting.newestAlbumName
        val imageList: ArrayList<ImageInfo> = ArrayList()
        val resolver: ContentResolver? = mContext.contentResolver
        if (resolver != null) {
            val cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.SIZE,
                            MediaStore.Images.Media.DISPLAY_NAME,
                            MediaStore.Images.Media.DATE_MODIFIED
                    ),
                    null,
                    null,
                    MediaStore.Images.Media.DATE_MODIFIED + " desc")
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    if (cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)) <= 0) {
                        continue
                    }
                    val imageInfo = ImageInfo()
                    imageInfo.path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    imageInfo.displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                    imageInfo.time = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))
                    imageList.add(imageInfo)
                }
                cursor.close()
                newestFolder.firstImagePath = imageList[0].path
                newestFolder.pictures = imageList
                newestFolder.count = imageList.size
            }
        }
        EventBus.getDefault().post(OnFolderFinish(newestFolder))
        return newestFolder
    }

}