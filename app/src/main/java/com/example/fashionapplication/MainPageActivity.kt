package com.example.fashionapplication

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.fashionapplication.bottomNavigation.ClosetFragment
import com.example.fashionapplication.bottomNavigation.MainFragment
import com.example.fashionapplication.bottomNavigation.ProfileFragment
import com.example.fashionapplication.bottomNavigation.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.fragment_profile.*

class MainPageActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        user()
        val profile = findViewById<CircleImageView>(R.id.profile)
        profile.setOnClickListener {
            val fragmentManager:FragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.main_screen, ProfileFragment()).commit()
        }
        bottom_navi.setOnNavigationItemSelectedListener(this)
        supportFragmentManager.beginTransaction().add(R.id.main_screen, MainFragment()).commit()
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // 사진의 경로를 불러올수 있게 하는 권한
        val editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit()
        editor.putString("profileid", FirebaseAuth.getInstance().currentUser?.uid)
        editor.apply()
        when (item.itemId) {
            R.id.item_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_screen, MainFragment())
                    .commitAllowingStateLoss()
                return true
            }
            R.id.item_closet -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_screen, ClosetFragment())
                    .commitAllowingStateLoss()
                return true
            }
            R.id.item_profile -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_screen, ProfileFragment())
                    .commitAllowingStateLoss()
                return true
            }
            R.id.item_search -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_screen, SearchFragment())
                    .commitAllowingStateLoss()
                return true
            }
        }
        return false
    }
}
