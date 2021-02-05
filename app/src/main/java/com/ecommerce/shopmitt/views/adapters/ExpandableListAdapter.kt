package com.ecommerce.shopmitt.views.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.base.model.ExpandedMenuModel


class ExpandableListAdapter(
    var context: Context, var listDataHeader: List<ExpandedMenuModel>,
    var listChildData: HashMap<ExpandedMenuModel, List<String>>,
    var view: ExpandableListView
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return this.listDataHeader.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        var childCount = 0
        if (listChildData.size != 0)
            childCount = this.listChildData[this.listDataHeader[groupPosition]]!!.size

        return childCount
    }

    override fun getGroup(groupPosition: Int): Any? {
        return this.listDataHeader[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any? {
        Log.d(
            "CHILD", listChildData[this.listDataHeader[groupPosition]]!![childPosition].toString()
        )
        return this.listChildData[this.listDataHeader[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var view: View? = convertView
        val item = getGroup(groupPosition) as ExpandedMenuModel?
        if (view == null) {
            val viewInflater = this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = viewInflater.inflate(R.layout.row_nav_drawer, parent,false)
        }
        val title = view?.findViewById(R.id.navigation_title) as TextView
        val icon: ImageView = view.findViewById(R.id.navigation_icon) as ImageView
        title.text = item?.title
        item?.icon?.let { icon.setImageResource(it) }


        /*if (getChildrenCount(groupPosition) > 0) {
            if (isExpanded) {
                animateExpand(icon)
            } else {
                animateCollapse(icon)
            }
        }*/

        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var view = convertView
        val childText = getChild(groupPosition, childPosition) as String?
        if (view == null) {
            val viewInflater = this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = viewInflater.inflate(R.layout.row_child_drawer, parent,false)
        }
        val txtListChild = view?.findViewById<View>(R.id.navigation_title) as TextView
        val childIcon = view.findViewById<View>(R.id.navigation_icon) as ImageView
        txtListChild.text = childText
        return view
    }



    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }




    fun animateExpand(icon: ImageView?) {

        val rotate = RotateAnimation(360F, 180F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 300
        rotate.fillAfter = true
        icon?.animation = rotate
    }

    fun animateCollapse(icon: ImageView?) {

        val rotate = RotateAnimation(180F, 360F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 300
        rotate.fillAfter = true
        icon?.animation = rotate
    }

    fun animateExpandT(view: View) {

        val icon = view.findViewById<View>(R.id.navigation_icon) as ImageView
        val rotate = RotateAnimation(360F, 180F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 300
        rotate.fillAfter = true
        icon?.animation = rotate
    }

    fun animateCollapseT(view: View) {

        val icon = view.findViewById<View>(R.id.navigation_icon) as ImageView
        val rotate = RotateAnimation(180F, 360F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 300
        rotate.fillAfter = true
        icon?.animation = rotate
    }


}