package com.quypham.assignment.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Movie::class, MovieDetail::class, RemoteKeys::class], version = 1, exportSchema = false)
@TypeConverters(MovieConverter::class)
abstract class MovieDatabase: RoomDatabase() {
    abstract fun getMovieDao(): MovieDao
    abstract fun getMovieDetailDao(): MovieDetailDao
    abstract fun getRemoteKeysDao(): RemoteKeysDao
}