package com.quypham.assignment.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "detail"
)
data class MovieDetail(
    @PrimaryKey val id: Int,
    val originalLanguage: String,
    val posterPath: String?,
    val originalTitle: String,
    val overview: String,
    val releaseDate: String,
    val revenue: Long,
    val runTime: Int,
    val status: String,
    val voteAverage: Double,
    val voteCount: Int,
    val genres: List<Genres>
)

data class Genres (val id: Int,
                   val name: String)