package com.ecommerce.shopmitt.views.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.databinding.ActivityReferralBinding
import com.ecommerce.shopmitt.models.ReferralModel
import com.ecommerce.shopmitt.models.ReferralOffersModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.views.adapters.ReferralOffersAdapter
import io.reactivex.disposables.Disposable

class ReferralActivity : BaseActivity() {

    private val SHARE_CODE: Int = 4343

    private var disposableReferralCode: Disposable? = null
    private var disposableReferralOffers: Disposable? = null

    private lateinit var binding: ActivityReferralBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReferralBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar("Refer & Earn")

        getReferralCode()
        getReferralOffers()

        binding.btnRefer.setOnClickListener {
            shareCode()
        }

        binding.edtRefer.setOnClickListener {
            copyText()
        }
    }

    private fun shareCode() {
        val storeLink = "https://play.google.com/store/apps/details?id="
        val packageName = "com.ecommerce.shopmitt"
        val appLink = storeLink + packageName
        val appName = "ShopMitt"

        val shareBody = """Haven't tried $appName yet?  Download now and use my referral code : ${binding.edtRefer.text.toString()} to get the best quality of grocery products for the best prices. Download: $appLink"""

        val sharingIntent = Intent()
        sharingIntent.action = Intent.ACTION_SEND
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, appName)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivityForResult(
            Intent.createChooser(
                sharingIntent,
                resources.getText(R.string.app_name)
            ), SHARE_CODE
        )
    }

    private fun copyText() {
        val text: String = binding.edtRefer.text.toString()
        if (text.isNotEmpty()) {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("key", text)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(applicationContext, "Referral code has been copied to clipboard.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Referral code not generated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getReferralCode() {

        showLoadingDialog()
        disposableReferralCode = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as ReferralModel
                handleRefCode(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).getReferralCode()
    }

    private fun handleRefCode(data: ReferralModel.Data) {
        binding.edtRefer.setText(data.referral_code)

        if (data.placeholder != null && data.placeholder != "") {
            binding.staticBannerRef.visibility = View.VISIBLE

            Glide.with(this)
                .load(data.placeholder)
                .into(binding.staticBannerRef)
        } else
            binding.staticBannerRef.visibility = View.GONE
    }

    private fun getReferralOffers() {
        showLoadingDialog()
        disposableReferralOffers = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as ReferralOffersModel
                handleOffers(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).getReferralOffers()
    }

    private fun handleOffers(data: List<ReferralOffersModel.RewardOffers>) {

        val adapter = ReferralOffersAdapter(onItemClicked = {

        })

        adapter.items = data as ArrayList<ReferralOffersModel.RewardOffers>

        binding.rvRefferralOffers.layoutManager = LinearLayoutManager(this)
        binding.rvRefferralOffers.adapter = adapter
        binding.rvRefferralOffers.isNestedScrollingEnabled = false
    }

    override val fragmentContainer: Int
        get() = 0

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SHARE_CODE)
            getReferralCode()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableReferralCode)
        disposeApiCall(disposableReferralOffers)
    }
}