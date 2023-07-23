package com.dicoding.githubuserapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.githubuserapplication.dataclasses.Users
import com.dicoding.githubuserapplication.databinding.ItemUserBinding
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter (private val listUser: ArrayList<Users>) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){

    var onItemClick: ((Users) -> Unit)? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =
        ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val users = listUser[position]
        viewHolder.bind(users)
        Glide.with(viewHolder.itemView.context)
            .load(users.avatarUrl)
            .into(viewHolder.userImg)
        viewHolder.itemView.setOnClickListener{
            onItemClick?.invoke(users)
        }
    }

    override fun getItemCount() = listUser.size

    class ViewHolder(private val viewBinding: ItemUserBinding) : RecyclerView.ViewHolder(viewBinding.root){
        val userImg: CircleImageView = viewBinding.userPhoto
        fun bind(users: Users){
            viewBinding.tvUserName.text = users.login
        }
    }
}