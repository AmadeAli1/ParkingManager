package repository

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import com.google.gson.JsonParser
import okhttp3.ResponseBody

fun ResponseBody.toMessage(): String {
    return try {
        JsonParser.parseString(string()).asJsonObject.get("message").asString
    } catch (e: Exception) {
        "Internal Server Error"
    }
}

fun <T> MutableState<T>.asState(): State<T> {
    return this
}