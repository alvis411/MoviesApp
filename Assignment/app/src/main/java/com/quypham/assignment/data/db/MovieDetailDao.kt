package com.quypham.assignment.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDetailDao {
    @Query("SELECT * FROM detail WHERE id=:id")
    suspend fun getMovieDetailDao(id: Int): MovieDetail?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetail(movieDetail: MovieDetail)
}