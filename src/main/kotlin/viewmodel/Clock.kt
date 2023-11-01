package viewmodel

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

data class Clock(
    var day: String = "",
    var time: String = "",
)
