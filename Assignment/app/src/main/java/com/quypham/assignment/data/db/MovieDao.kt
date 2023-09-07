package com.quypham.assignment.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<Movie>)

    @Query("DELETE FROM movie")
    suspend fun clearAllMovies()

    @Query("SELECT * FROM movie ORDER BY page")
    fun getMoviePagingSource(): PagingSource<Int, Movie>

    @Query("SELECT * FROM movie ORDER BY page")
    fun getAllMovie(): List<Movie>
}