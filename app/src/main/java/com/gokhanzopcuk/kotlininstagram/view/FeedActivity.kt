package com.gokhanzopcuk.kotlininstagram.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gokhanzopcuk.kotlininstagram.R
import com.gokhanzopcuk.kotlininstagram.adapter.FeedRecylerAdapter
import com.gokhanzopcuk.kotlininstagram.databinding.ActivityFeedBinding
import com.gokhanzopcuk.kotlininstagram.model.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import com.google.firebase.firestore.firestore

class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var postArrayList: ArrayList<Post>
    private lateinit var feedAdapte:FeedRecylerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFeedBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        auth=Firebase.auth
        db=Firebase.firestore
        getData()
        postArrayList=ArrayList<Post>()
        binding.recylerView.layoutManager=LinearLayoutManager(this)
        feedAdapte= FeedRecylerAdapter(postArrayList)
        binding.recylerView.adapter=feedAdapte

    }
    private fun getData(){
        db.collection("Posts").orderBy("date", Direction.DESCENDING).addSnapshotListener { value, error ->
//order bay da tarihe göre sırala dedik ve descending de en son paylaşılan en üstte dedik
            if (error != null){
                Toast.makeText(this,localClassName,Toast.LENGTH_LONG).show()
            }else{
                if (value!=null){
                    if (!value.isEmpty){
                        //ici boş degilse
                        val documents=value.documents
                        postArrayList.clear()
                        for (document in documents){
                            val comment=document.get("comment")as String
                            val userEmail=document.get("userEmail") as String
                            val  downloadnUrl=document.get("downloadUrl")as String
                            val post=Post(userEmail,comment,downloadnUrl)
                           postArrayList.add(post)
                        }
                        feedAdapte.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val  menuInflater=menuInflater
        menuInflater.inflate(R.menu.insta_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId== R.id.add_post){
val intent=Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
        else if (item.itemId== R.id.signout){
            auth.signOut()
            val intent=Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}