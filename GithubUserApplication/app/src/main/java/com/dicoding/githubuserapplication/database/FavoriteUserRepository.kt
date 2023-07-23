package com.dicoding.githubuserapplication.database

import android.app.Application
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteUserRepository(application: Application) {
    private val mFavoriteUserDao: FavoriteUserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavoriteUserDatabase.getDatabase(application)
        mFavoriteUserDao = db.favoriteUserDao()
    }

    fun getAllFavUsers(): LiveData<List<FavoriteUserEntity>> = mFavoriteUserDao.getAllFavUsers()

    fun insert(favoriteUser: FavoriteUserEntity) {
        executorService.execute { mFavoriteUserDao.insert(favoriteUser) }
    }

    fun delete(favoriteUser: FavoriteUserEntity) {
        executorService.execute { mFavoriteUserDao.delete(favoriteUser) }
    }

    fun getFavUserByUsername(username: String) : LiveData<List<FavoriteUserEntity>> = mFavoriteUserDao.getFavoriteUserByUsername(username)
}