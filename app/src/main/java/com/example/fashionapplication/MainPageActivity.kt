package com.example.fashionapplication

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.fashionapplication.bottomNavigation.ClosetFragment
import com.example.fashionapplication.bottomNavigation.MainFragment
import com.example.fashionapplication.bottomNavigation.ProfileFragment
import com.example.fashionapplication.bottomNavigation.SearchFragment
import com.example.fashionapplication.databinding.ActivityMainPageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main_page.*

class MainPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user()
        setBtmNavi()
    }

    private fun setBtmNavi() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavi.setupWithNavController(navController)
    }

    private fun user() {
        val prefs: SharedPreferences = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val profileid = prefs.getString("profileid", "none").toString()
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileid)

        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(com.example.fashionapplication.data.User::class.java)
                Glide.with(this@MainPageActivity).load(user?.imageurl).into(profile)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
