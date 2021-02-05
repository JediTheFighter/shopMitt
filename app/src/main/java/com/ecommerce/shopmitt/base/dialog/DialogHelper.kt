package com.ecommerce.shopmitt.base.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.ecommerce.shopmitt.R
import java.util.*

class DialogHelper(context: Context, private val dialogParams: DialogParams,
                   private val dgCallback: DialogCallback
) {

    private val dialog = Dialog(context, R.style.CommonDialogTheme)

    fun showDialog(showTitle: Boolean) {

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_alert)
        Objects.requireNonNull(dialog.window)
                ?.setBackgroundDrawableResource(android.R.color.transparent)

        val llButtons = dialog.findViewById<LinearLayout>(R.id.llButtons)
        val btnPositive = dialog.findViewById<Button>(R.id.btnPositive)
        val btnNegative = dialog.findViewById<Button>(R.id.btnNegative)
        val btnSingle = dialog.findViewById<Button>(R.id.btnSingle)
        val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
        val line = dialog.findViewById<View>(R.id.line)
        //val ivImage = dialog.findViewById<View>(R.id.ivImage)

        btnPositive.setOnClickListener {
            dgCallback.onButtonPositive(dialogParams.dialogId)
            dialog.dismiss()
        }

        btnNegative.setOnClickListener {
            dgCallback.onButtonNegative(dialogParams.dialogId)
            dialog.dismiss()
        }

        btnSingle.setOnClickListener {
            dgCallback.onButtonPositive(dialogParams.dialogId)
            dialog.dismiss()
        }

        tvMessage.text = dialogParams.message
        btnPositive.text = dialogParams.positive
        btnNegative.text = dialogParams.negative

        if (dialogParams.dgType == DialogType.DG_POS_ONLY) {
            //btnNegative.setVisibility(View.GONE);
            //btnPositive.setVisibility(View.GONE);
            llButtons.visibility = View.GONE
            btnSingle.visibility = View.VISIBLE
            btnSingle.text = dialogParams.positive
        }

        tvTitle.text = dialogParams.title
        line.visibility = View.VISIBLE

        if (showTitle) {
            tvTitle.visibility = View.VISIBLE
        }

        dialog.setCancelable(dialogParams.isCancelable())

        dialog.show()

    }


}