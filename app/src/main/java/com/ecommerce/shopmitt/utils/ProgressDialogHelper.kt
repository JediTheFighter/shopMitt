package com.ecommerce.shopmitt.utils

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.ecommerce.shopmitt.R

class ProgressDialogHelper(mContext: Context) {

    private val ROTATE_FROM = 0.0f
    private val ROTATE_TO = 359.0f

    private var dialog: AppCompatDialog? = null
    private var context: Context? = null

    private var ivProcessing: ImageView? = null
    private var tvProgressMessage: TextView? = null
    private var rotation: RotateAnimation? = null

    init {
        this.context = mContext
    }

    fun create() {
        if (dialog == null) {
            dialog = AppCompatDialog(context,
                    R.style.ProgressDialogStyle)
            dialog?.setContentView(R.layout.layout_progress_dialog)
            ivProcessing = dialog!!.findViewById(R.id.ivProcessing)
            tvProgressMessage = dialog!!.findViewById(R.id.tvProgressMessage)
            if (ivProcessing != null) {
                ivProcessing!!.isDrawingCacheEnabled = true
            }
            dialog!!.setCancelable(false)
            rotation = RotateAnimation(ROTATE_FROM, ROTATE_TO,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f)
            rotation!!.duration = 1000
            rotation!!.interpolator = LinearInterpolator()
            rotation!!.repeatCount = Animation.INFINITE
            //ivProcessing.startAnimation(rotation);
        } else {
            LogHelper.instance.printErrorLog("Progress dialog already created.")
        }
    }

    fun show() {
        if (dialog != null && !dialog!!.isShowing) {
            ivProcessing!!.startAnimation(rotation)
            dialog!!.show()
        }
    }

    fun show(message: String?) {
        if (dialog != null && !dialog!!.isShowing) {
            tvProgressMessage!!.text = message
            tvProgressMessage!!.visibility = View.VISIBLE
            ivProcessing!!.startAnimation(rotation)
            dialog!!.show()
        }
    }

    fun hide() {
        if (dialog != null && dialog!!.isShowing) {
            ivProcessing!!.clearAnimation()
            tvProgressMessage!!.text = null
            tvProgressMessage!!.visibility = View.GONE
            dialog!!.dismiss()
        }
    }

    fun dispose() {
        hide()
        dialog = null
        context = null
    }

}