package com.cnam.magasinenligne.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import com.cnam.magasinenligne.MyApplication
import com.cnam.magasinenligne.models.DeviceTimezone
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

fun Any.logDebug(message: String) {
    val tag = this.javaClass.simpleName
    Log.d(tag, message)
}

fun Any.logError(message: String) {
    val tag = this.javaClass.simpleName
    Log.e(tag, message)
}

fun Any.logWarning(message: String) {
    val tag = this.javaClass.simpleName
    Log.w(tag, message)
}

fun Any.logVerbose(message: String) {
    val tag = this.javaClass.simpleName
    Log.v(tag, message)
}

fun Any.logInfo(message: String) {
    val tag = this.javaClass.simpleName
    Log.i(tag, message)
}

fun Any.getTag(): String {
    return this.javaClass.simpleName
}

fun <T> putPreference(name: String, value: T) = with(MyApplication.editor) {

    when (value) {

        is Long -> putLong(name, value)

        is String -> putString(name, value)

        is Int -> putInt(name, value)

        is Boolean -> putBoolean(name, value)

        is Float -> putFloat(name, value)

        else -> throw IllegalArgumentException("This type can't be saved into Preferences")

    }.commit()

}

fun putPreferenceSet(name: String, values: Set<String>) = with(MyApplication.editor) {
    putStringSet(name, values).commit()
}

fun findPreference(name: String, default: MutableSet<String> = mutableSetOf()): Set<String> =
    with(MyApplication.shared) {
        return getStringSet(name, default) as MutableSet
    }

@Suppress("UNCHECKED_CAST")
fun <T> findPreference(name: String, default: T): T = with(MyApplication.shared) {

    val res: Any = when (default) {

        is Long -> getLong(name, default)

        is String -> getString(name, default)

        is Int -> getInt(name, default)

        is Boolean -> getBoolean(name, default)

        is Float -> getFloat(name, default)

        else -> throw IllegalArgumentException("This type can be saved into Preferences")

    }!!



    res as T

}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPhone(phoneNumber: String): Boolean {
    val phoneUtil = PhoneNumberUtil.getInstance()

    try {
        val numberProto = phoneUtil.parse(phoneNumber, null)
        return phoneUtil.isValidNumber(numberProto)
    } catch (e: NumberParseException) {
        Log.e("Exception", "NumberParseException was thrown: $e")
    }

    return false

}

fun validatePassword(password: String): Int {
    val upperCasePattern = Pattern.compile("[A-Z ]") // in case of error flag -2
    val lowerCasePattern = Pattern.compile("[a-z ]") // in case of error flag -3
    val digitCasePattern = Pattern.compile("[0-9 ]") // in case of error flag -4
    val specialCharPattern =
        Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE) // in case of error flag -5


    if (password.length < 8) { // length error flag -1
        return -1
    }
    if (!upperCasePattern.matcher(password).find()) {
        return -2
    }
    if (!lowerCasePattern.matcher(password).find()) {
        return -3
    }
    if (!digitCasePattern.matcher(password).find()) {
        return -4
    }
    if (!specialCharPattern.matcher(password).find()) {
        return -5
    }
    return 0 // true flag
}

fun getCurrentTimeUsingDate(): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss a", Locale.ROOT).format(Date())

fun getTimeFromLong(milliseconds: Long): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss a", Locale.ROOT).format(Date(milliseconds))

fun getNowTimeStamp(): String = System.currentTimeMillis().toString()

fun getCountryIsoCode(context: Context): String {
    var countryCode: String?

    // try to get country code from TelephonyManager service
    val tm: TelephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    // query first getSimCountryIso()
    countryCode = tm.simCountryIso
    if (countryCode != null && countryCode.length == 2) return countryCode //.toLowerCase(Locale.ROOT)

    countryCode = tm.networkCountryIso

    if (countryCode != null && countryCode.length == 2) return countryCode


    // if network country not available (tablets maybe), get country code from Locale class
    countryCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales[0].country
    } else {
        context.resources.configuration.locale.country
    }
    return if (countryCode != null && countryCode.length == 2) countryCode else ""
}

fun getCountryCallingCode(countryName: String): Int =
    PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryName)

fun getTimezone(): DeviceTimezone {
    return try {
        val tz = TimeZone.getDefault()
        val timezone = tz.getDisplayName(true, TimeZone.SHORT).replace("GMT", "")
        val timezoneRegion = tz.id
        DeviceTimezone(timezoneRegion, timezone)
    } catch (error: AssertionError) {
        DeviceTimezone("", "")
    } catch (e: Exception) {
        DeviceTimezone("", "")
    }
}


fun setImageFile(imageUri: Uri, name: String = "profile_image_file"): MultipartBody.Part? {
    var juri: URI? = null
    try {
        juri = URI(imageUri.toString())
    } catch (e: java.lang.Exception) {
        Log.d("Error", "${e.message}")
    }
    if (juri == null) return null
    Log.d("JURI", "$juri")
    val file = File(juri)
    Log.d("file", file.toString())
    val requestFile: RequestBody =
        RequestBody.create(MediaType.parse("multipart/form-data"), file)
    Log.d("Request file", requestFile.toString())
    val data = MultipartBody.Part.createFormData(name, file.name, requestFile)
    Log.d("Data", data.toString())
    return data
}

fun createTextRequestBody(text: String): RequestBody {
    return RequestBody.create(MediaType.parse("text/plain"), text)
}

