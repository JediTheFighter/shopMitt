package com.ecommerce.shopmitt.base.dialog


/**
 * The class Builder.
 */
class Builder {
    /**
     * The Dialog id.
     */
    var dialogId: Int = 0
    /**
     * The Title.
     */
    internal var title = "ALERT"
    /**
     * The Message.
     */
    internal var message = "Default message"
    /**
     * The Positive.
     */
    internal var positive = "YES"
    /**
     * The Negative.
     */
    internal var negative = "NO"
    /**
     * The Neutral.
     */
    internal var neutral = "CANCEL"
    /**
     * The Cancelable.
     */
    internal var cancelable = false
    /**
     * The Dg type.
     */
    internal var dgType = DialogType.DG_POS_NEG

    /**
     * Dialog id builder.
     *
     * @param dialogId the dialog id
     * @return the builder
     */
    fun dialogId(dialogId: Int): Builder {
        this.dialogId = dialogId
        return this
    }

    /**
     * Title builder.
     *
     * @param title the title
     * @return the builder
     */
    fun title(title: String): Builder {
        this.title = title
        return this
    }

    /**
     * Message builder.
     *
     * @param message the message
     * @return the builder
     */
    fun message(message: String): Builder {
        this.message = message
        return this
    }

    /**
     * Positive builder.
     *
     * @param positive the positive
     * @return the builder
     */
    fun positive(positive: String): Builder {
        this.positive = positive
        return this
    }

    /**
     * Negative builder.
     *
     * @param negative the negative
     * @return the builder
     */
    fun negative(negative: String): Builder {
        this.negative = negative
        return this
    }

    /**
     * Nutral builder.
     *
     * @param nutral the nutral
     * @return the builder
     */
    fun nutral(nutral: String): Builder {
        this.neutral = nutral
        return this
    }

    /**
     * Cancelable builder.
     *
     * @param cancelable the cancelable
     * @return the builder
     */
    fun cancelable(cancelable: Boolean?): Builder {
        this.cancelable = cancelable!!
        return this
    }

    /**
     * Dg type builder.
     *
     * @param dgType the dg type
     * @return the builder
     */
    fun dgType(dgType: DialogType): Builder {
        this.dgType = dgType
        return this
    }


    /**
     * Build dialog params.
     *
     * @return the dialog params
     */

    fun build(): DialogParams {
        return DialogParams(this)
    }


}