package ir.snapp.box.interview.task.utils

import android.annotation.SuppressLint
import android.content.Context

// context holder class
// this class is SingleTone and initialize
// from application class
class AppContext(val context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: AppContext? = null
        fun init(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = AppContext(context)
            }
        }

        fun get(): AppContext {
            return INSTANCE!!
        }
    }

}