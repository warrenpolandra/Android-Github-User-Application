package com.dicoding.githubuserapplication.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteUserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favoriteUserEntity: FavoriteUserEntity)

    @Delete
    fun delete(favoriteUserEntity: FavoriteUserEntity)

    @Query("SELECT * from favoriteUser")
    fun getAllFavUsers(): LiveData<List<FavoriteUserEntity>>

    @Query("SELECT * FROM favoriteUser WHERE username = :username")
    fun getFavoriteUserByUsername(username: String): LiveData<List<FavoriteUserEntity>>
}