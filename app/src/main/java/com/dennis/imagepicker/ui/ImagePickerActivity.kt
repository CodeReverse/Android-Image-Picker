package com.dennis.imagepicker.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.dennis.imagepicker.R
import com.dennis.imagepicker.Setting
import com.dennis.imagepicker.model.SelectedImageInfo
import kotlinx.android.synthetic.main.activity_picker.*

class ImagePickerActivity : FragmentActivity() {
    companion object {
        const val EXTRA_KEY_INIT_DATA = "EXTRA_KEY_INIT_DATA"
        const val EXTRA_KEY_DATA_SOURCE = "EXTRA_KEY_DATA_SOURCE"
        const val EXTRA_KEY_DATA_SELECTED = "EXTRA_KEY_DATA_SELECTED"
        const val EXTRA_KEY_BROWSE_INDEX = "EXTRA_KEY_BROWSE_INDEX"

        const val TAG_PICASSO = "Gallery"
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
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(String.format(getString(R.string.tips), Setting.MAX_SELECTED))
        dialogBuilder.setPositiveButton(getString(R.string.dialog_text_ok), object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                p0?.dismiss()
            }
        })
        dialogBuilder.create().show()
    }

    fun finishSelect(pictures: ArrayList<SelectedImageInfo>) {

        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishSelect(ArrayList())
    }

}