package com.example.fashionapplication

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.fashionapplication.bottomNavigation.ClosetFragment
import com.example.fashionapplication.bottomNavigation.MainFragment
import com.example.fashionapplication.bottomNavigation.ProfileFragment
import com.example.fashionapplication.bottomNavigation.SearchFragment
import com.example.fashionapplication.databinding.ActivityMainPageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainPageActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainPageBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.bottomNavi.setOnNavigationItemSelectedListener(this)
        supportFragmentManager.beginTransaction().add(R.id.linear, MainFragment()).commitAllowingStateLoss()

        user()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.mainFragment -> {
                supportFragmentManager.beginTransaction().replace(R.id.linear, MainFragment().apply {
                    arguments = Bundle().apply {
                        putString("uid", intent.getStringExtra("userId"))
                    }
                }).commitAllowingStateLoss()
                return true
            }
            R.id.closetFragment -> {
                supportFragmentManager.beginTransaction().replace(R.id.linear, ClosetFragment().apply {
                    arguments = Bundle().apply {
                        putString("uid", intent.getStringExtra("userId"))
                    }
                }).commitAllowingStateLoss()
                return true
            }
            R.id.profileFragment -> {
                supportFragmentManager.beginTransaction().replace(R.id.linear, ProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString("uid", intent.getStringExtra("userId"))
                    }
                }).commitAllowingStateLoss()
                return true
            }
            R.id.searchFragment -> {
                supportFragmentManager.beginTransaction().replace(R.id.linear, SearchFragment()).commitAllowingStateLoss()
                return true
            }
        }
        return false
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
