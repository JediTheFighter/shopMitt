package com.ecommerce.shopmitt.base.activity

import PreferencesManager
import android.animation.ObjectAnimator
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.views.activities.CartActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class CountMenuActivity : BaseActivity() {

    override val fragmentContainer: Int
        get() = 0

    var cart_view: View? = null
    var tv_cart_item_count: TextView? = null
    var count = 0

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        // Inflate the menu; this adds items to the action bar if it is present.
        initCartView(menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun initCartView(menu: Menu?) {

        menu?.add(Menu.NONE, MENU_CART, 2, "Cart")?.setIcon(R.drawable.vc_cart)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        val menuItemCreateCart = menu?.findItem(MENU_CART)
        if (menuItemCreateCart == null) {
//            menuItemCreateCart  = menu.add(0, R.id.menuItemCreateCart, 0, R.string.Create);
        }
        cart_view = layoutInflater.inflate(R.layout.cart_menu_layout, null)
        tv_cart_item_count = cart_view!!.findViewById<View>(R.id.tv_cart_items) as TextView
        tv_cart_item_count!!.text = "0"
        tv_cart_item_count!!.visibility =
            if (tv_cart_item_count!!.text == "0") View.GONE else View.VISIBLE
        // tv_cart_item_count.setVisibility(View.GONE);
        cart_view!!.setOnClickListener {
            initCartItemCount()
            onClickCart()
            //                        manageCart();
            //                        startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
        }
        initCartItemCount()
        if (menuItemCreateCart != null) {
            menuItemCreateCart.actionView = cart_view
        }
    }

    open fun initCartItemCount() {

        if (tv_cart_item_count == null) return

        GlobalScope.launch {
            withContext(Dispatchers.Main) {

                val count: Int = getCartItemCount()
                PreferencesManager.instance.setInt(PreferencesManager.KEY_CART_COUNT, count)
                tv_cart_item_count!!.visibility = if (count == 0) View.GONE else View.VISIBLE
                tv_cart_item_count!!.text = count.toString() + ""
                ObjectAnimator.ofFloat(tv_cart_item_count, "rotationY", 360f, 0f).setDuration(2000).start()
            }
        }
        return
    }

    open suspend fun getCartItemCount(): Int {
        return 0
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val appPackageName = packageName
        when (item.itemId) {
            MENU_CART -> onClickCart()
        }
        return super.onOptionsItemSelected(item)
    }



    private fun onClickCart() {
        startActivity(Intent(this, CartActivity::class.java))
    }

    companion object {
        private const val MENU_CART = Menu.FIRST + 100000
    }
}