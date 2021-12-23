package com.example.fashionapplication.bottomNavigation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fashionapplication.MainPageActivity
import com.example.fashionapplication.databinding.FragmentProfileBinding
import com.example.fashionapplication.function.writingPostActivity

class ProfileFragment : Fragment() {

    private lateinit var post:writingPostActivity
    private lateinit var binding:FragmentProfileBinding
    lateinit var mainPageActivity: MainPageActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainPageActivity = activity as MainPageActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        post = writingPostActivity()
        binding.temp.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                mainPageActivity.changeToMenu()
            }
        })
        // Inflate the layout for this fragment
        val view = binding.root
        return view
    }
}