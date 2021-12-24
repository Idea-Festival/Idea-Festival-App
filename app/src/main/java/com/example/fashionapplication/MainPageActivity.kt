package com.example.fashionapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import com.example.fashionapplication.bottomNavigation.ClosetFragment
import com.example.fashionapplication.bottomNavigation.MainFragment
import com.example.fashionapplication.bottomNavigation.ProfileFragment
import com.example.fashionapplication.bottomNavigation.SearchFragment
import com.example.fashionapplication.function.writingPostActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main_page.*

class MainPageActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var img: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        img = findViewById(R.id.profile)
        img.clipToOutline = true    // 이미지를 둥글게 만들어줌

        bottom_navi.setOnNavigationItemSelectedListener(this)
        supportFragmentManager.beginTransaction().add(R.id.main_screen, MainFragment()).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
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
