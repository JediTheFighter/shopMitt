package com.ecommerce.shopmitt.base.dialog

interface DialogCallback {

    /**
     * callback On button positive.
     *
     * @param dialogId the dialog id
     */
    abstract fun onButtonPositive(dialogId: Int)

    /**
     * callback On button negative.
     *
     * @param dialogId the dialog id
     */
    abstract fun onButtonNegative(dialogId: Int)

}