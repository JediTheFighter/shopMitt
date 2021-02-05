package com.ecommerce.shopmitt.base.activity

import DetailsTransition
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.transition.Fade
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.ecommerce.shopmitt.AppDataManager
import com.ecommerce.shopmitt.R
import com.ecommerce.shopmitt.utils.AlisApplication
import com.ecommerce.shopmitt.utils.Navigator
import com.ecommerce.shopmitt.utils.ProgressDialogHelper
import com.ecommerce.shopmitt.utils.ToastHelper
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.toolbar.*

/**
 * The class BaseActivity, Abstract android activity common for all activities.
 * Every activity extends this abstract class.
 * A common Tool bar is designed in this activity. Common
 * functions for activities can be created here.
 */

abstract class BaseActivity : AppCompatActivity() {

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    protected var toolbar: Toolbar? = null
    private var titleFont: Typeface? = null
    private var progressDialog: ProgressDialogHelper? = null
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        titleFont = ResourcesCompat.getFont(this, R.font.regular)
        progressDialog = ProgressDialogHelper(this)
        progressDialog!!.create()
    }





    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null /*&&
                !this.getClass().getSimpleName()
                        .equals(TestActivity.class.getSimpleName())*/) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    // <<<<<<<<<<<<<<<< Fragment helper functions >>>>>>>>>>>>>>>>>>>>> //
    protected fun replaceFragment(fragment: Fragment, addToBackStack: Boolean, animate: Boolean) {
        try {
            val fragmentTransaction = supportFragmentManager
                    .beginTransaction()
            if (animate) { //fragmentTransaction.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_left);
                fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_left,
                    R.anim.slide_in_right,
                    R.anim.slide_out_right
                )
            }
            if (addToBackStack) {
                fragmentTransaction.addToBackStack(null)
            }
            fragmentTransaction.replace(
                fragmentContainer, fragment, fragment
                    .javaClass.simpleName
            )
            //fragmentTransaction.commit();
            fragmentTransaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun replaceFragmentWithFadeAnimation(
        fragment: Fragment,
        addToBackStack: Boolean
    ) {
        val fragmentTransaction = supportFragmentManager
                .beginTransaction()
        fragmentTransaction.setCustomAnimations(
            android.R.anim.fade_in,
            android.R.anim.fade_out,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.replace(
            fragmentContainer, fragment, fragment
                .javaClass.simpleName
        )
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss()
    }

    @SafeVarargs
    protected fun replaceFragmentWithTransition(
        fragment: Fragment,
        vararg sharedElements: Pair<View?, String?>
    ) {
        fragment.sharedElementEnterTransition = DetailsTransition()
        fragment.enterTransition = Fade()
        fragment.exitTransition = Fade()
        fragment.sharedElementReturnTransition = DetailsTransition()
        val fragmentTransaction = supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(null)
        for (sharedElement in sharedElements) {
            fragmentTransaction.addSharedElement(sharedElement.first!!, sharedElement.second!!)
        }
        fragmentTransaction.replace(
            fragmentContainer, fragment, fragment
                .javaClass.simpleName
        )
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss()
    }





    protected val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(
            fragmentContainer
        )

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    private fun isFragmentShown(currentFragment: Fragment): Boolean {
        return (currentFragment.isAdded && !currentFragment.isDetached
                && !currentFragment.isRemoving)
    }

    protected fun popBackStack() {
        supportFragmentManager.popBackStack()
    }

    protected fun removeAllBackStacks() {
        supportFragmentManager.popBackStackImmediate(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    protected val backStackCount: Int
        get() = supportFragmentManager.backStackEntryCount

    // <<<<<<<<<<<<<<<< end of Fragment helper functions >>>>>>>>>>>>>>>>>>>>> //


// <<<<<<<<<<<<<<<< Toolbar helper functions >>>>>>>>>>>>>>>>>>>>> //

    protected fun setToolbar(title: String) {
        setUpToolbar(R.id.toolbar)
        setUpHomeUpBackNavigation(R.drawable.arrow_back)
        tvToolbarTitle.text = title
    }

    protected fun setUpToolbar(@IdRes toolbar_id: Int) {
        toolbar = findViewById<View>(toolbar_id) as Toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }
    }

    protected fun setHomeAsUp(@DrawableRes resourceId: Int) {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(resourceId)
        }
    }

    protected fun setLogo(@DrawableRes resourceId: Int) {
        toolbar!!.setLogo(resourceId)
    }

    protected fun changeToolbarColor(@ColorInt color: Int) {
        toolbar!!.setBackgroundColor(color)
    }

    protected fun hideToolBar() {
        if (supportActionBar != null && supportActionBar!!.isShowing) supportActionBar!!.hide()
    }

    protected fun showToolBar() {
        if (supportActionBar != null && !supportActionBar!!.isShowing) supportActionBar!!.show()
    }

    protected fun setAppTitle(@StringRes title: Int) {
        if (supportActionBar != null) supportActionBar!!.setTitle(title)
    }

    protected fun hideAppTitle() {
        if (supportActionBar != null) supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    protected fun showAppTitle() {
        if (supportActionBar != null) supportActionBar!!.setDisplayShowTitleEnabled(true)
    }

    protected fun setUpHomeUpBackNavigation(@DrawableRes resourceId: Int) {
        // toolbar ic_home button click event listener.
        toolbar!!.setNavigationOnClickListener { onHomeUpBackNavigationClicked() }
        toolbar!!.setNavigationIcon(resourceId)
    }

    protected fun onHomeUpBackNavigationClicked() {
        onBackPressed()
    }

    protected fun hideBackNavigation() {
        toolbar!!.navigationIcon = null
    }

    protected fun toggleAllActionItemsVisibility(menu: Menu?, visible: Boolean) {
        if (menu != null) {
            for (i in 0 until menu.size()) {
                menu.getItem(i).isVisible = visible
            }
        }
    }

    protected fun toggleActionItemVisibility(item: MenuItem?, visible: Boolean) {
        if (item != null) {
            item.isVisible = visible
        }
    }

    protected fun setAppTitle(title: String?) {
        if (supportActionBar != null) supportActionBar!!.title = title
    }

    protected fun setToolbarSubTitle(subTitle: String?) {
        if (supportActionBar != null) supportActionBar!!.subtitle = subTitle
    }

    protected fun setToolbarLogo(@DrawableRes image: Int) {
        if (supportActionBar != null) supportActionBar!!.setLogo(image)
    }

    protected fun setToolbarLogo(image: String?) {
        for (i in 0 until toolbar!!.childCount) {
            val view = toolbar!!.getChildAt(i)
            if (view is ImageView) {
                Glide.with(this).load(image) //.transform(new CircleTransform(this))
                        //.placeholder(R.drawable.img_user_default)
                        .into(view)
                break
            }
        }
    }

    // <<<<<<<<<<<<<<<< end of Toolbar helper functions >>>>>>>>>>>>>>>>>>>>> //
    abstract val fragmentContainer: Int

    val navigator: Navigator
        get() = AlisApplication.instance.navigator


    ////////////////////////////////

    fun showLoadingDialog() {
        progressDialog!!.show()
    }

    fun showLoadingDialog(message: String?) {
        progressDialog!!.show(message)
    }

    fun hideLoadingDialog() {
        progressDialog!!.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog!!.dispose()
    }

    private fun injectViews() {
    }

    /////
    private fun init() {
        injectViews()
    }


    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(layoutResID)
        init()
        animateActivityNavigation()
    }

    fun setToolbarTitleEndMargin(margin: Int) {
        val toolbarTitle = findViewById<TextView>(R.id.tvToolbarTitle)
        setMargins(toolbarTitle, 0, 0, margin, 0)
    }

    private fun setMargins(view: View, start: Int, top: Int, end: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(start, top, end, bottom)
            view.requestLayout()
        }
    }


    var toolbarTitle: String?
        get() {
            val toolbarTitle = findViewById<TextView>(R.id.tvToolbarTitle)
            return toolbarTitle.text.toString()
        }
        set(title) {
            val toolbarTitle = findViewById<TextView>(R.id.tvToolbarTitle)
            toolbarTitle.text = title
        }

    /**
     * Showing Activity in Immersive  Mode
     *
     * @param activity activity to show in immersive mode
     */
    fun fullScreen(activity: Activity) {
        activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    protected fun animateView(context: Context?, view: View, @AnimRes anim: Int): Animation {
        val animation = AnimationUtils
                .loadAnimation(context, anim)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })
        view.startAnimation(animation)
        /*AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                R.animator.flip_up);
        set.setTarget(view);
        set.start();
        view.setVisibility(View.VISIBLE);*/return animation
    }

    protected fun performBackPress() {
        if (doubleBackToExitPressedOnce) {
            finish()
        }
        doubleBackToExitPressedOnce = true
        ToastHelper.instance.show("Please click BACK again to exit")
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    /* public void logUserForFabric() {

        LoginModel loginData = AppDataManager.instance.getLoginData();

        if (loginData == null) {
            return;
        }

        Crashlytics.setUserIdentifier(loginData.getData().getMobile());
        Crashlytics.setUserName(loginData.getData().getName());
        Crashlytics.setUserEmail(loginData.getData().getEmail());
        */
    /*Crashlytics.setString(com.shopping.alis.utils.Constants.SCREEN_DENSITY,
                    DeviceInfoUtils.getDensity(AlisApplication.instance));*/
    /*

}*/

    open fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            true
        } else {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
            false
        }
    }


    fun disposeApiCall(disposable: Disposable?) {
        disposable?.dispose()
    }

    val isRTLConfig: Boolean
        get() {
            val config = resources.configuration
            return config.layoutDirection == View.LAYOUT_DIRECTION_RTL
        }

    fun finishActivity(activity: Activity) {
        activity.finish()
        animateBackNavigation()
    }

    fun finishMainActivity(activity: Activity) {
        activity.finish()
        //animateBackNavigation();
    }

    interface GetFileToUploadListener {
        fun onGotUploadFile(fileName: String?, type: String?)
    }

    private fun animateActivityNavigation() {
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun animateBackNavigation() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun animateActivitySlideInUp(activity: Activity) {
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    fun animateActivitySlideOutDown(activity: Activity) {
        activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        animateBackNavigation()
    }

    protected fun setSearchViewStyle(searchView: SearchView) {
        try {
            /* Code for changing the Text color and Hint color for the search view */
            val searchText: SearchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
            searchText.setHintTextColor(ContextCompat.getColor(this, R.color.colorHint))
            searchText.setTextColor(ContextCompat.getColor(this, R.color.colorText))
            /*....*/ //TextView searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            searchText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            val myCustomFont = ResourcesCompat.getFont(this, R.font.regular)
            searchText.typeface = myCustomFont
            searchText.inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            val mCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            mCursorDrawableRes.isAccessible = true
            mCursorDrawableRes[searchText] = 0 //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun enableButton(isEnable: Boolean, button: Button) {
        if (isEnable) {
            button.background = ContextCompat
                    .getDrawable(this, R.color.colorGrey)
            button.isEnabled = true
        } else {
            button.background = ContextCompat
                    .getDrawable(this, R.color.colorButton)
            button.isEnabled = false
        }
    }

    protected fun toggleFragment(activeFragment: Fragment?, fragment: Fragment): Fragment {
        supportFragmentManager.beginTransaction()
                .hide(activeFragment!!).show(fragment).commit()
        return fragment
    }

    protected fun hideAndReplaceFragment(fragment: Fragment, isHide: Boolean) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
                .add(
                    fragmentContainer, fragment, fragment
                        .javaClass.simpleName
                )
        if (isHide) {
            fragmentTransaction.hide(fragment).commit()
        } else {
            fragmentTransaction.commit()
        }
    }

    protected fun removeAndReplaceFragment(fragment: Fragment, isHide: Boolean, oldFrag: Fragment) {

        if (oldFrag != null) {
            supportFragmentManager.beginTransaction().remove(oldFrag).commit()
        }

        val fragmentTransaction = supportFragmentManager.beginTransaction()
                .add(
                    fragmentContainer, fragment, fragment
                        .javaClass.simpleName
                )
        if (isHide) {
            fragmentTransaction.hide(fragment).commit()
        } else {
            fragmentTransaction.commit()
        }
    }



    protected fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    protected fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    protected fun getToast(): ToastHelper {
        return ToastHelper.instance
    }

    protected fun getLang() : String? {
        return AppDataManager.instance.savedLanguage
    }



}