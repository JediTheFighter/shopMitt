package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.models.PlaceData
import com.ecommerce.shopmitt.utils.LogHelper
import com.ecommerce.shopmitt.utils.ToastHelper
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import kotlinx.android.synthetic.main.row_place_picker.view.*

class DestinationPlacePickerAdapter(val context: Context, var list: List<PlaceData>, val inMap: Boolean, var itemClickListener: SelectDestination): RecyclerView.Adapter<DestinationPlacePickerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_place_picker, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position],holder.itemView.context)

        val placeData = list[position]
        val placePrimaryText: String? = placeData.getPrimaryText()
        val placeSecondaryText: String? = placeData.getSecondaryText()
        holder.itemView.title.text = placePrimaryText
        holder.itemView.address!!.text = placeSecondaryText

        holder.itemView.llMain.setOnClickListener{

            val placeFields = listOf(Place.Field.LAT_LNG,
                    Place.Field.NAME, Place.Field.ADDRESS)
            val placeId: String = list[position].getPlaceId()
            val placeText: String = list[position].getPlaceText()
            val request = FetchPlaceRequest.builder(placeId, placeFields)
                    .build()
            val placesClient = Places.createClient(holder.itemView.context)
            val fetchPlaceResponseTask = placesClient.fetchPlace(request)
            fetchPlaceResponseTask.addOnSuccessListener { fetchPlaceResponse ->
                LogHelper.instance.printInfoLog("onSuccess: ")
                val place = fetchPlaceResponse.place
                val latLng = place.latLng
                val name = place.name
                val address = place.address
                LogHelper.instance.printInfoLog("latLng: $latLng")
                LogHelper.instance.printInfoLog("name: $name")
                LogHelper.instance.printInfoLog("address: $address")
                if (latLng != null) { //Pass selected place to activity
                    itemClickListener.onSelectDestination(latLng.latitude.toString(), latLng.longitude.toString(), name)
                } else {
                    ToastHelper.instance.show(context?.getString(R.string.place_not_found))
                }
            }.addOnFailureListener { e ->
                LogHelper.instance.printErrorLog("onFailure: ")
                e.printStackTrace()
                ToastHelper.instance.show(context.getString(R.string.place_not_found))
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val title = itemView.title
        private val tvAddress = itemView.address
        private val llmain = itemView.llMain

        fun bind(placeData: PlaceData, context: Context) {

        }
    }

    interface SelectDestination {
        fun onSelectDestination(lat: String?, lng: String?, place_name: String?)
    }

    
}