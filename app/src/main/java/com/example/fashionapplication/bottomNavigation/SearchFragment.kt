package com.example.fashionapplication.bottomNavigation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fashionapplication.R
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapplication.data.User
import com.google.firebase.database.*
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.example.fashionapplication.Adapter.SearchAdapter
import com.example.fashionapplication.R.id.searching_user_profile
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.main_img
import kotlinx.android.synthetic.main.search_recyclerview_item.*
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.search_recyclerview_item.searching_user_profile as searching_user_profile1

class SearchFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: SearchAdapter
    private lateinit var mUsers: ArrayList<User>
    private lateinit var searchBar: EditText
    lateinit var a: CircleImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)
        a = view.findViewById(searching_user_profile)
        recyclerView = view.findViewById(R.id.search_recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        searchBar = view.findViewById(R.id.search_text)

        mUsers = arrayListOf()
        userAdapter = SearchAdapter(requireContext(), mUsers, true)
        recyclerView.adapter = userAdapter

        readUsers()
        searchBar.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchUsers(p0.toString().lowercase(Locale.getDefault()))
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        return view
    }

    private fun readUsers() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (searchBar.text.toString() == "") {
                    mUsers.clear()
                    for (dataSnapshot in snapshot.children) {
                        val user = dataSnapshot.getValue(User::class.java)
                        mUsers.add(user!!)
                    }
                    userAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun searchUsers(s: String) {
        val query: Query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
            .startAt(s)
            .endAt(s+"\uf8ff")

        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (searchBar.text.toString() == "") {
                    mUsers.clear()
                    for (dataSnapshot in snapshot.children) {
                        val user = dataSnapshot.getValue(User::class.java)
                        mUsers.add(user!!)
                    }
                    userAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}