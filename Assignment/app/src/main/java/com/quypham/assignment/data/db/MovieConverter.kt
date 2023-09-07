package com.quypham.assignment.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MovieConverter {
    @TypeConverter
    fun toGenreList(list: String): List<Genres> {
        return Gson().fromJson(list, object : TypeToken<List<Genres>>() {}.type)
    }

    @TypeConverter
    fun fromGenreList(list: List<Genres>): String{
        return Gson().toJson(list, object : TypeToken<List<Genres>>() {}.type)
    }
}