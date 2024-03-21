package com.gokhanzopcuk.kotlininstagram.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gokhanzopcuk.kotlininstagram.databinding.RecylerRowBinding
import com.gokhanzopcuk.kotlininstagram.model.Post
import com.squareup.picasso.Picasso

class FeedRecylerAdapter(private val postList: ArrayList<Post> ): RecyclerView.Adapter<FeedRecylerAdapter.PostHolder>(){
    class PostHolder(val binding:RecylerRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
val binding=RecylerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)

    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
      holder.binding.recylerEmailText.text=postList.get(position).email
        holder.binding.recylerCommentText.text=postList.get(position).comment
        Picasso.get().load(postList.get(position).download).into(holder.binding.recylerImageView)

    }
}