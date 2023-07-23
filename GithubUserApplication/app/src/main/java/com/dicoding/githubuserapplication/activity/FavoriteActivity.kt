package com.dicoding.githubuserapplication.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuserapplication.adapter.UserAdapter
import com.dicoding.githubuserapplication.database.FavoriteUserEntity
import com.dicoding.githubuserapplication.databinding.ActivityFavoriteBinding
import com.dicoding.githubuserapplication.dataclasses.Users
import com.dicoding.githubuserapplication.viewmodel.FavoriteUserViewModel
import com.dicoding.githubuserapplication.viewmodel.MainViewModel
import com.dicoding.githubuserapplication.viewmodel.SettingPreferences
import com.dicoding.githubuserapplication.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var favoriteUserViewModel: FavoriteUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = SettingPreferences.getInstance(dataStore)
        val mainViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application, pref))[MainViewModel::class.java]

        val layoutManager = LinearLayoutManager(this)
        binding.rvUserFavorite.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvUserFavorite.addItemDecoration(itemDecoration)

        mainViewModel.isLoading.observe(this) {
            showLoading((it))
        }

        favoriteUserViewModel = obtainViewModel(this@FavoriteActivity)
        favoriteUserViewModel.getAllFavUsers().observe(this) { users: List<FavoriteUserEntity> ->
            val items = arrayListOf<Users>()
            users.map {
                val item = Users(login = it.username, avatarUrl = it.avatarUrl!!)
                items.add(item)
            }
            val adapter = UserAdapter(items)
            binding.rvUserFavorite.adapter = adapter
            adapter.onItemClick = {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("user", it)
                startActivity(intent)
            }
        }
        supportActionBar?.title = "Favorite Users"

    }

    private fun obtainViewModel(activity: AppCompatActivity): FavoriteUserViewModel {
        val pref = SettingPreferences.getInstance(dataStore)
        val factory = ViewModelFactory.getInstance(activity.application, pref)
        return ViewModelProvider(activity, factory)[FavoriteUserViewModel::class.java]
    }

    // Menunjukkan Loading
    private fun showLoading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}