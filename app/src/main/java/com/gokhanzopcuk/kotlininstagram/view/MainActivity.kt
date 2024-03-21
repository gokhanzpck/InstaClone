package com.gokhanzopcuk.kotlininstagram.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gokhanzopcuk.kotlininstagram.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        // Initialize Firebase Auth
        auth = Firebase.auth
        val currentUser=auth.currentUser
        if (currentUser!=null){
            val intent=Intent(this, FeedActivity::class.java)
            startActivity(intent)
            //eger kullanıcı önceden giriş yaptıysa direk şifre sormadan ana sayfaya yönlendirdik
        }
    }
     fun signIn( view: View){
         val email=binding.epostaText.text.toString()
         val password=binding.parolaText.text.toString()
         if (email.equals("")||password.equals("")){
             Toast.makeText(this@MainActivity,"Enter mail and paswordd",Toast.LENGTH_LONG).show()
         }else{
             auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                 val intent=Intent(this@MainActivity, FeedActivity::class.java)
                 startActivity(intent)
                 finish()
             }.addOnFailureListener {
                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
             }
         }
    }
     fun signup(view: View){
         val email=binding.epostaText.text.toString()
         val password=binding.parolaText.text.toString()
         if (email.equals("") && password.equals("")){
             Toast.makeText(this,"eposta veya şifre boş",Toast.LENGTH_LONG).show()
         }else{
             auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                 //başarılı olursa yani kullanıcı ve parola dogruysa(succes)
                 val intent=Intent(this@MainActivity, FeedActivity::class.java)
                 startActivity(intent)
                 finish()
             }.addOnFailureListener {
                 Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                 //hatalı oldugunda mesaj gönder dedik
             }

         }
    }
}