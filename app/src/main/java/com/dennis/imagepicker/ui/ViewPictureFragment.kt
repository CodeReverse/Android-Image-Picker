package com.dennis.imagepicker.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.dennis.imagepicker.R
import com.dennis.imagepicker.Setting
import com.dennis.imagepicker.adapter.ThumbnailAdapter
import com.dennis.imagepicker.event.OnOperationFinish
import com.dennis.imagepicker.loader.ImageLoader
import com.dennis.imagepicker.loader.ImageLoaderUtil
import com.dennis.imagepicker.model.OnItemClickListener
import com.dennis.imagepicker.model.SelectedImageInfo
import kotlinx.android.synthetic.main.fragment_view_picture.*
import kotlinx.android.synthetic.main.imageloader_layout_bottom.view.*
import kotlinx.android.synthetic.main.imageloader_layout_title.view.*
import org.greenrobot.eventbus.EventBus

class ViewPictureFragment : Fragment(), View.OnClickListener {
    private lateinit var mImageList: ArrayList<SelectedImageInfo>
    private lateinit var mThumbnailImageList: ArrayList<SelectedImageInfo>
    private var mOperationImageList: ArrayList<SelectedImageInfo> = ArrayList()
    private lateinit var mThumbnailAdapter: ThumbnailAdapter
    private lateinit var mPagerAdapter: ViewAdapter
    private var operationList: ArrayList<Int> = ArrayList()
    private var lastIndex = 0 // 记录Thumbnail RecyclerView点击的index,避免点击同一个item还要进行逻辑处理
    private var browseIndex: Int = Int.MAX_VALUE
    private var hasThumbnail: Boolean = false // 前一页面是否有选中的图片
    private var isBrowsePicDirectly: Boolean = false // 是否是点击图片的形式跳转的

