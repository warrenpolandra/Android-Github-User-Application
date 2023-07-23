package com.dicoding.githubuserapplication.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubuserapplication.database.FavoriteUserEntity
import com.dicoding.githubuserapplication.database.FavoriteUserRepository

class FavoriteUserViewModel(application: Application) : ViewModel() {

    private val mFavoriteUserRepository: FavoriteUserRepository = FavoriteUserRepository(application)

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun getAllFavUsers() = mFavoriteUserRepository.getAllFavUsers()

    fun insert(favoriteUser: FavoriteUserEntity) {
        mFavoriteUserRepository.insert(favoriteUser)
    }

    fun delete(favoriteUser: FavoriteUserEntity) {
        mFavoriteUserRepository.delete(favoriteUser)
    }

    fun isFavoriteByUsername(username: String): LiveData<List<FavoriteUserEntity>> = mFavoriteUserRepository.getFavUserByUsername(username)
}