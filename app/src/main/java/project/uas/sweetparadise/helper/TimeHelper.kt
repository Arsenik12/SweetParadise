package project.uas.sweetparadise.helper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeHelper {
    fun getCurrentTime() : String {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val time = Date()
        return timeFormat.format(time)
    }
}