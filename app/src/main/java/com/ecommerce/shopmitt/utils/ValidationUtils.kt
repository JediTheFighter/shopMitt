

/**
 * Created by Sreejith Kp on 02/12/2020.
 * Aufait
 * sreejith.kp@mindster.in
 */
class ValidationUtils private constructor() {

    fun isEmpty(text: String?): Boolean {
        return text.isNullOrEmpty()
    }

    fun isValidMail(mail: String?): Boolean {

        if(mail.isNullOrEmpty())
            return false
        else
            return android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()
    }

    private object HOLDER {
        val INSTANCE = ValidationUtils()
    }

    companion object {
        val instance: ValidationUtils by lazy { HOLDER.INSTANCE }
    }

}