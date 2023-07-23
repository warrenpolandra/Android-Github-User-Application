package com.dicoding.githubuserapplication.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dicoding.githubuserapplication.R
import com.dicoding.githubuserapplication.adapter.SectionsPagerAdapter
import com.dicoding.githubuserapplication.database.FavoriteUserEntity
import com.dicoding.githubuserapplication.databinding.ActivityDetailBinding
import com.dicoding.githubuserapplication.dataclasses.DetailUserResponse
import com.dicoding.githubuserapplication.dataclasses.Users
import com.dicoding.githubuserapplication.viewmodel.FavoriteUserViewModel
import com.dicoding.githubuserapplication.viewmodel.MainViewModel
import com.dicoding.githubuserapplication.viewmodel.SettingPreferences
import com.dicoding.githubuserapplication.viewmodel.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "settings")

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var favoriteUserViewModel: FavoriteUserViewModel
    private var isFavorite = false

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail User Github"

        val pref = SettingPreferences.getInstance(dataStore)

        // MainViewModel
        val mainViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application, pref))[MainViewModel::class.java]
        mainViewModel.userDetail.observe(this){ userDetail ->
            userDetail.getContentIfNotHandled()?.let {
                setUserDetail(it)
            }
        }

        mainViewModel.isLoading.observe(this){
            showLoading(it)
        }

        val user = intent.getParcelableExtra<Users>("user")
        if(user != null)
            mainViewModel.findUserDetail(user.login)

        // Tab Navigation
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        if (user != null)
            sectionsPagerAdapter.username = user.login
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs

        TabLayoutMediator(tabs, viewPager){ tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        // FavUserViewModel
        favoriteUserViewModel = obtainViewModel(this@DetailActivity)
        if (user != null) {
            favoriteUserViewModel.isFavoriteByUsername(user.login).observe(this) {
                if (it.isEmpty()) {
                    isFavorite = false
                    setButtonTitle(isFavorite)
                } else {
                    isFavorite = true
                    setButtonTitle(isFavorite)
                }
            }
        }
        favoriteUserViewModel.isFavorite.observe(this) {
            setButtonTitle(it)
        }
        // Favorite Button
        binding.btnFavorite.setOnClickListener {
            if (user != null) {
                val favUser = FavoriteUserEntity(user.login, user.avatarUrl)
                if(!isFavorite) {
                    favoriteUserViewModel.insert(favUser)
                    isFavorite = true
                    setButtonTitle(isFavorite)
                } else {
                    favoriteUserViewModel.delete(favUser)
                    isFavorite = false
                    setButtonTitle(isFavorite)
                }
            }
        }

        // Share Button
        binding.btnShare.setOnClickListener {
            if (user != null) {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, "Check out this github user:\nhttps://github.com/" + user.login)
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "Share to:"))
            }
        }

        supportActionBar?.elevation = 0f
    }

    // Set Favorite Button Title
    private fun setButtonTitle(isFavorite: Boolean) {
        val favButton = binding.btnFavorite
        if (isFavorite) {
            favButton.text = getString(R.string.added_to_fav)
        } else {
            favButton.text = getString(R.string.add_to_fav)
        }
    }

    // Get ViewModel
    private fun obtainViewModel(activity: AppCompatActivity): FavoriteUserViewModel {
        val pref = SettingPreferences.getInstance(dataStore)
        val factory = ViewModelFactory.getInstance(activity.application, pref)
        return ViewModelProvider(activity, factory)[FavoriteUserViewModel::class.java]
    }

    // Set User Detail
    private fun setUserDetail(userDetail: DetailUserResponse){
        Glide.with(this)
            .load(userDetail.avatarUrl)
            .into(binding.imgDetail)
        binding.tvUsernameDetail.text = userDetail.login
        binding.tvNameDetail.text = userDetail.name
        binding.tvFollower.text = getString(R.string.follower_num, userDetail.followers)
        binding.tvFollowing.text = getString(R.string.following_num, userDetail.following)
    }

    // Indikator Loading Detail/List user
    private fun showLoading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}