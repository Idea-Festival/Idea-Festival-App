package com.example.fashionapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.fashionapplication.bottomNavigation.ProfileFragment
import com.example.fashionapplication.databinding.ActivityMainPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        user()
        setBtmNavi()
    }

    private fun setBtmNavi() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavi.setupWithNavController(navController)
    }

    private fun user() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(auth.uid.toString()).child("imageurl")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(String::class.java)
                Glide.with(this@MainPageActivity)
                    .load(user)
                    .into(binding.profile)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
