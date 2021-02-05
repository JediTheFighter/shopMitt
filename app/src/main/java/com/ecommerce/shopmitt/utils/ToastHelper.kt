package com.ecommerce.shopmitt.utils

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.ecommerce.shopmitt.R


class ToastHelper {

    /**
     * Shows Toast message
     */

    fun show(message: String?) {
        //Toast.makeText(AlisApplication.instance, message, Toast.LENGTH_SHORT).show();
        val inflater = LayoutInflater.from(AlisApplication.instance)
        val layout: View = inflater.inflate(R.layout.custom_toast_layout, null)
        val tvCustomToast = layout.findViewById<TextView>(R.id.tvCustomToast)
        tvCustomToast.text = message
        val toast = Toast(AlisApplication.instance)
        //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.duration = Toast.LENGTH_SHORT
        /*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 100);
        tvCustomToast.setLayoutParams(params);*/toast.view = layout
        toast.show()
    }

    private object HOLDER {
        val INSTANCE = ToastHelper()
    }

    companion object {
        //val instance: com.shopping.alis.utils.LogHelper = HOLDER.INSTANCE
        val instance: ToastHelper by lazy { HOLDER.INSTANCE }
    }

}