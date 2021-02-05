package com.ecommerce.shopmitt.views.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.databinding.ActivityWebviewBinding
import com.ecommerce.shopmitt.models.WebViewModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.Constants.PAGE_ABOUT_US
import com.ecommerce.shopmitt.utils.Constants.PAGE_PRIVACY
import com.ecommerce.shopmitt.utils.Constants.PAGE_TERMS

class WebviewActivity : BaseActivity() {

    private lateinit var binding: ActivityWebviewBinding

    private lateinit var url: String

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getTitleFromIntent()
        url = intent.getStringExtra("url")!!

        binding.webView.settings.javaScriptEnabled = true


        /*binding.webView.settings.builtInZoomControls = true

        binding.webView.loadUrl(url)
        Log.i("WEBVIEW URL", url)

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                binding.loadingLayout.visibility = View.GONE
            }
        }*/
    }

    private fun getTitleFromIntent() {
        val type = intent.getIntExtra("title", 0)

        when (type) {
            PAGE_PRIVACY -> {
                setToolbar("Privacy Policy")
                callPP()
            }
            PAGE_TERMS -> {
                setToolbar("Terms & Conditions")
                callTNC()
            }
            PAGE_ABOUT_US -> {
                setToolbar("About Us")
                callAboutUs()
            }
        }
    }

    private fun callAboutUs() {

        binding.loadingLayout.visibility = View.VISIBLE
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                binding.loadingLayout.visibility = View.GONE
                val model = `object` as WebViewModel
                handleData(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                binding.loadingLayout.visibility = View.GONE
            }

        }, this).getAboutUs()
    }

    private fun callTNC() {
        binding.loadingLayout.visibility = View.VISIBLE
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                binding.loadingLayout.visibility = View.GONE
                val model = `object` as WebViewModel
                handleData(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                binding.loadingLayout.visibility = View.GONE
            }

        }, this).getTerms()
    }

    private fun callPP() {
        binding.loadingLayout.visibility = View.VISIBLE
        RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                binding.loadingLayout.visibility = View.GONE
                val model = `object` as WebViewModel
                handleData(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                binding.loadingLayout.visibility = View.GONE
            }

        }, this).getPrivacyPolicy()
    }

    private fun handleData(data: WebViewModel.Data?) {

        val html = Html.fromHtml(data?.description.toString())

        Log.d("HTML", "onResponse: " + data?.description.toString())
        binding.webView.loadDataWithBaseURL(null, html.toString(), "text/html", "UTF-8", null)
    }

    override val fragmentContainer: Int
        get() = 0
}