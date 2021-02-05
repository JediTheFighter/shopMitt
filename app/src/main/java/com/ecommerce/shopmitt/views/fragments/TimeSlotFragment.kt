package com.ecommerce.shopmitt.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecommerce.shopmitt.databinding.FragmentTimeSlotBinding
import com.ecommerce.shopmitt.models.DeliverySlotModel
import com.ecommerce.shopmitt.views.adapters.DeliveryTimeSlotAdapter

class TimeSlotFragment : Fragment() {

    lateinit var adapter: DeliveryTimeSlotAdapter

    private var dateList: ArrayList<DeliverySlotModel.Data.Date.TimeSlot> = arrayListOf()

    private lateinit var binding: FragmentTimeSlotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dateList = it.getParcelableArrayList(DATES)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTimeSlotBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DeliveryTimeSlotAdapter(onItemClicked = {

        })

        adapter.items = dateList

        binding.rvTimeslot.layoutManager = LinearLayoutManager(activity)
        binding.rvTimeslot.adapter = adapter
        binding.rvTimeslot.isNestedScrollingEnabled = false


        if (adapter.itemCount == 0) {
            binding.tvError.visibility = View.VISIBLE
        } else {
            binding.tvError.visibility = View.GONE
        }
    }

    companion object {

        private const val DATES = "dates"

        @JvmStatic
        fun newInstance(dates: ArrayList<DeliverySlotModel.Data.Date.TimeSlot>) =
            TimeSlotFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(DATES, dates)
                }
            }
    }
}