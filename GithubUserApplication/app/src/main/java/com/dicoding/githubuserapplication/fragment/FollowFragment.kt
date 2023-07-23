package com.dicoding.githubuserapplication.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuserapplication.adapter.UserAdapter
import com.dicoding.githubuserapplication.databinding.FragmentFollowBinding
import com.dicoding.githubuserapplication.dataclasses.ItemsItem
import com.dicoding.githubuserapplication.dataclasses.Users
import com.dicoding.githubuserapplication.viewmodel.MainViewModel
import com.dicoding.githubuserapplication.viewmodel.SettingPreferences
import com.dicoding.githubuserapplication.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class FollowFragment : Fragment() {

    private lateinit var binding: FragmentFollowBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowBinding.inflate(layoutInflater, container,false)

        return binding.root
    }

    private fun setUserData(users: List<ItemsItem>){
        val listUser = ArrayList<Users>()
        for(user in users){
            listUser.add(
                Users(user.id, user.avatarUrl, user.htmlUrl,
                user.followingUrl, user.login, user.followersUrl, user.url)
            )
        }
        val adapter = UserAdapter(listUser)
        binding.rvUserFollow.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val position = arguments?.getInt(ARG_POSITION)
        val username = arguments?.getString(ARG_USERNAME)

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvUserFollow.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(activity, layoutManager.orientation)
        binding.rvUserFollow.addItemDecoration(itemDecoration)

        val pref = SettingPreferences.getInstance(requireActivity().dataStore)
        val mainViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application, pref))[MainViewModel::class.java]
        mainViewModel.user.observe(viewLifecycleOwner) { user ->
            user.getContentIfNotHandled()?.let {
                setUserData(it)
            }
        }

        mainViewModel.isLoadingFollow.observe(viewLifecycleOwner){
            showLoadingFollow(it)
        }

        if (username != null){
            if(position == 1)
                mainViewModel.findUserFollowers(username)
            else
                mainViewModel.findUserFollowing(username)
        }
    }

    // Indikator Loading Follower/Following user
    private fun showLoadingFollow(isLoading: Boolean){
        if(isLoading){
            binding.progressBarFollow.visibility = View.VISIBLE
        } else {
            binding.progressBarFollow.visibility = View.GONE
        }
    }

    companion object {
        const val ARG_POSITION = "position"
        const val ARG_USERNAME = "username"
    }
}