package com.dicoding.githubuserapplication.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuserapplication.*
import com.dicoding.githubuserapplication.adapter.UserAdapter
import com.dicoding.githubuserapplication.databinding.ActivityMainBinding
import com.dicoding.githubuserapplication.dataclasses.ItemsItem
import com.dicoding.githubuserapplication.dataclasses.Users
import com.dicoding.githubuserapplication.viewmodel.MainViewModel
import com.dicoding.githubuserapplication.viewmodel.SettingPreferences
import com.dicoding.githubuserapplication.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = SettingPreferences.getInstance(dataStore)

        // MainViewModel
        mainViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application, pref))[MainViewModel::class.java]
        mainViewModel.user.observe(this) { user ->
            user.getContentIfNotHandled()?.let {
                setUserData(it)
            }
        }

        // Theme Settings
        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // RecycleView
        val layoutManager = LinearLayoutManager(this)
        binding.rvUser.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvUser.addItemDecoration(itemDecoration)

        // FindUser
        mainViewModel.isLoading.observe(this) {
            showLoading((it))
        }
        mainViewModel.findUser("ahmad")
    }

    // Opsi Pencarian
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        val mainViewModel = ViewModelProvider(this@MainActivity, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]
        mainViewModel.user.observe(this@MainActivity) { user ->
            user.getContentIfNotHandled()?.let {
                setUserData(it)
            }
        }
        mainViewModel.isLoading.observe(this@MainActivity) {
            showLoading((it))
        }

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as androidx.appcompat.widget.SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String): Boolean {
                mainViewModel.findUser(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.favorite -> {
                val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
                startActivity(intent)
            }
            R.id.settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Menampilkan data user ke RecycleView
    private fun setUserData(users: List<ItemsItem>){
        val listUser = ArrayList<Users>()
        for(user in users){
            listUser.add(
                Users(user.id, user.avatarUrl, user.htmlUrl,
                user.followingUrl, user.login, user.followersUrl, user.url)
            )
        }
        val adapter = UserAdapter(listUser)
        binding.rvUser.adapter = adapter
        adapter.onItemClick = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("user", it)
            startActivity(intent)
        }
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