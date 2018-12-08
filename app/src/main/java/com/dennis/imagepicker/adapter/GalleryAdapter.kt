package com.dennis.imagepicker.adapter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.dennis.imagepicker.R
import com.dennis.imagepicker.Setting
import com.dennis.imagepicker.loader.ImageLoader
import com.dennis.imagepicker.loader.ImageLoaderUtil
import com.dennis.imagepicker.model.OnItemClickListener
import com.dennis.imagepicker.model.SelectedImageInfo
import com.dennis.imagepicker.ui.ImagePickerActivity

class GalleryAdapter(private var pictureList: ArrayList<SelectedImageInfo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var onItemClickListener: OnItemClickListener
    private var mSelectedList: ArrayList<Int> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        return GalleryHolder(view)
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
            .imgView((holder as GalleryHolder).pictureIv)
            .url(imageInfo.path)
            .type(ImageLoaderUtil.PIC_THUMBNAIL)
            .fromStorage(true)
            .tag(ImagePickerActivity.TAG_PICASSO)
            .build()
        ImageLoaderUtil.getInstance().loadImage(imageLoader)
        if (mSelectedList.contains(position)) {
            holder.chosenFlagIv.setImageResource(Setting.iconSource[mSelectedList.indexOf(position)])
        } else {
            holder.chosenFlagIv.setImageResource(R.drawable.icon_unselected)
        }

        // 超出选中的图片限制后，其余图片灰度处理
        val colorMatrix = ColorMatrix()
        if (mSelectedList.size >= Setting.MAX_SELECTED && !mSelectedList.contains(position)) {
            holder.pictureIv.setBackgroundColor(ContextCompat.getColor(holder.pictureIv.context, android.R.color.white))
            colorMatrix.setScale(1f, 1f, 1f, 0.5f)
        } else {
            holder.pictureIv.setBackgroundColor(ContextCompat.getColor(holder.pictureIv.context, android.R.color.transparent))
            colorMatrix.setScale(1f, 1f, 1f, 1f)
        }
        val colorFilter = ColorMatrixColorFilter(colorMatrix)
        holder.pictureIv.colorFilter = colorFilter

        holder.chooseSectionRl.setOnClickListener { p0 ->
            if (p0 != null) {
                onItemClickListener.onItemClick(holder.chosenFlagIv, position)
            }
        }

        holder.rootView.setOnClickListener { p0 ->
            if (p0 != null) {
                onItemClickListener.onItemClick(holder.rootView, position)
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setSelectedList(selectedList: ArrayList<Int>) {
        mSelectedList = selectedList
    }

    inner class GalleryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rootView: RelativeLayout = itemView.findViewById(R.id.root)
        var pictureIv: ImageView = itemView.findViewById(R.id.iv_picture)
        var chosenFlagIv: ImageView = itemView.findViewById(R.id.iv_chosen_flag)
        var chooseSectionRl: RelativeLayout = itemView.findViewById(R.id.rl_choose_section)

    }
}