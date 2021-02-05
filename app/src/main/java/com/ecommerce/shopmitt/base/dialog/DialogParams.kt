package com.ecommerce.shopmitt.base.dialog


class DialogParams(builder: Builder) {

    /**
     * The Dialog id.
     */
    internal var dialogId: Int = 0
    /**
     * The Title.
     */
    internal var title = "ALERT"
    /**
     * The Message.
     */
    internal var message = "No description available"
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
    private var neutral = "CANCEL"
    /**
     * The Cancelable.
     */
    private var cancelable = false
    /**
     * The Dg type.
     */
    internal var dgType = DialogType.DG_POS_NEG

    /**
     * Gets dialog id.
     *
     * @return the dialog id
     */
    fun getDialogId(): Int {
        return dialogId
    }

    /**
     * Sets dialog id.
     *
     * @param dialogId the dialog id
     */
    fun setDialogId(dialogId: Int) {
        this.dialogId = dialogId
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    fun getTitle(): String {
        return title
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    fun setTitle(title: String) {
        this.title = title
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    fun getMessage(): String {
        return message
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    fun setMessage(message: String) {
        this.message = message
    }

    /**
     * Gets positive.
     *
     * @return the positive
     */
    fun getPositive(): String {
        return positive
    }

    /**
     * Sets positive.
     *
     * @param positive the positive
     */
    fun setPositive(positive: String) {
        this.positive = positive
    }

    /**
     * Gets negative.
     *
     * @return the negative
     */
    fun getNegative(): String {
        return negative
    }

    /**
     * Sets negative.
     *
     * @param negative the negative
     */
    fun setNegative(negative: String) {
        this.negative = negative
    }

    /**
     * Gets neutral.
     *
     * @return the neutral
     */
    fun getNeutral(): String {
        return neutral
    }

    /**
     * Sets neutral.
     *
     * @param neutral the neutral
     */
    fun setNeutral(neutral: String) {
        this.neutral = neutral
    }

    /**
     * Is cancelable boolean.
     *
     * @return the boolean
     */
    fun isCancelable(): Boolean {
        return cancelable
    }

    /**
     * Sets cancelable.
     *
     * @param cancelable the cancelable
     */
    fun setCancelable(cancelable: Boolean) {
        this.cancelable = cancelable
    }

    /**
     * Gets dg type.
     *
     * @return the dg type
     */
    fun getDgType(): DialogType {
        return dgType
    }

    /**
     * Sets dg type.
     *
     * @param dgType the dg type
     */
    fun setDgType(dgType: DialogType) {
        this.dgType = dgType
    }


    /**
     * Instantiates a new Dialog params.
     */
    init {
        this.dialogId = builder.dialogId
        this.title = builder.title
        this.message = builder.message
        this.positive = builder.positive
        this.negative = builder.negative
        this.neutral = builder.neutral
        this.cancelable = builder.cancelable
        this.dgType = builder.dgType
    }


}