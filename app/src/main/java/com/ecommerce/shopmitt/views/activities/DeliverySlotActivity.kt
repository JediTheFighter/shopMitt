package com.ecommerce.shopmitt.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.activity.BaseActivity
import com.ecommerce.shopmitt.base.dialog.*
import com.ecommerce.shopmitt.databinding.ActivityDeliverySlotBinding
import com.ecommerce.shopmitt.models.DeliverySlotModel
import com.ecommerce.shopmitt.models.SlotTitleModel
import com.ecommerce.shopmitt.models.ViewCart
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.Constants.CURRENCY
import com.ecommerce.shopmitt.views.adapters.TimeSlotAdapter
import com.ecommerce.shopmitt.views.adapters.ViewPagerFragmentAdapter
import com.ecommerce.shopmitt.views.fragments.TimeSlotFragment
import io.reactivex.disposables.Disposable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DeliverySlotActivity : BaseActivity(), TimeSlotAdapter.TimeSlotBridge {

    private lateinit var pagerAdapter: ViewPagerFragmentAdapter

    private var datesList: ArrayList<DeliverySlotModel.Data.Date> = arrayListOf()

    private var disposableSlots: Disposable? = null

    private var count = ""
    private var total = ""

    private lateinit var binding: ActivityDeliverySlotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliverySlotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar("Select TimeSlot")

        callDeliverySlots()

        binding.proceed.setOnClickListener {
            showProceedDialog()
        }

        getExtrasData()
    }

    private fun getExtrasData() {

        if (intent.hasExtra("count"))
            count = intent.getStringExtra("count")!!
        if (intent.hasExtra("total"))
            total = intent.getStringExtra("total")!!

        if (count == "1")
            binding.txtCount.text = count + " " + getString(R.string.item)
        else
            binding.txtCount.text = count + " " + getString(R.string.items)

        binding.txtTotal.text = getString(R.string.total) + " " + CURRENCY + total
    }

    private fun showProceedDialog() {

        Log.i("TEST", getTimeSlotValue() + " is value")
        if (getTimeSlotValue() == null) {
            getToast().show("Please choose your delivery slot")
        } else {
            val params: DialogParams = Builder()
                .cancelable(false).dgType(DialogType.DG_POS_NEG)
                .title(getString(R.string.confirm_slot))
                .message(getString(R.string.do_you_want_proceed_with_slot))
                .positive(getString(R.string.yes))
                .negative(getString(R.string.no))
                .build()


            DialogHelper(this, params, object : DialogCallback {
                override fun onButtonPositive(dialogId: Int) {
                    moveToNext()
                }

                override fun onButtonNegative(dialogId: Int) {

                }
            }).showDialog(false)
        }
    }

    private fun moveToNext() {
        val intent = Intent(this, ShippingActivity::class.java)
        val list = getIntent().getParcelableArrayListExtra<ViewCart.Data.Total>("bill")
        intent.putExtra("date", getDateValue())
        intent.putExtra("time_slot", getTimeSlotValue())
        intent.putParcelableArrayListExtra("bill",list)
        startActivity(intent)
    }

    private fun callDeliverySlots() {
        showLoadingDialog()
        disposableSlots = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as DeliverySlotModel
                if (model.success == 1 && model.data != null) {
                    handleSlots(model.data)
                }
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
                getToast().show(statusMessage)
            }

        }, this).getDeliverySlots()
    }

    private fun handleSlots(data: DeliverySlotModel.Data) {

        pagerAdapter = ViewPagerFragmentAdapter(this, this)

        datesList.clear()

        datesList.addAll(data.dates)

        for (i in 0 until data.dates.size) {
            val slots = data.dates[i].timeSlots as ArrayList<DeliverySlotModel.Data.Date.TimeSlot>
            pagerAdapter.addFragment(TimeSlotFragment.newInstance(slots))
        }

        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        binding.viewPager.isUserInputEnabled = false

        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })


        val tabs = ArrayList<SlotTitleModel>()
        var newDateStr: String
        val form = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        var date: Date? = null
        for (i in 0 until data.dates.size) {

            try {
                date = form.parse(data.dates[i].date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val postFormater = SimpleDateFormat("dd MMM", Locale.ENGLISH)

            if (date != null)
                newDateStr = postFormater.format(date)
            else
                newDateStr = data.dates[i].date

            val tab = SlotTitleModel(data.dates[i].title, newDateStr)
            tabs.add(tab)
        }
        val adapter = TimeSlotAdapter(onItemClicked = {

        }, this)

        adapter.items = tabs

        binding.tabLayout.layoutManager = LinearLayoutManager(this)
        binding.tabLayout.adapter = adapter
        binding.tabLayout.isNestedScrollingEnabled = false

        adapter.selectFirstPos()

    }

    private fun getDateValue(): String {
        var date = ""
        Log.v("page no", "pager no : " + binding.viewPager.currentItem.toString())
        date = datesList[binding.viewPager.currentItem].date
        Log.v("response", "chosen date for delivery : $date")
        return date
    }

    private fun getTimeSlotValue(): String? {
        val timeSlotFragment =
            pagerAdapter.getCurrentFragment(binding.viewPager.currentItem) as TimeSlotFragment
        var time: String? = null

        if (timeSlotFragment.adapter.getTimeSlot() == null)
            return null
        else {
            time = timeSlotFragment.adapter.getTimeSlot()
            Log.v("response", "chosen time for delivery : $time")
            return time
        }

    }

    override val fragmentContainer: Int
        get() = 0

    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableSlots)
    }

    override fun onSelectTab(position: Int) {
        binding.viewPager.currentItem = position
    }
}