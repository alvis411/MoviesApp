package com.quypham.assignment.di

import com.quypham.assignment.data.repositories.MovieRepository
import com.quypham.assignment.data.repositories.movie.MovieLocalDataSource
import com.quypham.assignment.data.repositories.movie.MovieLocalDataSourceImp
import com.quypham.assignment.data.repositories.movie.MovieRemoteDataSource
import com.quypham.assignment.data.repositories.movie.MovieRemoteDataSourceImp
import com.quypham.assignment.data.repositories.movie.MovieRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
interface RepositoriesModule {
    @Binds
    fun provideMovieRepository(repository: MovieRepositoryImp): MovieRepository

    @Binds
    fun provideMovieLocalDataSource(local: MovieLocalDataSourceImp): MovieLocalDataSource

    @Binds
    fun provideMovieRemoteDatSource(remote: MovieRemoteDataSourceImp): MovieRemoteDataSource
}