package com.ecommerce.shopmitt

import PreferencesManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.bumptech.glide.Glide
import com.dgreenhalgh.android.simpleitemdecoration.grid.GridDividerItemDecoration
import com.ecommerce.shopmitt.base.activity.CountMenuActivity
import com.ecommerce.shopmitt.base.dialog.*
import com.ecommerce.shopmitt.base.model.BaseModel
import com.ecommerce.shopmitt.base.model.ExpandedMenuModel
import com.ecommerce.shopmitt.databinding.ActivityMainBinding
import com.ecommerce.shopmitt.db.AppDatabase
import com.ecommerce.shopmitt.db.dao.CartDao
import com.ecommerce.shopmitt.db.entities.CartData
import com.ecommerce.shopmitt.models.BannerModel
import com.ecommerce.shopmitt.models.CategoryModel
import com.ecommerce.shopmitt.models.ProductModel
import com.ecommerce.shopmitt.models.ProfileActivity
import com.ecommerce.shopmitt.network.RestHelper
import com.ecommerce.shopmitt.network.RestResponseHandler
import com.ecommerce.shopmitt.utils.Constants
import com.ecommerce.shopmitt.utils.Constants.ABOUT_US
import com.ecommerce.shopmitt.utils.Constants.PAGE_ABOUT_US
import com.ecommerce.shopmitt.utils.Constants.PAGE_PRIVACY
import com.ecommerce.shopmitt.utils.Constants.PAGE_TERMS
import com.ecommerce.shopmitt.utils.Constants.PRIVACY_POLICY
import com.ecommerce.shopmitt.utils.Constants.TERMS_AND_CONDITIONS
import com.ecommerce.shopmitt.views.activities.*
import com.ecommerce.shopmitt.views.adapters.*
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.content_main.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : CountMenuActivity() {

    private var disposableLogout: Disposable? = null
    private var disposableBannerOne: Disposable? = null
    private var disposableBannerTwo: Disposable? = null
    private var disposableBannerThree: Disposable? = null
    private var disposableBannerFour: Disposable? = null
    private var disposableBannerFive: Disposable? = null
    private var disposableTopSection: Disposable? = null
    private var disposableMiddleSection: Disposable? = null
    private var disposableBottomSection: Disposable? = null
    private var disposableFeaturedBanners: Disposable? = null
    private var disposableCategories: Disposable? = null
    private var disposableTitles: Disposable? = null


    lateinit var mMenuAdapter: ExpandableListAdapter
    lateinit var listDataHeader: List<ExpandedMenuModel>
    lateinit var listDataChild: HashMap<ExpandedMenuModel, List<String>>

    private lateinit var cartDao: CartDao


    private lateinit var binding: ActivityMainBinding

    var page = 0
    var handler = Handler()
    private var timeCounter: Runnable = object : Runnable {
        override fun run() {
            if (binding.content.vpBanner.adapter != null) {
                if (binding.content.vpBanner.adapter?.count == page) {
                    page = 0
                } else {
                    page++
                }
                binding.content.vpBanner.setCurrentItem(page, true)
                handler.postDelayed(this, 3000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imgHamburger = findViewById<ImageView>(R.id.img_hamburger)
        setSupportActionBar(binding.content.toolbar.customToolbar)


        cartDao = AppDatabase.getDatabase(this).cartDao()

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)

        setupDrawerContent(navigationView)
        setHeaderView(headerView)

        prepareSideMenuList()


        mMenuAdapter = ExpandableListAdapter(
            this,
            listDataHeader,
            listDataChild,
            binding.navigationMenu
        )

        // setting list adapter
        binding.navigationMenu.setAdapter(mMenuAdapter)

        binding.navigationMenu.setOnChildClickListener { expandableListView, view, i, i1, l ->
            false
        }


        binding.navigationMenu.setOnGroupClickListener { expandableListView, view, i, l ->

            handleClick(i)
            false
        }

        imgHamburger.setOnClickListener {
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        callApis()

        binding.content.toolbar.menuSearch.setOnClickListener {
            navigator.navigate(this, SearchActivity::class.java)
        }

        binding.content.toolbar.menuFav.setOnClickListener {
            navigator.navigate(this, WishListActivity::class.java)
        }

        binding.logout.setOnClickListener {

            val params: DialogParams = Builder()
                .cancelable(false).dgType(DialogType.DG_POS_NEG)
                .title(getString(R.string.logout))
                .message(getString(R.string.are_you_sure_to_logout))
                .positive(getString(R.string.yes))
                .negative(getString(R.string.no))
                .build()


            DialogHelper(this, params, object : DialogCallback {
                override fun onButtonPositive(dialogId: Int) {
                    callLogout()
                }

                override fun onButtonNegative(dialogId: Int) {

                }
            }).showDialog(true)

        }

        if (PreferencesManager.instance.getString(PreferencesManager.KEY_CURR_LOCATION)!!.isNotEmpty())
            binding.content.toolbar.tvUserLocation.text = PreferencesManager.instance.getString(PreferencesManager.KEY_CURR_LOCATION)

        binding.content.toolbar.tvUserLocation.setOnClickListener {
            navigator.navigate(this,PickupLocationActivity::class.java)
        }

    }

    private fun handleClick(pos: Int) {
        when (pos) {
            0 -> { // Profile
                if (AppDataManager.instance.isLoggedIn) {
                    navigator.navigate(this, ProfileActivity::class.java)
                } else
                    getToast().show("Log in to continue..")
            }
            1 -> { // WishList
                if (AppDataManager.instance.isLoggedIn) {
                    navigator.navigate(this, WishListActivity::class.java)
                } else
                    getToast().show("Log in to continue..")
            }
            2 -> { // Orders
                if (AppDataManager.instance.isLoggedIn) {
                    navigator.navigate(this, OrderListActivity::class.java)
                } else
                    getToast().show("Log in to continue..")
            }
            3 -> { //Wallet

                if (AppDataManager.instance.isLoggedIn) {
                    navigator.navigate(this, WalletActivity::class.java)
                } else
                    getToast().show("Log in to continue..")

            }
            4 -> { //Refer n Earn

                if (AppDataManager.instance.isLoggedIn) {
                    navigator.navigate(this, ReferralActivity::class.java)
                } else
                    getToast().show("Log in to continue..")

            }
            5 -> { //ContactUs

                navigator.navigate(this, ContactUsActivity::class.java)
            }
            6 -> { // Privacy policy

                val intent = Intent(this, WebviewActivity::class.java)
                intent.putExtra("title", PAGE_PRIVACY)
                intent.putExtra("url", PRIVACY_POLICY)
                startActivity(intent)
            }
            7 -> { // Terms & conditions

                val intent = Intent(this, WebviewActivity::class.java)
                intent.putExtra("title", PAGE_TERMS)
                intent.putExtra("url", TERMS_AND_CONDITIONS)
                startActivity(intent)
            }
            8 -> { // About us
                val intent = Intent(this, WebviewActivity::class.java)
                intent.putExtra("title", PAGE_ABOUT_US)
                intent.putExtra("url", ABOUT_US)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
    }

    private fun callLogout() {
        showLoadingDialog()
        disposableLogout = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BaseModel
                AppDataManager.instance.clearLoginData()
                clearTable()
                refreshPage()
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).logout(logoutParams())
    }

    private fun clearTable() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {

                withContext(Dispatchers.IO) {
                    cartDao.clearTable()
                }
            }
        }
    }

    private fun refreshPage() {
        finish()
        startActivity(intent)
    }

    private fun logoutParams(): JsonObject {
        val body = JsonObject()
        body.addProperty("customer_id", AppDataManager.instance.customerId)
        return body
    }

    private fun callApis() {
        callTitles()
        callCategories()
        callBannerOne()
        callBannerTwo()
        callBannerThree()
        callTopSection()
        callMiddleSection()
        callBannerFour()
        callBottomSection()
        callBannerFive()
        callFeaturedBanners()
    }

    private fun callTitles() {
        showLoadingDialog()
        disposableTitles = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BannerModel
                handleTitles(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getHomeTitles()
    }

    private fun handleTitles(model: BannerModel) {
        binding.content.titleOne.text = model.data[0].imageOnly
        binding.content.titleTwo.text = model.data[1].imageOnly
        binding.content.titleThree.text = model.data[2].imageOnly
    }

    private fun callFeaturedBanners() {

        showLoadingDialog()
        disposableFeaturedBanners = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BannerModel
                handleFeaturedBanners(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getFeaturedBanners()
    }

    private fun handleFeaturedBanners(model: BannerModel) {

        val adapter = FeaturedBannerAdapter(onItemClicked = {
            callFeaturedProducts(it.link)
        })
        adapter.items = model.data as MutableList<BannerModel.Data>

        binding.content.root.rv_banner_sq_two.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.content.root.rv_banner_sq_two.adapter = adapter
        binding.content.root.rv_banner_sq_two.isNestedScrollingEnabled = false
    }

    private fun callFeaturedProducts(id: String) {
        showLoadingDialog()
        disposableFeaturedBanners = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as ProductModel
                handleFeaturedProducts(model.data)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getFeaturedProducts(id)
    }

    private fun handleFeaturedProducts(data: List<ProductModel.Data>) {
        val intent = Intent(this, ProductListActivity::class.java)
        intent.putExtra("from_featured", true)
        intent.putParcelableArrayListExtra("data", data as ArrayList<ProductModel.Data>)
        startActivity(intent)
    }

    private fun callBannerFive() {

        showLoadingDialog()
        disposableBannerFive = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BannerModel
                handleBannerFive(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getBannerPosFive()
    }

    private fun handleBannerFive(model: BannerModel) {

        val item = model.data[0]

        Glide.with(this)
            .load(item.image)
            .centerCrop()
            .into(binding.content.bannerStaticThree)

        binding.content.bannerStaticThree.setOnClickListener {

            val intent: Intent = if (item.type == "category" || item.type == "2")
                Intent(this, ProductListActivity::class.java)
            else
                Intent(this, DetailsActivity::class.java)

            intent.putExtra("title", item.title)
            intent.putExtra("id", item.link)
            intent.putExtra("product_id", item.link)
            intent.putExtra("sub_category_name", item.title)
            intent.putExtra("sub_category_id", item.link)
            startActivity(intent)
        }
    }

    private fun callBottomSection() {
        showLoadingDialog()
        disposableBottomSection = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BannerModel
                handleBottomSection(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getBottomSection()
    }

    private fun handleBottomSection(model: BannerModel) {

        val adapter = CategoryGridAdapter(onItemClicked = {
            if (it.productType) {
                val intent = Intent(this, ProductListActivity::class.java)
                intent.putExtra("sub_category_name", it.title)
                intent.putExtra("sub_category_id", it.categoryId)
                startActivity(intent)
            } else {
                val intent = Intent(this, SubCategoryActivity::class.java)
                intent.putExtra("category_id", it.categoryId)
                intent.putExtra("category_name", it.title)
                startActivity(intent)
            }
        })
        adapter.items = model.data as MutableList<BannerModel.Data>

        binding.content.root.rv_bottom_section.layoutManager = GridLayoutManager(
            this,
            3
        )
        binding.content.root.rv_bottom_section.adapter = adapter
        binding.content.root.rv_bottom_section.isNestedScrollingEnabled = false
    }

    private fun callBannerFour() {
        showLoadingDialog()
        disposableBannerFour = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BannerModel
                handleBannerFour(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getBannerPosFour()
    }

    private fun handleBannerFour(model: BannerModel) {
        val item = model.data[0]

        Glide.with(this)
            .load(item.image)
            .centerCrop()
            .into(binding.content.bannerStaticTwo)

        binding.content.bannerStaticTwo.setOnClickListener {

            val intent: Intent = if (item.type == "category" || item.type == "2")
                Intent(this, ProductListActivity::class.java)
            else
                Intent(this, DetailsActivity::class.java)

            intent.putExtra("title", item.title)
            intent.putExtra("id", item.link)
            intent.putExtra("product_id", item.link)
            intent.putExtra("sub_category_name", item.title)
            intent.putExtra("sub_category_id", item.link)
            startActivity(intent)
        }
    }

    private fun callMiddleSection() {

        showLoadingDialog()
        disposableBannerThree = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BannerModel
                handleMiddleSection(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getMiddleSection()
    }

    private fun handleMiddleSection(model: BannerModel) {
        val adapter = CategoryGridAdapter(onItemClicked = {
            if (it.productType) {
                val intent = Intent(this, ProductListActivity::class.java)
                intent.putExtra("sub_category_name", it.title)
                intent.putExtra("sub_category_id", it.categoryId)
                startActivity(intent)
            } else {
                val intent = Intent(this, SubCategoryActivity::class.java)
                intent.putExtra("category_id", it.categoryId)
                intent.putExtra("category_name", it.title)
                startActivity(intent)
            }
        })
        adapter.items = model.data as MutableList<BannerModel.Data>

        binding.content.root.rv_middle_section.layoutManager = GridLayoutManager(
            this,
            3
        )
        binding.content.root.rv_middle_section.adapter = adapter
        binding.content.root.rv_middle_section.isNestedScrollingEnabled = false
    }

    private fun callTopSection() {
        showLoadingDialog()
        disposableBannerThree = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BannerModel
                handleTopSection(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getTopSection()
    }

    private fun handleTopSection(model: BannerModel) {

        val adapter = CategoryGridAdapter(onItemClicked = {
            if (it.productType) {
                val intent = Intent(this, ProductListActivity::class.java)
                intent.putExtra("sub_category_name", it.title)
                intent.putExtra("sub_category_id", it.categoryId)
                startActivity(intent)
            } else {
                val intent = Intent(this, SubCategoryActivity::class.java)
                intent.putExtra("category_id", it.categoryId)
                intent.putExtra("category_name", it.title)
                startActivity(intent)
            }
        })
        adapter.items = model.data as MutableList<BannerModel.Data>

        binding.content.root.rv_top_section.layoutManager = GridLayoutManager(
            this,
            3
        )
        binding.content.root.rv_top_section.adapter = adapter
        binding.content.root.rv_top_section.isNestedScrollingEnabled = false
    }

    private fun callBannerThree() {
        showLoadingDialog()
        disposableBannerThree = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BannerModel
                handleBannerThree(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getBannerPosThree()
    }

    private fun handleBannerThree(model: BannerModel) {
        val item = model.data[0]

        Glide.with(this)
            .load(item.image)
            .centerCrop()
            .into(binding.content.bannerStaticOne)

        binding.content.bannerStaticOne.setOnClickListener {

            val intent: Intent = if (item.type == "category" || item.type == "2")
                Intent(this, ProductListActivity::class.java)
            else
                Intent(this, DetailsActivity::class.java)

            intent.putExtra("title", item.title)
            intent.putExtra("id", item.link)
            intent.putExtra("product_id", item.link)
            intent.putExtra("sub_category_name", item.title)
            intent.putExtra("sub_category_id", item.link)
            startActivity(intent)
        }
    }

    private fun callBannerTwo() {
        showLoadingDialog()
        disposableBannerTwo = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BannerModel
                handleBannerTwo(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getBannerPosTwo()
    }

    private fun handleBannerTwo(model: BannerModel) {

        val adapter = SquareBannerAdapter(onItemClicked = {

            val intent: Intent = if (it.type == "category" || it.type == "2")
                Intent(this, ProductListActivity::class.java)
            else
                Intent(this, DetailsActivity::class.java)

            intent.putExtra("title", it.title)
            intent.putExtra("id", it.link)
            intent.putExtra("product_id", it.link)
            intent.putExtra("sub_category_name", it.title)
            intent.putExtra("sub_category_id", it.link)
            startActivity(intent)
        })
        adapter.items = model.data as MutableList<BannerModel.Data>

        binding.content.root.rv_banner_sq_one.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.content.root.rv_banner_sq_one.adapter = adapter
        binding.content.root.rv_banner_sq_one.isNestedScrollingEnabled = false
    }

    private fun callBannerOne() {
        showLoadingDialog()
        disposableBannerOne = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as BannerModel
                handleBannerOne(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getBannerPosOne()
    }

    private fun handleBannerOne(model: BannerModel) {

        binding.content.vpBanner.adapter = HomeBannerAdapter(this, model.data)
        binding.content.tabDots.setupWithViewPager(binding.content.vpBanner)
        ViewCompat.setNestedScrollingEnabled(binding.content.vpBanner, false)
        timeCounter.run()

        binding.content.vpBanner.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                page = position
            }
        })
    }

    private fun callCategories() {
        showLoadingDialog()
        disposableCategories = RestHelper(object : RestResponseHandler {
            override fun onSuccess(`object`: Any?) {
                hideLoadingDialog()
                val model = `object` as CategoryModel
                handleCategories(model)
            }

            override fun onError(statusCode: Int, statusMessage: String?, retry: Boolean) {
                hideLoadingDialog()
            }

        }, this).getCategories()
    }

    private fun handleCategories(model: CategoryModel) {

        val adapter = TopCategoryAdapter(onItemClicked = {
            if (it.productType == "true") {
                val intent = Intent(this, ProductListActivity::class.java)
                intent.putExtra("sub_category_name", it.name)
                intent.putExtra("sub_category_id", it.categoryId)
                startActivity(intent)
            } else {
                val intent = Intent(this, SubCategoryActivity::class.java)
                intent.putExtra("category_id", it.categoryId)
                intent.putExtra("category_name", it.name)
                startActivity(intent)
            }
        })
        adapter.items = model.data as MutableList<CategoryModel.Data>

        binding.content.root.rv_categories.layoutManager = GridLayoutManager(this, 3)
        binding.content.root.rv_categories.adapter = adapter
        binding.content.root.rv_categories.isNestedScrollingEnabled = false

        val horizontalDivider = ContextCompat.getDrawable(this, R.drawable.line_divider)
        val verticalDivider = ContextCompat.getDrawable(this, R.drawable.line_divider)
        binding.content.root.rv_categories.addItemDecoration(
            GridDividerItemDecoration(horizontalDivider, verticalDivider, 3)
        )
    }

    private fun setHeaderView(headerView: View?) {

        val rlLogin: RelativeLayout? = headerView?.findViewById(R.id.loginLL)
        val login: TextView? = headerView?.findViewById(R.id.login)
        val signUp: TextView? = headerView?.findViewById(R.id.signUp)
        val userName: TextView? = headerView?.findViewById(R.id.user_name)

        if (AppDataManager.instance.isLoggedIn) {
            binding.logout.visibility = View.VISIBLE
            rlLogin?.visibility = View.GONE
            userName?.visibility = View.VISIBLE
            userName?.text = AppDataManager.instance.userName
        } else {
            binding.logout.visibility = View.GONE
            rlLogin?.visibility = View.VISIBLE
            userName?.visibility = View.GONE
        }


        binding.rlContact.setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=" + Constants.WHATSAPP_NO
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        login?.setOnClickListener {
            navigator.navigate(this, LoginActivity::class.java)
        }

        signUp?.setOnClickListener {
            navigator.navigate(this, RegistrationActivity::class.java)
        }
    }

    private fun prepareSideMenuList() {

//        ExpandedMenuModel(R.drawable.vc_down, "Mobile Accessories", true),
//        ExpandedMenuModel(R.drawable.vc_offer, "Saving Zone", false),
//        ExpandedMenuModel(R.drawable.vc_down, "Alis offers", true),
//        ExpandedMenuModel(0, "Special", false),

        /*if (i == 3 || i == 5) {  // expand icon code
            if (binding.navigationMenu.isGroupExpanded(i)) {
                mMenuAdapter.animateCollapseT(view)
            } else {
                mMenuAdapter.animateExpandT(view)
            }
        }*/

        listDataHeader = arrayListOf(
            ExpandedMenuModel(0, "Profile", false),
            ExpandedMenuModel(0, "WishList", false),
            ExpandedMenuModel(0, "My Orders", false),
            ExpandedMenuModel(0, "My Wallet", false),
            ExpandedMenuModel(0, "Refer & Earn", false),
            ExpandedMenuModel(0, "Contact Us", false),
            ExpandedMenuModel(0, "Privacy Policy", false),
            ExpandedMenuModel(0, "Terms & Conditions", false),
            ExpandedMenuModel(0, "About Us", false),
        )
        listDataChild = HashMap()


        // Adding child data
//        val heading1: MutableList<String> = ArrayList()
//        heading1.add("Iphone 12")
//        heading1.add("Galaxy Edge")
//        val heading2: MutableList<String> = ArrayList()
//        heading2.add("Summer bonanza")
//        heading2.add("Big billion day")

        val empty: MutableList<String> = ArrayList()

//        listDataChild[listDataHeader[3]] = empty // Header, Child data
//        listDataChild[listDataHeader[5]] = empty

        listDataChild[listDataHeader[0]] = empty
        listDataChild[listDataHeader[1]] = empty
        listDataChild[listDataHeader[2]] = empty
        listDataChild[listDataHeader[3]] = empty
        listDataChild[listDataHeader[4]] = empty
        listDataChild[listDataHeader[5]] = empty
        listDataChild[listDataHeader[6]] = empty
        listDataChild[listDataHeader[7]] = empty
        listDataChild[listDataHeader[8]] = empty
    }


    private fun setupDrawerContent(navigationView: NavigationView) {

        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    override val fragmentContainer: Int
        get() = 0


    override fun initCartItemCount() {
        super.initCartItemCount()
    }

    override suspend fun getCartItemCount(): Int {
        return cartDao.getTotalProductCount()
    }


    override fun onDestroy() {
        super.onDestroy()
        disposeApiCall(disposableCategories)
        disposeApiCall(disposableBannerOne)
        disposeApiCall(disposableBannerTwo)
        disposeApiCall(disposableBannerThree)
        disposeApiCall(disposableBannerFour)
        disposeApiCall(disposableBannerFive)
        disposeApiCall(disposableFeaturedBanners)
        disposeApiCall(disposableTopSection)
        disposeApiCall(disposableBottomSection)
        disposeApiCall(disposableMiddleSection)
        disposeApiCall(disposableLogout)
    }

}
