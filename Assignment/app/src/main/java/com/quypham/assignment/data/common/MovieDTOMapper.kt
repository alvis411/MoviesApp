package com.quypham.assignment.data.common

import com.quypham.assignment.api.response.GenresResponse
import com.quypham.assignment.api.response.MovieDetailApiResponse
import com.quypham.assignment.api.response.MovieResponse
import com.quypham.assignment.api.response.ResponseItems
import com.quypham.assignment.data.db.Genres
import com.quypham.assignment.data.db.Movie
import com.quypham.assignment.data.db.MovieDetail


fun ResponseItems<MovieResponse>.mapToEntity(): List<Movie> {
    return this.results.map {
        Movie(id = it.id,
            title = it.title,
            posterUrl = it.posterPath,
            overview = it.overview,
            releaseDate = it.releaseDate,
            page = this.page)
    }
}

fun MovieDetailApiResponse.mapToEntity(): MovieDetail {
    return MovieDetail(id = this.id,
                       originalLanguage = this.originalLanguage,
                       posterPath = this.posterPath,
                       originalTitle = this.originalTitle,
                       overview = this.overview,
                       releaseDate = this.releaseDate,
                       revenue = this.revenue,
                       runTime = this.runtime,
                       status = this.status,
                       voteAverage = this.voteAverage,
                       voteCount = this.voteCount,
                       genres = this.genres.mapToEntity())
}

fun List<GenresResponse>.mapToEntity(): List<Genres> {
    val result = arrayListOf<Genres>()
    this.forEach {
        result.add(Genres(id = it.id, name = it.name))
    }

    return result
}