package com.quypham.assignment.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieDetailApiResponse(
    @Json(name="adult") val adult: Boolean,
    @Json(name="backdrop_path") val backdropPath: String?,
    @Json(name="belongs_to_collection") val belongsToCollection: BelongsToCollectionsResponse?,
    @Json(name="budget") val budget: Int,
    @Json(name="genres") val genres: List<GenresResponse> = arrayListOf(),
    @Json(name="homepage") val homepage: String,
    @Json(name="id") val id: Int,
    @Json(name="imdb_id") val imdbId: String?,
    @Json(name="original_language") val originalLanguage: String,
    @Json(name="original_title") val originalTitle: String,
    @Json(name="overview") val overview: String,
    @Json(name="popularity") val popularity: Double,
    @Json(name="poster_path") val posterPath: String?,
    @Json(name="production_companies") val productionCompanies: List<ProductionCompaniesResponse> = arrayListOf(),
    @Json(name="production_countries") val productionCountries: List<ProductionCountriesResponse> = arrayListOf(),
    @Json(name="release_date") val releaseDate: String,
    @Json(name="revenue") val revenue: Long,
    @Json(name="runtime") val runtime: Int,
    @Json(name="spoken_languages") val spokenLanguages: List<SpokenLanguagesResponse> = arrayListOf(),
    @Json(name="status") val status: String,
    @Json(name="tagline") val tagline: String,
    @Json(name="title") val title: String,
    @Json(name="video") val video: Boolean,
    @Json(name="vote_average") val voteAverage: Double,
    @Json(name="vote_count") val voteCount: Int
)


data class GenresResponse(
    @Json(name="id") val id: Int,
    @Json(name="name") val name: String
)


data class BelongsToCollectionsResponse(
    @Json(name="id") val id: Int,
    @Json(name="name") val name: String,
    @Json(name="poster_path") val posterPath: String?,
    @Json(name="backdrop_path") val backdropPath: String?,
)

data class ProductionCompaniesResponse(
    @Json(name="id") val id: Int,
    @Json(name="logo_path") val logoPath: String?,
    @Json(name="name") val name: String,
    @Json(name="origin_country") val originCountry: String
)

data class ProductionCountriesResponse(
    @Json(name="iso_3166_1") val iso31661: String,
    @Json(name="name") val name: String
)


data class SpokenLanguagesResponse(
    @Json(name="english_name") val englishName: String,
    @Json(name="iso_639_1") val iso6391: String,
    @Json(name="name") val name: String
)