package com.ecommerce.shopmitt.views.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.databinding.ActivityAddAddressBinding
import kotlinx.android.synthetic.main.toolbar.*

class AddAddressActivity : BaseActivity() {

    private lateinit var binding: ActivityAddAddressBinding

    enum class AddressType {
        Home,
        Office,
        Others
    }

    private var currentAddressType = AddressType.Home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()


        binding.rlHome.setOnClickListener {
            handleHomeBtnClick()
            currentAddressType = AddressType.Home
        }

        binding.rlOffice.setOnClickListener {
            handleOfficeBtnClick()
            currentAddressType = AddressType.Office
        }

        binding.rlOthers.setOnClickListener {
            handleOthersBtnClick()
            currentAddressType = AddressType.Others
        }
    }

    private fun handleOthersBtnClick() {
        disabledBtnState(binding.rlHome,binding.homeIcon,binding.homeText)
        disabledBtnState(binding.rlOffice,binding.officeIcon,binding.officeText)
        enabledBtnState(binding.rlOthers,binding.othersIcon,binding.othersText)
    }

    private fun handleOfficeBtnClick() {
        disabledBtnState(binding.rlHome,binding.homeIcon,binding.homeText)
        enabledBtnState(binding.rlOffice,binding.officeIcon,binding.officeText)
        disabledBtnState(binding.rlOthers,binding.othersIcon,binding.othersText)
    }

    private fun handleHomeBtnClick() {
        enabledBtnState(binding.rlHome,binding.homeIcon,binding.homeText)
        disabledBtnState(binding.rlOffice,binding.officeIcon,binding.officeText)
        disabledBtnState(binding.rlOthers,binding.othersIcon,binding.othersText)
    }

    private fun enabledBtnState(lyt: RelativeLayout,img: ImageView, text: TextView) {

        lyt.setBackgroundColor(ContextCompat.getColor(this,R.color.colorAccent))
        img.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN)
        text.setTextColor(ContextCompat.getColor(this,R.color.colorWhite))
    }

    private fun disabledBtnState(lyt: RelativeLayout,img: ImageView, text: TextView) {

        lyt.setBackgroundColor(ContextCompat.getColor(this,R.color.colorWhite))
        img.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN)
        text.setTextColor(ContextCompat.getColor(this,R.color.colorText))
    }

    private fun setToolbar() {
        setUpToolbar(R.id.toolbar)
        setUpHomeUpBackNavigation(R.drawable.arrow_back)
        tvToolbarTitle.text = "Delivery Info"
    }

    override val fragmentContainer: Int
        get() = 0
}