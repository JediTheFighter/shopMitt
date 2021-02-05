package com.ecommerce.shopmitt.views.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.extentions.autoNotify
import com.ecommerce.shopmitt.models.DetailsModel
import com.ecommerce.shopmitt.utils.Constants.CURRENCY
import kotlin.math.abs
import kotlin.properties.Delegates

class ProductQuantityAdapter(
    val onItemClicked: (Int) -> Unit,
    val products: DetailsModel.Data
) :
    RecyclerView.Adapter<ProductQuantityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        val quantity: TextView = itemView.findViewById(R.id.tv_quantity)
        val price: TextView = itemView.findViewById(R.id.tv_price)
        val mrp: TextView = itemView.findViewById(R.id.tv_mrp)
        val item: LinearLayout = itemView.findViewById(R.id.ll_item)
        val off: TextView = itemView.findViewById(R.id.tv_off)
    }

    var items: MutableList<DetailsModel.Data.Option.OptionValue> by Delegates.observable(mutableListOf()) { _, oldList, newList ->
        autoNotify(oldList, newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_option, parent, false)
        viewHolder = ViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.quantity.text = products.options[0].optionValue[position].name

        val optionValue: DetailsModel.Data.Option.OptionValue = products.options[0].optionValue[position]


        val base_price: Double
        var base_special = 0.0
        var option_base = 0.0
        var option_special = 0.0
        var mrp_special: Double
        var total: String? = null
        var mrp: String? = null
        var has_base_special = false
        var total_int: Int
        val mrp_int: Int

        // Product Base Original Price

        // Product Base Original Price
        base_price = products.price.toDouble()


        if (products.special != null && !products.special.equals("false")) {
            has_base_special = true
            base_special = products.special!!.toDouble()
        }


        val option_base_val: String =
            products.options[0].optionValue[position].price
        val option_special_val: String? =
            products.options[0].optionValue[position].specialPrice

        if (option_special_val != null && option_special_val != "false") {
            mrp = (base_price + option_base_val!!.toDouble()).toString()
            option_special = option_special_val.toDouble()
            total = if (!has_base_special) {
                (base_price + option_special).toString()
            } else {
                (base_special + option_special).toString()
            }
            total_int = total.toDouble().toInt()
            mrp_int = mrp.toDouble().toInt()
            holder.price.setText(CURRENCY + total_int)
            holder.mrp.paintFlags = holder.mrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.mrp.setText(CURRENCY + mrp_int)
            if (mrp_int == total_int) {
                holder.mrp.visibility = View.GONE
                holder.off.visibility = View.GONE
            } else {
                holder.off.visibility = View.VISIBLE
                holder.off.setText(getOff(mrp_int, total_int).toString() + "% OFF")
            }
        } else {
            if (option_base_val != null && option_base_val != "false") {
                option_base = option_base_val.toDouble()
                mrp = (base_price + option_base_val.toDouble()).toString()
                mrp_int = mrp.toDouble().toInt()
                if (!has_base_special) {
                    total = (base_price + option_base).toString()
                } else {
                    total = (base_special + option_base).toString()
                    total_int = total.toDouble().toInt()
                    holder.off.visibility = View.VISIBLE
                    holder.off.text = getOff(mrp_int, total_int).toString() + "% OFF"
                    holder.mrp.paintFlags = holder.mrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    holder.mrp.text = CURRENCY + mrp_int
                }
                total_int = total.toDouble().toInt()
                holder.price.text = CURRENCY + total_int
            } else {
                total_int = base_price.toInt()
                holder.price.text = CURRENCY + total_int
            }
        }

        holder.item.setOnClickListener {
           // option selected
            onItemClicked(position)
        }
    }

    override fun getItemCount() = items.size

    private fun getOff(mrp_int: Int, total_int: Int): Int {
        val off: Double = (mrp_int - total_int).toDouble() / mrp_int * 100
        return abs(off).toInt()
    }

}