@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")



import android.content.Context
import com.ecommerce.shopmitt.R
import java.sql.Timestamp
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * The class DateUtils
 */
class DateUtils private constructor() {

    fun getDateFromGivenTime(context: Context, inputDate: String): String {
        var date = "N/A"
        try {
            val dateFormat = SimpleDateFormat(GIVEN_FULL_DATE_FORMAT, Locale.ENGLISH)
            val dateObject = dateFormat.parse(inputDate)
            SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).also {
                date = it.format(dateObject)
            }
            if (android.text.format.DateUtils.isToday(getTimeStamp(inputDate))) {
                return context.resources.getString(R.string.today) + ", " + date
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    fun getDateFromYearlyFormat(context: Context, inputDate: String): String {
        var date = "N/A"
        try {
            val dateFormat = SimpleDateFormat(GIVEN_YEAR_FORMAT, Locale.ENGLISH)
            val dateObject = dateFormat.parse(inputDate)
            SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).also {
                date = it.format(dateObject)
            }

        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    fun getTimeStampFromDate(str_date: String?): Long {
        val dateFormat = SimpleDateFormat(GIVEN_YEAR_FORMAT,Locale.ENGLISH)
        val filterDateFromTs = Timestamp(dateFormat.parse(str_date).time)
        return filterDateFromTs.time
    }

    private fun getTimeStamp(str_date: String): Long {
        var timestamp: Long = 0
        val formatter: DateFormat = SimpleDateFormat(GIVEN_FULL_DATE_FORMAT, Locale.ENGLISH)
        val date: Date?
        try {
            date = formatter.parse(str_date)
            var output: Long = 0
            if (date != null) output = date.time / 1000L
            val str = output.toString()
            timestamp = str.toLong() * 1000
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timestamp
    }

    fun getFormattedDate(mills: Long): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = mills
        return android.text.format.DateFormat.format(GIVEN_FULL_DATE_FORMAT, cal).toString()
    }

    fun getYearlyFormattedDate(mills: Long): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = mills
        return android.text.format.DateFormat.format(GIVEN_YEAR_FORMAT, cal).toString()
    }

    fun getFormattedDateYearWise(year: Int, month: Int, day: Int): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.set(year, month, day)
        return android.text.format.DateFormat.format(GIVEN_YEAR_FORMAT, cal).toString()
    }

    fun addDays(days: Int, currentDate: Long): Long {

        val cal = Calendar.getInstance()
        cal.time = Date(currentDate)
        cal.add(Calendar.DATE, days) //minus number would decrement the days
        val date = cal.time
        return date.time
    }

    fun formatDate(context: Context, inputDate1: String?, inputTime1: String?): String? {


        val inputDate = "$inputDate1 $inputTime1"    //yyyy-MM-dd HH:mm:ss
        var date = "N/A"
        val outputFormat: String

        val recentDays = getRecentDays(inputDate, context)

        outputFormat = if (recentDays.isEmpty()) {
            OUT_PUT_DATE_FORMAT_2
        } else {
            OUT_PUT_DATE_FORMAT_1
        }

        try {
            val dateFormat = SimpleDateFormat(GIVEN_FULL_DATE_FORMAT, Locale.ENGLISH)
            val dateObject = dateFormat.parse(inputDate)
            val timeFormat = SimpleDateFormat(outputFormat, Locale.ENGLISH)
            date = timeFormat.format(dateObject)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date
    }

    fun formatDateWithYear(context: Context, inputDate1: String?, inputTime1: String?): String? {


        val inputDate = "$inputDate1 $inputTime1"    //yyyy-MM-dd HH:mm:ss
        var date = "N/A"


        val outputFormat: String = OUT_PUT_DATE_FORMAT_3

        try {
            val dateFormat = SimpleDateFormat(GIVEN_FULL_DATE_FORMAT, Locale.ENGLISH)
            val dateObject = dateFormat.parse(inputDate)
            val timeFormat = SimpleDateFormat(outputFormat, Locale.ENGLISH)
            date = timeFormat.format(dateObject)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date
    }

    fun formatDateT4(inputDate1: String?, inputTime1: String?): String? {


        val inputDate = "$inputDate1 $inputTime1"    //yyyy-MM-dd HH:mm:ss
        var date = "N/A"
        val outputFormat: String


        outputFormat = OUT_PUT_DATE_FORMAT_1

        try {
            val dateFormat = SimpleDateFormat(GIVEN_FULL_DATE_FORMAT_T4, Locale.ENGLISH)
            val dateObject = dateFormat.parse(inputDate)
            val timeFormat = SimpleDateFormat(outputFormat, Locale.ENGLISH)
            date = timeFormat.format(dateObject)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date
    }

    fun formatDateT4WithYear(inputDate1: String?, inputTime1: String?): String? {


        val inputDate = "$inputDate1 $inputTime1"    //yyyy-MM-dd HH:mm:ss
        var date = "N/A"
        val outputFormat: String


        outputFormat = OUT_PUT_DATE_FORMAT_3

        try {
            val dateFormat = SimpleDateFormat(GIVEN_FULL_DATE_FORMAT_T4, Locale.ENGLISH)
            val dateObject = dateFormat.parse(inputDate)
            val timeFormat = SimpleDateFormat(outputFormat, Locale.ENGLISH)
            date = timeFormat.format(dateObject)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date
    }

    private fun getRecentDays(inputDate: String, context: Context): String {

        val df: DateFormat = SimpleDateFormat(GIVEN_FULL_DATE_FORMAT, Locale.ENGLISH)
        val dateObject = df.parse(inputDate)

        when {
            android.text.format.DateUtils.isToday(getTimeStamp(inputDate)) -> {
                return context.getString(R.string.today)
            }
            isTomorrow(dateObject) -> {
                return context.getString(R.string.tomorrow)
            }
            isYesterday(dateObject) -> {
                return context.getString(R.string.yesterday)
            }
            else -> return ""
        }

    }


    private fun isYesterday(d: Date): Boolean {
        return android.text.format.DateUtils.isToday(d.time + android.text.format.DateUtils.DAY_IN_MILLIS)
    }

    private fun isTomorrow(d: Date): Boolean {
        return android.text.format.DateUtils.isToday(d.time - android.text.format.DateUtils.DAY_IN_MILLIS)
    }

    private object HOLDER {
        val INSTANCE = DateUtils()
    }

    companion object {
        private const val GIVEN_FULL_DATE_FORMAT_T4 = "yyyy-MM-dd HH:mm:ss" // 2018-05-21 18:15:54
        private const val GIVEN_FULL_DATE_FORMAT = "yyyy-MM-dd hh:mm a" // 2018-05-21 12:10 PM
        private const val GIVEN_YEAR_FORMAT = "yyyy-MM-dd"
        private const val DATE_FORMAT = "MMM d" // Feb 11

        private const val OUT_PUT_DATE_FORMAT_1 = "MMM d - hh:mm aaa" //10/10 PM
        private const val OUT_PUT_DATE_FORMAT_2 = "EEE, MMM d - hh:mm aaa" //10/10 PM
        private const val OUT_PUT_DATE_FORMAT_3 = "MMM d yyyy  hh:mm aaa" //10/10 PM


        val instance: DateUtils by lazy { HOLDER.INSTANCE }
    }

}