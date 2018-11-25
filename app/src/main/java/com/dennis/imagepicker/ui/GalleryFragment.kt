package com.dennis.imagepicker.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.dennis.imagepicker.AlbumUtils
import com.dennis.imagepicker.CommonUtils
import com.dennis.imagepicker.R
import com.dennis.imagepicker.Setting
import com.dennis.imagepicker.adapter.GalleryAdapter
import com.dennis.imagepicker.event.OnFolderFinish
import com.dennis.imagepicker.event.OnOperationFinish
import com.dennis.imagepicker.model.ImageFolder
import com.dennis.imagepicker.model.OnItemClickListener
import com.dennis.imagepicker.model.SelectedImageInfo
import com.dennis.imagepicker.ui.ImagePickerActivity.Companion.EXTRA_KEY_DATA_SOURCE
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.imageloader_layout_bottom.view.*
import kotlinx.android.synthetic.main.imageloader_layout_title.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class GalleryFragment : Fragment(), View.OnClickListener {
    private lateinit var mGalleryAdapter: GalleryAdapter
    private var mImageFolder: ImageFolder? = null
    private var selectedCount = 0
    private var mImageInfoList: ArrayList<SelectedImageInfo> = ArrayList()
    private var recordSelectedList: ArrayList<SelectedImageInfo> = ArrayList()
    private lateinit var iconSource: Array<Int>
    private var mSelectedPosition: ArrayList<Int> = ArrayList()

    companion object {

        const val TAG_PICASSO = "Gallery"

        fun newInstance(imageFolder: ImageFolder?): GalleryFragment {
            val galleryFragment = GalleryFragment()
            val bundle = Bundle()
            bundle.putSerializable(ImagePickerActivity.EXTRA_KEY_DATA_SOURCE, imageFolder)
            galleryFragment.arguments = bundle
            return galleryFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mImageFolder = arguments?.getSerializable(EXTRA_KEY_DATA_SOURCE) as ImageFolder?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_gallery, container, false)
        EventBus.getDefault().register(this)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        initSource()
    }

    private fun initView() {
        gallery_title.tv_title_bar_title.text = getString(R.string.title_choose_picture)
        gallery_title.tv_title_back.text = getString(R.string.album)
        gallery_bottom.tv_browse.text = getString(R.string.browse)

        gallery_title.btn_title_back.visibility = View.GONE
        gallery_title.tv_title_back.setOnClickListener(this)
        gallery_title.btn_title_bar_function.setOnClickListener(this)
        val layoutParams = gallery_title.btn_title_bar_function.layoutParams
        layoutParams.height = CommonUtils.dipToPx(16f)
        layoutParams.width = CommonUtils.dipToPx(16f)
        gallery_title.btn_title_bar_function.layoutParams = layoutParams

        gallery_bottom.tv_browse.setOnClickListener(this)
        gallery_bottom.setBackgroundColor(ContextCompat.getColor(context!!, R.color.background_gray_bottom))

        gallery_bottom.btn_finish.setOnClickListener(this)

        updateText()

        rv_picture.layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
        mGalleryAdapter = GalleryAdapter(mImageInfoList)
        rv_picture.adapter = mGalleryAdapter
        mGalleryAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                // 点击的是chooseFlag更改选中状态
                if (view is ImageView) {
                    val imageInfo = mImageInfoList[position]
                    if (imageInfo in recordSelectedList) {
                        selectedCount--
                        imageInfo.isSelected = false
                        mSelectedPosition.remove(position)
                        recordSelectedList.remove(imageInfo)
                    } else {
                        if (selectedCount >= Setting.MAX_SELECTED) {
                            (activity as ImagePickerActivity).showAlert()
                            return
                        }
                        imageInfo.isSelected = true
                        selectedCount++
                        mSelectedPosition.add(position)
                        recordSelectedList.add(imageInfo)
                    }
                    mGalleryAdapter.setSelectedList(mSelectedPosition)
                    mGalleryAdapter.notifyDataSetChanged()
                    updateText()
                } else if (view is RelativeLayout) {
                    // 选择九张后，点击置灰图片不可进入预览。点击高亮图片可以进入预览。
                    if (selectedCount >= Setting.MAX_SELECTED && !mSelectedPosition.contains(position)) {
                        return
                    }
                    startFragment(ViewPictureFragment.newInstance(mImageInfoList, recordSelectedList, position))
                }
            }
        })

        // 滑动时不加载图片
        rv_picture.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> Picasso.get().resumeTag(TAG_PICASSO)
                    else -> Picasso.get().pauseTag(TAG_PICASSO)
                }
            }
        })
    }

    private fun initSource() {
        iconSource = Setting.iconSource.sliceArray(0..Setting.MAX_SELECTED + 1)
        val thread = object : Thread() {
            override fun run() {
                AlbumUtils(context!!).getFolder(mImageFolder)
            }
        }
        thread.run()
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                gallery_title.tv_title_back.id -> startFragment(FolderFragment.newInstance())
                gallery_title.btn_title_bar_function.id -> (activity as ImagePickerActivity).finishSelect(ArrayList())
                gallery_bottom.tv_browse.id -> {
                    startFragment(ViewPictureFragment.newInstance(recordSelectedList, recordSelectedList))
                }
                gallery_bottom.btn_finish.id -> {
                    (activity as ImagePickerActivity).finishSelect(recordSelectedList)
                }
                else -> print("")
            }
        }
    }

    private fun startFragment(fragment: Fragment) {
        val fTransaction = activity?.supportFragmentManager?.beginTransaction()
        fTransaction?.add((view?.parent as ViewGroup).id, fragment, fragment.tag)
        fTransaction?.hide(this)
        fTransaction?.addToBackStack(fragment.tag)
        fTransaction?.commitAllowingStateLoss()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onFolderFinish(folderFinish: OnFolderFinish) {
        val folder = folderFinish.folder
        if (folder != null) {
            mImageInfoList.clear()
            val imageInfoList = folder.pictures!!
            for (item in imageInfoList) {
                val selectedImageInfo = SelectedImageInfo(item)
                mImageInfoList.add(selectedImageInfo)
            }
            mGalleryAdapter.notifyDataSetChanged()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onOperationFinish(onOperationFinish: OnOperationFinish) {
        val operationPicList = onOperationFinish.operationPicList
        mSelectedPosition.clear()
        for (item in operationPicList) {
            mSelectedPosition.add(mImageInfoList.indexOf(item))
        }
        selectedCount = operationPicList.size
        mGalleryAdapter.setSelectedList(mSelectedPosition)
        mGalleryAdapter.notifyDataSetChanged()
        recordSelectedList = operationPicList
        updateText()
    }

    private fun updateText() {
        if (selectedCount > 0) {
            gallery_bottom.tv_browse.setTextColor(ContextCompat.getColor(context!!, android.R.color.white))
            gallery_bottom.btn_finish.text = String.format(getString(R.string.select_done), selectedCount)
            gallery_bottom.btn_finish.background =
                    ContextCompat.getDrawable(context!!, R.drawable.shape_radius_blue_finish_button)
            gallery_bottom.btn_finish.isEnabled = true
            gallery_bottom.tv_browse.isEnabled = true
        } else {
            gallery_bottom.btn_finish.text = getString(R.string.done)
            gallery_bottom.btn_finish.background =
                    ContextCompat.getDrawable(context!!, R.drawable.shape_radius_gray_finish_button)
            gallery_bottom.tv_browse.setTextColor(ContextCompat.getColor(context!!, R.color.text_color_dark_gray))
            gallery_bottom.btn_finish.isEnabled = false
            gallery_bottom.tv_browse.isEnabled = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        Picasso.get().cancelTag(TAG_PICASSO)
    }

}