package com.dennis.imagepicker.adapter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.dennis.imagepicker.R
import com.dennis.imagepicker.loader.ImageLoader
import com.dennis.imagepicker.loader.ImageLoaderUtil
import com.dennis.imagepicker.model.OnItemClickListener
import com.dennis.imagepicker.model.SelectedImageInfo
import com.dennis.imagepicker.ui.ImagePickerActivity

class ThumbnailAdapter(private var pictureList: ArrayList<SelectedImageInfo>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var onItemClickListener: OnItemClickListener
    private var selectedPosition = 0
    private var mOperationList: ArrayList<Int> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_thumbnail, parent, false)
        return ThumbHolder(view)
    }

    override fun getItemCount(): Int {
        return pictureList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val imageInfo = pictureList[position]
        val imageLoader = ImageLoader.Builder()
            .imgView((holder as ThumbHolder).thumbIv)
            .url(imageInfo.path)
            .type(ImageLoaderUtil.PIC_THUMBNAIL)
            .fromStorage(true)
            .tag(ImagePickerActivity.TAG_PICASSO)
            .build()
        ImageLoaderUtil.getInstance().loadImage(imageLoader)

        if (position == selectedPosition) {
            holder.thumbIv.background = ContextCompat.getDrawable(holder.thumbIv.context, R.drawable.bg_image_border)
        } else {
            holder.thumbIv.background = ContextCompat.getDrawable(holder.thumbIv.context, R.drawable.bg_image_border_transparent)
        }

        val colorMatrix = ColorMatrix()
        if (mOperationList.contains(position)) {
            colorMatrix.setScale(1f, 1f, 1f, 0.5f)
        } else {
            colorMatrix.setScale(1f, 1f, 1f, 1f)
        }
        val colorFilter = ColorMatrixColorFilter(colorMatrix)
        holder.thumbIv.colorFilter = colorFilter

        holder.rootView.setOnClickListener { p0 ->
            if (p0 != null) {
                onItemClickListener.onItemClick(holder.thumbIv, position)
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setSelectedItem(position: Int) {
        selectedPosition = position
    }

    fun setOperationItem(operationList: ArrayList<Int>) {
        mOperationList = operationList
    }

    inner class ThumbHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rootView: LinearLayout = itemView.findViewById(R.id.root)
        var thumbIv: ImageView = itemView.findViewById(R.id.iv_thumbnail)

    }
}