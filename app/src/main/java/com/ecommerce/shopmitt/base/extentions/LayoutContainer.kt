package com.ecommerce.shopmitt.base.extentions

import android.view.View

/**
 * A base interface for all view holders supporting Android Extensions-style view access.
 */
interface LayoutContainer {

    /** Returns the root holder view. */
    public val containerView: View?
}