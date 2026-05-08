package top.pythagodzilla.courser.data.dataBase

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

class Convertor {
    @TypeConverter
    fun fromLocalDateTime(time: LocalDateTime?): Long? {
        return time?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun fromLongToLocalDateTime(time: Long?): LocalDateTime? {
        return time?.let {
            LocalDateTime.ofEpochSecond(time, 0, java.time.ZoneOffset.UTC)
        }
    }

//    @TypeConverter

}