package com.example.fashionapplication.bottomNavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionapplication.Adapter.MainFragmentRecyclerAdapter
import com.example.fashionapplication.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainFragment : Fragment() {
    private var fireStore: FirebaseFirestore? = null
    private var uid: String? = null
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fireStore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.postRecyclerview.adapter = MainFragmentRecyclerAdapter()
        binding.postRecyclerview.layoutManager = LinearLayoutManager(context)

        return binding.root
    }
}
