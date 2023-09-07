package com.quypham.assignment.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(
    tableName = "movie"
)
data class Movie(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val title: String,
    val posterUrl: String?,
    val releaseDate: String,
    val overview: String,
    val page:Int
): Parcelable