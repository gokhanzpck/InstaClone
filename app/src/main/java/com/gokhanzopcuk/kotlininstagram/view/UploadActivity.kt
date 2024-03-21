package com.gokhanzopcuk.kotlininstagram.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gokhanzopcuk.kotlininstagram.databinding.ActivityUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.util.UUID

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
     var selectedPicture : Uri?=null
    private lateinit var auth:FirebaseAuth
    private lateinit var firestore:FirebaseFirestore
    private lateinit var storage:FirebaseStorage



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding=ActivityUploadBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        registerLauncher()
        auth=Firebase.auth
        firestore=Firebase.firestore
        storage=Firebase.storage


    }
    fun uploud(view: View){
        val uuid =UUID.randomUUID()
        val imageName="$uuid.jpg"
        //resimlerin her yaptıgımızda farklı bir id veriyor gibi düşünülebilir resimlerin karışmaması icin
        val reference=storage.reference
        //referance verdik şuruya kaydet diye
        val imageReference=reference.child("images").child(imageName)
        //image dosyası ac icine image.jpg diye dosya koy
        if (selectedPicture!=null){
           imageReference.putFile(selectedPicture!!).addOnSuccessListener {
            val uploudPictureReference=storage.reference.child("images").child(imageName)
               //imagename e ulaş dedik
               uploudPictureReference.downloadUrl.addOnSuccessListener {
                   val downloudUrl=it.toString()
                   //resmi string hale cevirdik
                 val postMap= hashMapOf<String,Any>()
                   //ANY HERHANGİ BİR ŞEY OLABİLİR DDİK
                   postMap.put("downloadUrl",downloudUrl)
                   postMap.put("userEmail",auth.currentUser!!.email!!)
                   postMap.put("comment",binding.commentText.text.toString())
                   postMap.put("date",Timestamp.now())
                   firestore.collection("Posts").add(postMap).addOnSuccessListener {
                       //COLLECTİON ADINI POSTS YAPTIK VERİLERİ EKLEDİK POSTMAP İLE VE BAŞARILI OLURSA BURDAN CIKIŞ YAP DEDİK
                       finish()
                   }.addOnFailureListener {
                       Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                   }
               }
           }.addOnFailureListener {
               Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
           }
        }
    }
    fun selectedImage(view: View){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
        println("a")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            //izin iste
            println("a0")
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                Snackbar.make(view, "Permission needed for galery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                    //request permisson
                    println("a1")
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }.show()
            } else {
                //request permisson,
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
        }else{
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED){
                //İFİN İCİNDE KONTROL ET DEDİK GALERİYE İZİN VERİLİP VERİLMEDİGİNİ PERMİSONGRANTED (İZİN VERİLDİ)
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permisson meeded for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                        //should ile başlıyan izin alma mantıgını gösteriyim mi sonra hangisi icin extranalstroge
                        //true dönerse snackbar ile altta gösterecek
                        //indefınete kullanıcı secesiye kadar süre diyor
                        //İZİN ALMA YAPILACAK
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
                }else{
                    //izin alma
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else{
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //İNTENLE ONU GİDİP ALABİLİRZ ACTİON PİCK BUNDA KULLANILIR (GALERİYE GİT VERİ AL)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }

    private fun registerLauncher(){
        println("a3")
     activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
         if (result.resultCode== RESULT_OK){
             val intentFromResult=result.data
             if (intentFromResult!=null){
                 try {
                     if (Build.VERSION.SDK_INT>=28){
                         println("a4")
                         selectedPicture=intentFromResult.data
                         selectedPicture?.let {
                             binding.imageView1.setImageURI(it)
                             println("a5")
                         }
                     }
                 }catch (e:Exception){
                     e.printStackTrace()
                 }
             }
         }
     }
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if (result){
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)

            }else{
                //permission neddedd
Toast.makeText(this,"Permission Needed",Toast.LENGTH_LONG  ).show()
            }
        }
}
}