package com.example.corouseljava

import SlideModelWithUrl
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {
    private lateinit var mainslider: ImageSlider
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference = firebaseDatabase.reference
    private val childref = databaseReference.child("URL")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        mainslider = findViewById(R.id.image_slider)
        val remoteimages: MutableList<SlideModelWithUrl> = ArrayList()

        FirebaseDatabase.getInstance().reference.child("Slider")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (data in dataSnapshot.children) {
                        val imageUrl = data.child("url").value.toString()
                        val imageTitle = data.child("title").value.toString()
                        val websiteUrl = data.child("websiteUrl").value.toString()

                        val slideModel = SlideModel(imageUrl, imageTitle, ScaleTypes.FIT)
                        val slideModelWithUrl = SlideModelWithUrl(slideModel, websiteUrl)
                        remoteimages.add(slideModelWithUrl)
                    }

                    mainslider.setImageList(remoteimages.map { it.slideModel }, ScaleTypes.FIT)
                    mainslider.setItemClickListener(object : ItemClickListener {
                        override fun doubleClick(position: Int) {
                        }

                        override fun onItemSelected(i: Int) {
                            val websiteUrl = remoteimages[i].websiteUrl
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(websiteUrl)
                            startActivity(intent)
                        }
                    })
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }
}

