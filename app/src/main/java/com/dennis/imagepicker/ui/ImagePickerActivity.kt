package com.dennis.imagepicker.ui

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.dennis.imagepicker.R
import com.dennis.imagepicker.model.SelectedImageInfo
import kotlinx.android.synthetic.main.activity_picker.*

class ImagePickerActivity : FragmentActivity() {
    companion object {
        const val EXTRA_KEY_INIT_DATA = "EXTRA_KEY_INIT_DATA"
        const val EXTRA_KEY_DATA_SOURCE = "EXTRA_KEY_DATA_SOURCE"
        const val EXTRA_KEY_DATA_SELECTED = "EXTRA_KEY_DATA_SELECTED"
        const val EXTRA_KEY_BROWSE_INDEX = "EXTRA_KEY_BROWSE_INDEX"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)
        val fragment = GalleryFragment.newInstance(null)
        val fTransaction = supportFragmentManager.beginTransaction()
        fTransaction.add(fragment_container.id, fragment, fragment.tag)
        fTransaction.disallowAddToBackStack()
        fTransaction.commit()
    }

    fun showAlert() {
//        val confirm = IOSConfirm.Builder(this).setMessage(mMultiImagePick.tip).setPositiveButton(getContextWrapper().resources.getString(R.string.dialog_text_ok), { dialog, _ -> dialog.dismiss() }).createAlert()
//        confirm.setCancelable(false)
//        if (!this.supportFragmentManager.findFragmentById(fragment_container.id).isDetached) {
//            confirm.show()
//        }
    }

    fun finishSelect(pictures: ArrayList<SelectedImageInfo>) {

        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishSelect(ArrayList())
    }

}