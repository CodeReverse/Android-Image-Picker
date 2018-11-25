package com.dennis.imagepicker.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.dennis.imagepicker.R
import com.dennis.imagepicker.loader.ImageLoader
import com.dennis.imagepicker.loader.ImageLoaderUtil
import com.dennis.imagepicker.model.ImageFolder
import com.dennis.imagepicker.model.OnItemClickListener

class FolderAdapter(private var folderList: ArrayList<ImageFolder>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var onItemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false)
        return FolderHolder(view)
    }

    override fun getItemCount(): Int {
        return folderList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val imageFolder = folderList[position]
        val imageLoader = ImageLoader.Builder()
                .imgView((holder as FolderHolder).thumbnailIv)
                .url(imageFolder.firstImagePath)
                .type(ImageLoaderUtil.PIC_THUMBNAIL)
                .fromStorage(true)
                .build()
        ImageLoaderUtil.getInstance().loadImage(imageLoader)
        holder.folderNameTv.text = imageFolder.folderName
        holder.pictureCountTv.text = "(${imageFolder.count})"
        holder.rootView.setOnClickListener { p0 ->
            if (p0 != null) {
                onItemClickListener.onItemClick(p0, position)
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    inner class FolderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rootView: RelativeLayout = itemView.findViewById(R.id.root)
        var thumbnailIv: ImageView = itemView.findViewById(R.id.iv_thumbnail)
        var folderNameTv: TextView = itemView.findViewById(R.id.tv_folder_name)
        var pictureCountTv: TextView = itemView.findViewById(R.id.tv_picture_count)

    }
}