package com.dicoding.githubuserapplication.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.githubuserapplication.api.ApiConfig
import com.dicoding.githubuserapplication.dataclasses.DetailUserResponse
import com.dicoding.githubuserapplication.dataclasses.ItemsItem
import com.dicoding.githubuserapplication.dataclasses.UserResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: SettingPreferences) : ViewModel(){
    private val _user = MutableLiveData<Event<List<ItemsItem>?>>()
    val user: LiveData<Event<List<ItemsItem>?>> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingFollow = MutableLiveData<Boolean>()
    val isLoadingFollow: LiveData<Boolean> = _isLoadingFollow

    private val _userDetail = MutableLiveData<Event<DetailUserResponse?>>()
    val userDetail: LiveData<Event<DetailUserResponse?>> = _userDetail

    companion object{
        private const val TAG = "MainViewModel"
    }

    // Mencari Detail user
    fun findUserDetail(name: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUserDetail(name)
        client.enqueue(object : Callback<DetailUserResponse>{
            override fun onResponse(
                call: Call<DetailUserResponse>,
                response: Response<DetailUserResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _userDetail.value = Event(response.body())
                } else{
                    onFailureLog(response.message())
                }
            }

            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                _isLoading.value = false
                onFailureLog(t.message)
            }
        })
    }

    // Mencari List user sesuai Query
    fun findUser(name: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUser(name)
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _user.value = Event(response.body()?.items)
                } else {
                    onFailureLog(response.message())
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _isLoading.value = false
                onFailureLog(t.message)
            }
        })
    }

    // Mencari Follower dari user tertentu
    fun findUserFollowers(name: String){
        _isLoadingFollow.value = true
        val client = ApiConfig.getApiService().getFollowers(name)
        client.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(call: Call<List<ItemsItem>>, response: Response<List<ItemsItem>>) {
                _isLoadingFollow.value = false
                if (response.isSuccessful){
                    _user.value = Event(response.body())
                } else {
                    onFailureLog(response.message())
                }
            }
            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                _isLoadingFollow.value = false
                onFailureLog(t.message)
            }
        })
    }

    // Mencari following dari user tertentu
    fun findUserFollowing(name: String){
        _isLoadingFollow.value = true
        val client = ApiConfig.getApiService().getFollowing(name)
        client.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(call: Call<List<ItemsItem>>, response: Response<List<ItemsItem>>) {
                _isLoadingFollow.value = false
                if (response.isSuccessful){
                    _user.value = Event(response.body())
                } else {
                    onFailureLog(response.message())
                }
            }
            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                onFailureLog(t.message)
            }
        })
    }

    // Log jika gagal
    fun onFailureLog(message: String?){
        _isLoadingFollow.value = false
        Log.e(TAG, "onFailure: $message")
    }

    // Theme settings
    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSettings(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }
}