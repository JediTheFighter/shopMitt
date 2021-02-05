package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ecommerce.shopmitt.R

class CustomSpinnerAdapter(
    context: Context,
    resource: Int,
    list: List<String>,
    drop_res: Int,
    price: List<String>,
    mrp: List<String>
): ArrayAdapter<String>(context, resource, 0, list) {

    private var mInflater: LayoutInflater? = null
    private val mContext: Context? = null
    private var weight_list: List<String>? = null
    private var price_list: List<String>? = null
    private var mrp_list: List<String>? = null
    private var mResource = 0
    private var drop_down_res = 0

    init {
        mInflater = LayoutInflater.from(context)
        mResource = resource
        drop_down_res = drop_res
        price_list = price
        mrp_list = mrp
        weight_list = list
    }

    override fun getCount(): Int {
        return super.getCount()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position,convertView,parent)
    }
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createDropDownView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = mInflater!!.inflate(mResource, parent, false)
        val weight = view.findViewById<View>(R.id.text1) as TextView
        weight.text = weight_list!![position]
        return view
    }

    private fun createDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = mInflater!!.inflate(drop_down_res, parent, false)
        val weight = view.findViewById<View>(R.id.tv_quantity) as TextView
        val price = view.findViewById<View>(R.id.tv_price) as TextView
        val mrp = view.findViewById<View>(R.id.tv_mrp) as TextView
        val top = view.findViewById<View>(R.id.tv_top) as TextView
        val off = view.findViewById<View>(R.id.tv_off) as TextView
        mrp.paintFlags = mrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        weight.text = weight_list!![position]
        val mrp_int: Int
        val total_int: Int
        mrp_int = mrp_list!![position].toInt()
        total_int = price_list!![position].toInt()
        price.text = "₹$total_int"
        mrp.text = "₹$mrp_int"
        if (mrp_int == total_int) {
            mrp.visibility = View.GONE
            off.visibility = View.GONE
        } else {
            off.visibility = View.VISIBLE
            off.setText(getOff(mrp_int, total_int).toString() + "% OFF")
        }
        if (position == 0) top.visibility = View.VISIBLE
        return view
    }

    private fun getOff(mrp_int: Int, total_int: Int): Int {
        var off = 0.0
        off = (mrp_int - total_int).toDouble() / mrp_int * 100
        return Math.abs(off).toInt()
    }
}