    companion object {
        fun newInstance(
            imageList: ArrayList<SelectedImageInfo>,
            thumbnailImageList: ArrayList<SelectedImageInfo>,
            index: Int = Int.MAX_VALUE
        ): ViewPictureFragment {
            val viewPictureFragment = ViewPictureFragment()
            val bundle = Bundle()
            bundle.putSerializable(ImagePickerActivity.EXTRA_KEY_DATA_SOURCE, imageList)
            bundle.putSerializable(ImagePickerActivity.EXTRA_KEY_DATA_SELECTED, thumbnailImageList)
            bundle.putInt(ImagePickerActivity.EXTRA_KEY_BROWSE_INDEX, index)
            viewPictureFragment.arguments = bundle
            return viewPictureFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mImageList =
                arguments?.getSerializable(ImagePickerActivity.EXTRA_KEY_DATA_SOURCE) as ArrayList<SelectedImageInfo>
        mThumbnailImageList =
                arguments?.getSerializable(ImagePickerActivity.EXTRA_KEY_DATA_SELECTED) as ArrayList<SelectedImageInfo>
        browseIndex = arguments?.getInt(ImagePickerActivity.EXTRA_KEY_BROWSE_INDEX) as Int
        if (mThumbnailImageList.size > 0) {
            hasThumbnail = true
            mOperationImageList.addAll(mThumbnailImageList)
        }
        if (browseIndex != Int.MAX_VALUE) {
            isBrowsePicDirectly = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_picture, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView() {
        view_picture_title.btn_title_back.visibility = View.VISIBLE
        view_picture_title.tv_title_back.visibility = View.GONE
        view_picture_title.layout_cancel.setOnClickListener(this)

        view_picture_title.btn_title_bar_function.setImageResource(R.drawable.icon_unselected)
        view_picture_title.btn_title_bar_function.setOnClickListener(this)

        view_picture_bottom.tv_browse.visibility = View.GONE
        view_picture_bottom.btn_finish.setOnClickListener(this)
        view_picture_bottom.btn_finish.text = getString(R.string.done)
        view_picture_bottom.setBackgroundColor(resources.getColor(R.color.background_black_alpha_80))
        view_picture_bottom.bringToFront()

        mPagerAdapter = ViewAdapter(mImageList)
        pager_picture.adapter = mPagerAdapter
        pager_picture.offscreenPageLimit = 2
        pager_picture.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val imageInfo = mImageList[position]
                // 更新预览item选中的边框状态
                if (mThumbnailImageList.contains(imageInfo)) {
                    mThumbnailAdapter.setSelectedItem(mThumbnailImageList.indexOf(imageInfo))
                    rv_thumbnail.scrollToPosition(mThumbnailImageList.indexOf(imageInfo))
                } else {
                    mThumbnailAdapter.setSelectedItem(Int.MAX_VALUE)
                }
                mThumbnailAdapter.notifyDataSetChanged()

                updateSelectedNumber(imageInfo)
            }
        })

        // 初始化缩略图RecyclerView
        rv_thumbnail.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mThumbnailAdapter = ThumbnailAdapter(mThumbnailImageList)
        mThumbnailAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (lastIndex == position) return
                val imageInfo = mThumbnailImageList[position]
                lastIndex = position
                mThumbnailAdapter.setSelectedItem(position)
                mThumbnailAdapter.notifyDataSetChanged()
                // 更新viewpager展示的图片
                pager_picture.setCurrentItem(mImageList.indexOf(imageInfo), true)

                updateSelectedNumber(imageInfo)
            }
        })

        rv_thumbnail.adapter = mThumbnailAdapter
        rv_thumbnail.bringToFront()

        divider.bringToFront()

        // 设置第一张显示的图片
        if (!hasThumbnail) { // 没有缩略图
            rv_thumbnail.visibility = View.GONE
            if (isBrowsePicDirectly) { // 只能通过点击图片进来，点击的图片index
                lastIndex = browseIndex
                pager_picture.currentItem = lastIndex
            }
        } else {// 有缩略图
            val selectedImageInfo: SelectedImageInfo?
            if (browseIndex != Int.MAX_VALUE) { // 直接点击图片进来
                lastIndex = browseIndex
                selectedImageInfo = mImageList[browseIndex]
            } else { // 点击浏览按钮进来
                selectedImageInfo = mThumbnailImageList[0]
                lastIndex = mImageList.indexOf(selectedImageInfo)
            }
            pager_picture.currentItem = lastIndex
            if (selectedImageInfo.isSelected) {
                view_picture_title.btn_title_bar_function.setImageResource(
                    Setting.iconSource[mOperationImageList.indexOf(
                        selectedImageInfo
                    )]
                )
            }
        }

        updateText()
    }

    // 更换图片
    private fun switchPicture(imageView: ImageView, picPath: String) {
        val imageLoader = ImageLoader.Builder()
            .imgView(imageView)
            .url(picPath)
            .type(ImageLoaderUtil.PIC_LARGE)
            .fromStorage(true)
            .tag(ImagePickerActivity.TAG_PICASSO)
            .build()
        ImageLoaderUtil.getInstance().loadImage(imageLoader)
    }

    // 更新右上角选中的图片状态
    private fun updateSelectedNumber(imageInfo: SelectedImageInfo) {
        if (!imageInfo.isSelected) {
            view_picture_title.btn_title_bar_function.setImageResource(R.drawable.icon_unselected)
        } else {
            view_picture_title.btn_title_bar_function.setImageResource(
                Setting.iconSource[mOperationImageList.indexOf(
                    imageInfo
                )]
            )
        }
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                view_picture_title.layout_cancel.id -> {
                    EventBus.getDefault().post(OnOperationFinish(mOperationImageList))
                    activity?.supportFragmentManager?.popBackStack()
                }
                view_picture_bottom.btn_finish.id -> {
                    (activity as ImagePickerActivity).finishSelect(mOperationImageList)
                }
                view_picture_title.btn_title_bar_function.id -> {
                    val imageInfo = mImageList[pager_picture.currentItem]
                    val isContain = mThumbnailImageList.contains(imageInfo)
                    if (mOperationImageList.contains(imageInfo)) {
                        imageInfo.isSelected = false
                        mOperationImageList.remove(imageInfo)
                        // 只有当在图片选择页面已经选择过图片并且是点击前一页面底部浏览按钮进来的才会只更改选中状态，否则会更改缩略图的数据源
                        if (hasThumbnail && isContain && !isBrowsePicDirectly) {
                            operationList.add(mThumbnailImageList.indexOf(imageInfo))
                        } else {
                            mThumbnailImageList.remove(imageInfo)
                            mThumbnailAdapter.setSelectedItem(Int.MAX_VALUE)
                        }
                        view_picture_title.btn_title_bar_function.setImageResource(R.drawable.icon_unselected)
                    } else {
                        if (mOperationImageList.size >= Setting.MAX_SELECTED) {
                            (activity as ImagePickerActivity).showAlert()
                            return
                        }
                        imageInfo.isSelected = true
                        mOperationImageList.add(imageInfo)
                        if (hasThumbnail && isContain && !isBrowsePicDirectly) {
                            operationList.remove(mThumbnailImageList.indexOf(imageInfo))
                        } else {
                            mThumbnailImageList.add(imageInfo)
                            mThumbnailAdapter.setSelectedItem(mThumbnailImageList.indexOf(imageInfo))
                            rv_thumbnail.scrollToPosition(mThumbnailImageList.indexOf(imageInfo))
                        }
                        view_picture_title.btn_title_bar_function.setImageResource(Setting.iconSource[mOperationImageList.size - 1])
                    }

                    mThumbnailAdapter.setOperationItem(operationList)
                    mThumbnailAdapter.notifyDataSetChanged()
                    if (mThumbnailImageList.size == 0) {
                        rv_thumbnail.visibility = View.GONE
                    } else {
                        rv_thumbnail.visibility = View.VISIBLE
                    }
                    mPagerAdapter.notifyDataSetChanged()
                    updateText()
                }
                else -> print("")
            }
        }
    }

    private fun updateText() {
        if (mOperationImageList.size > 0) {
            view_picture_bottom.btn_finish.visibility = View.VISIBLE
            view_picture_bottom.btn_finish.text =
                    String.format(getString(R.string.select_done), mOperationImageList.size)
        } else {
            view_picture_bottom.btn_finish.visibility = View.GONE
        }
    }

    inner class ViewAdapter(private var picList: ArrayList<SelectedImageInfo>) : PagerAdapter() {

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return picList.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageLayout = LayoutInflater.from(activity).inflate(R.layout.item_browse, container, false)
            val imageView = imageLayout.findViewById<RecyclablePhotoView>(R.id.page_image)
            imageView.setOnViewTapListener { view, _, _ ->
                if (view != null) {
                    if (view_picture_title.visibility == View.VISIBLE) {
                        view_picture_title.visibility = View.INVISIBLE
                        view_picture_bottom.visibility = View.GONE
                        divider.visibility = View.GONE
                        rv_thumbnail.visibility = View.GONE
                    } else {
                        view_picture_title.visibility = View.VISIBLE
                        view_picture_bottom.visibility = View.VISIBLE
                        divider.visibility = View.VISIBLE
                        if (mThumbnailAdapter.itemCount > 0) rv_thumbnail.visibility = View.VISIBLE
                        view_picture_bottom.bringToFront()
                        rv_thumbnail.bringToFront()
                        divider.bringToFront()
                    }
                }
            }
            switchPicture(imageView, mImageList[position].path!!)
            container.addView(imageLayout)
            return imageLayout
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }


}