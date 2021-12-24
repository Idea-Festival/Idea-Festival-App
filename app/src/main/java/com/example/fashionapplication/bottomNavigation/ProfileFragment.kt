package com.example.fashionapplication.bottomNavigation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fashionapplication.databinding.FragmentProfileBinding
import com.example.fashionapplication.function.writingPostActivity

class ProfileFragment : Fragment() {

    private lateinit var binding:FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        binding.mainImg.clipToOutline = true

        binding = FragmentProfileBinding.inflate(layoutInflater)
        binding.addPost.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(context, writingPostActivity::class.java))
            }
        })
        val view = binding.root
        return view
    }
}