package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.models.DetailsModel
import com.ecommerce.shopmitt.utils.Constants.CURRENCY
import com.ecommerce.shopmitt.views.activities.DetailsActivity

class RelatedProductsAdapter(
    val context: Context,
    val list: ArrayList<DetailsModel.Data.Related>
) : RecyclerView.Adapter<RelatedProductsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        val tv_prod_name: TextView = view.findViewById(R.id.tv_prod_name)
        val tv_prod_price: TextView = view.findViewById(R.id.tv_prod_price)
        val tv_prod_sp_price: TextView = view.findViewById(R.id.tv_prod_sp_price)
        val img_prod: ImageView = view.findViewById(R.id.img_prod)
        val root: View = view.findViewById(R.id.root)

        fun bind(item: DetailsModel.Data.Related) {
            val cat_name: String = item.name
            tv_prod_name.text = cat_name.replace("&amp;".toRegex(), "&")


            val total_int: Int
            val mrp_int: Int
            if (item.options.isNotEmpty()) {
                val optionDetails: DetailsModel.Data.Related.RelatedOption.OptionValue = 
                    item.options.get(0).optionValue.get(0)


                //////////// option price logic
                val base_price: Double
                var base_special = 0.0
                var option_base = 0.0
                var option_special = 0.0
                var has_base_special = false
                var total: String? = null
                var mrp: String? = null

                // Product Base Original Price
                base_price = item.price.toDouble()

                if (item.special != null && !item.special
                        .equals("false")
                ) {
                    has_base_special = true
                    base_special = item.special.toDouble()
                }
                tv_prod_sp_price.paintFlags =
                    tv_prod_sp_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                val option_base_val: String = optionDetails.price
                val option_special_val: String? = optionDetails.specialPrice
                if (option_special_val != null && option_special_val != "false") {
                    tv_prod_sp_price.visibility = View.VISIBLE
                    mrp = (base_price + option_base_val!!.toDouble()).toString()
                    option_special = option_special_val.toDouble()
                    total = if (!has_base_special) {
                        (base_price + option_special).toString()
                    } else {
                        (base_special + option_special).toString()
                    }
                    mrp_int = mrp.toDouble().toInt()
                    total_int = total.toDouble().toInt()
                    tv_prod_sp_price.setText(CURRENCY + mrp_int)
                    tv_prod_price.setText(CURRENCY + total_int)
                    if (mrp == total) {
                        tv_prod_sp_price.visibility = View.GONE
                    }
                } else {
//                    tv_prod_sp_price.setVisibility(GONE);
                    if (option_base_val != null && option_base_val != "false") {
                        option_base = option_base_val.toDouble()
                        mrp = (base_price + option_base_val.toDouble()).toString()
                        mrp_int = mrp.toDouble().toInt()
                        if (!has_base_special) {
                            total = (base_price + option_base).toString()
                            tv_prod_sp_price.visibility = View.GONE
                        } else {
                            total = (base_special + option_base).toString()
                            tv_prod_sp_price.visibility = View.VISIBLE
                            tv_prod_sp_price.setText(CURRENCY + mrp_int)
                        }
                        total_int = total.toDouble().toInt()
                        tv_prod_price.setText(CURRENCY + total_int)
                    } else {
                        total_int = base_price.toInt()
                        tv_prod_price.setText(CURRENCY + total_int)
                    }
                }

                /////////////////////////
            } else {
                val base_price: String
                val base_special: String
                Log.v("Special:", "Special " + item.special)
                if (item.special != null && !item.special
                        .equals("false")
                ) {
                    base_special = item.special
                    base_price = java.lang.String.valueOf(item.price)
                    tv_prod_sp_price.paintFlags =
                        tv_prod_sp_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    tv_prod_sp_price.visibility = View.VISIBLE
                    mrp_int = base_price.toDouble().toInt()
                    total_int = base_special.toDouble().toInt()
                    tv_prod_sp_price.setText(CURRENCY + mrp_int)
                    tv_prod_price.setText(CURRENCY + total_int)
                } else {
                    base_price = java.lang.String.valueOf(item.price)
                    total_int = base_price.toDouble().toInt()
                    tv_prod_price.setText(CURRENCY + total_int)
                    tv_prod_sp_price.visibility = View.GONE
                }
            }


//            tv_prod_weight.setText(item.getRating().toString());


//            tv_prod_weight.setText(item.getRating().toString());
            Glide.with(itemView.context)
                .load(item.image) // .transform(new CircleTransform(context))
                .centerCrop()
                .into(img_prod)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_related_products, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.bind(item)

        holder.root.setOnClickListener {
            moveToDetailPage(item)
        }

    }

    private fun moveToDetailPage(item: DetailsModel.Data.Related) {
        val intent = Intent(context,DetailsActivity::class.java)
        intent.putExtra("product_id",item.productId)
        context.startActivity(intent)
        (context as DetailsActivity).finish()
    }

    override fun getItemCount() = list.size
}