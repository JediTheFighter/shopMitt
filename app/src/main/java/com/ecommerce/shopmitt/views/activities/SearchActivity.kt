package com.ecommerce.shopmitt.views.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dgreenhalgh.android.simpleitemdecoration.grid.GridDividerItemDecoration
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.databinding.ActivitySearchBinding
import com.ecommerce.shopmitt.models.ProductModel
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.views.adapters.ProductsAdapter
import io.reactivex.disposables.Disposable
import java.util.*

class SearchActivity : BaseActivity(), ProductsAdapter.ItemUpdate {

    private lateinit var adapter: ProductsAdapter

    private var disposableSearch: Disposable? = null

    private lateinit var binding: ActivitySearchBinding

    private val REQ_CODE_SPEECH_INPUT = 100

    var delay: Long = 1000 // 1 seconds after user stops typing

    var last_text_edit: Long = 0
    var handler = Handler()

    private var isApiCalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar(binding.toolbar.id)
        binding.toolbar.setNavigationIcon(R.drawable.arrow_back_black)
        binding.toolbar.elevation = 4f
        binding.toolbar.setNavigationOnClickListener { view: View? -> finish() }


        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                handler.removeCallbacks(input_finish_checker)
            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.length > 0) {
                    last_text_edit = System.currentTimeMillis()
                    handler.postDelayed(input_finish_checker, delay)
                } else {
                    Log.v("search", "show trending searches here")
                }
            }
        })

        binding.imgVoice.setOnClickListener {
            handleVoice()
        }
    }

    private fun handleVoice() {
        if (isNetworkAvailable())
            promptSpeechInput()
        else Toast.makeText(
            applicationContext,
            getString(R.string.no_internet_connection),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onResume() {
        super.onResume()
        if (isApiCalled) {
            adapter.notifyDataSetChanged()
        }
    }

    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt))
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(
                applicationContext,
                getString(R.string.speech_not_supported),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    binding.edtSearch.setText(result?.get(0))
                }
            }
        }
    }

    private val input_finish_checker = Runnable {
        if (System.currentTimeMillis() > last_text_edit + delay - 500) {
            search()
        }
    }

    private fun search() {

        if (isValid()) {
            callSearch()
        }
    }

    private fun callSearch() {
        showLoadingDialog()
        disposableSearch = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as ProductModel
                isApiCalled = true
                handleSearch(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getProductsBySearch(binding.edtSearch.text.toString())
    }

    private fun handleSearch(model: ProductModel) {

        adapter = ProductsAdapter(onItemClicked = {
            val intent = Intent(this,DetailsActivity::class.java)
            intent.putExtra("product_id",it.productId)
            startActivity(intent)
        },this)

        adapter.items = model.data as MutableList<ProductModel.Data>

        binding.rvSearchResults.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResults.adapter = adapter
        binding.rvSearchResults.isNestedScrollingEnabled = false

        if(adapter.itemCount == 0) {
            binding.tvMsg.visibility = View.VISIBLE
        }
    }

    private fun isValid(): Boolean {
        if (binding.edtSearch.text.toString().isEmpty())
            Toast.makeText(
                this,
                R.string.enter_keyword,
                Toast.LENGTH_SHORT
            ).show()
        return binding.edtSearch.text.toString().isNotEmpty()
    }

    override val fragmentContainer: Int
        get() = 0

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableSearch)
    }

    override fun onUpdate() {

    }
}