package com.ecommerce.shopmitt.views.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.databinding.FragmentDialogUpdateProfileBinding
import com.ecommerce.shopmitt.databinding.FragmentQuantityDialogBinding
import com.ecommerce.shopmitt.models.DetailsModel
import com.ecommerce.shopmitt.models.ProfileActivity
import com.ecommerce.shopmitt.models.ProfileModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.ToastHelper
import com.ecommerce.shopmitt.views.adapters.ProductQuantityAdapter
import com.google.gson.JsonObject

class QuantityDialogFragment: DialogFragment() {

    private lateinit var binding: FragmentQuantityDialogBinding

    private lateinit var product: DetailsModel.Data

    private lateinit var optionBridge: OptionBridge

    companion object {

        const val PRODUCT = "product"

        fun newInstance(profile: DetailsModel.Data): QuantityDialogFragment {
            val args = Bundle()
            args.putParcelable(PRODUCT, profile)
            val fragment = QuantityDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentQuantityDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        product = arguments?.getParcelable(PRODUCT)!!
        setUpUi()
    }

    private fun setUpUi() {

        val adapter = ProductQuantityAdapter(onItemClicked = { position: Int ->
            optionBridge.onOptionSelected(position)
            dismiss()
        },product)

        adapter.items = product.options[0].optionValue as ArrayList<DetailsModel.Data.Option.OptionValue>

        binding.rvQuantity.layoutManager = LinearLayoutManager(activity)
        binding.rvQuantity.adapter = adapter

        Log.i("TEST",adapter.itemCount.toString() +" size, "+ product.name)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        optionBridge = context as OptionBridge
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    interface OptionBridge {
        fun onOptionSelected(pos: Int)
    }
}