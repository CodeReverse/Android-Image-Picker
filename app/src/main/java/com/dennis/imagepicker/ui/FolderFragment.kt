package com.dennis.imagepicker.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dennis.imagepicker.AlbumUtils
import com.dennis.imagepicker.R
import com.dennis.imagepicker.adapter.FolderAdapter
import com.dennis.imagepicker.event.OnAlbumFinish
import com.dennis.imagepicker.model.ImageFolder
import com.dennis.imagepicker.model.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_folder.*
import kotlinx.android.synthetic.main.imageloader_layout_title.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FolderFragment : Fragment(), View.OnClickListener {
    private lateinit var mFolderAdapter: FolderAdapter
    private var mFolderList: ArrayList<ImageFolder> = ArrayList()

    companion object {
        fun newInstance(): FolderFragment = FolderFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_folder, container, false)
        EventBus.getDefault().register(this)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        initSource()
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                folder_title.btn_title_bar_function.id -> (activity as ImagePickerActivity).finishSelect(ArrayList())
                else -> print("")
            }
        }
    }

    private fun initView() {
        folder_title.btn_title_bar_function.setOnClickListener(this)
        folder_title.tv_title_bar_title.text = getString(R.string.title_picture)
        folder_title.tv_title_back.visibility = View.GONE
        folder_title.btn_title_back.visibility = View.GONE
        rv_folder.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_folder.addItemDecoration(DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL_LIST))
        mFolderAdapter = FolderAdapter(mFolderList)
        rv_folder.adapter = mFolderAdapter
        mFolderAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val imageFolder = mFolderList[position]
                startFragment(imageFolder)
            }
        })
    }

    private fun initSource() {
        val thread = object : Thread() {
            override fun run() {
                AlbumUtils(context!!).getAlbums()
            }
        }
        thread.run()
    }

    private fun startFragment(imageFolder: ImageFolder) {
        val fragment = GalleryFragment.newInstance(imageFolder)
        val fTransaction = activity?.supportFragmentManager?.beginTransaction()
        fTransaction?.replace((view?.parent as ViewGroup).id, fragment, fragment.tag)
        fTransaction?.disallowAddToBackStack()
        fTransaction?.commitAllowingStateLoss()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun OnAlbumFinish(albumFinish: OnAlbumFinish) {
        val folderList = albumFinish.albums
        if (folderList != null) {
            mFolderList.clear()
            mFolderList.addAll(folderList)
            mFolderAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}