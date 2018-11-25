package com.dennis.imagepicker

import android.app.Application
import android.content.Context

class ContextHolder {

    companion object {
        private var mContext: Context? = null
        private var mainApplication: Application? = null

        fun getApplication(): Application? {
            return mainApplication
        }

        fun setApplication(application: Application) {
            mainApplication = application
        }

        fun setContext(context: Context) {
            mContext = context
        }

        fun getContext(): Context? {
            return mContext
        }
    }


}