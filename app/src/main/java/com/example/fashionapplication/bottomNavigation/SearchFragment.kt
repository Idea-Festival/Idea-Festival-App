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
import com.google.firebase.auth.FirebaseUser
import android.widget.Button
import de.hdodenhof.circleimageview.CircleImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot

import androidx.fragment.app.FragmentActivity

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var mUsers: ArrayList<User>
    private lateinit var searchBar: EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.search_recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        searchBar = view.findViewById(R.id.search_text)

        mUsers = ArrayList()
        userAdapter = UserAdapter(context, mUsers)
        recyclerView.adapter = userAdapter

        readUsers()
        searchBar.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchUsers(p0.toString().toLowerCase())
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        return view
    }

    private fun readUsers() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (searchBar.text.toString()=="") {
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
            .startAt(s).endAt(s+"\uf8ff")

        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mUsers.clear()
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)
                    mUsers.add(user!!)
                }
                userAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    inner class UserAdapter(private var mContext: Context?, private var mUsers: List<User>) :
        RecyclerView.Adapter<UserAdapter.ViewHolder>() {
        private val firebaseUser: FirebaseUser? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(mContext)
                .inflate(R.layout.search_recyclerview_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
            var user: User = mUsers.get(position)
            holder.btn_follow?.visibility = View.VISIBLE

            holder.username?.text = user.username
            holder.fullname?.text = user.fullname
            Glide.with(mContext!!).load(user.imageurl).into(holder.image_profile!!)
            isFollowing(user.id!!, holder.btn_follow!!)

            if (user.id.equals(firebaseUser.uid)) {
                holder.btn_follow?.visibility = View.GONE
            }

            holder.itemView.setOnClickListener(object: View.OnClickListener {
                override fun onClick(p0: View?) {
                    val editor: SharedPreferences.Editor = mContext?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)!!.edit()
                    editor.putString("profileid", user.id)
                    editor.apply()

                    (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.main_screen, ProfileFragment()).commit()
                }
            })

            holder.btn_follow?.setOnClickListener(object: View.OnClickListener {
                override fun onClick(p0: View?) {
                    if (holder.btn_follow?.text.toString().equals("follow")) {
                        FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)
                            .child("following").child(user.id!!).setValue(true)
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(user.id!!)
                            .child("followers").child(firebaseUser.getUid()).setValue(true)
                    } else {
                        FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)
                            .child("following").child(user.id!!).removeValue()
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(user.id!!)
                            .child("followers").child(firebaseUser.getUid()).removeValue()
                    }
                }
            })
        }

        private fun isFollowing(userid: String, button: Button) {
            val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
                .child("Follow").child(firebaseUser!!.uid).child("following")
            reference.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(userid).exists()) {
                        button.text = "following"
                    } else {
                        button.text = "follow"
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }

        override fun getItemCount(): Int {
            return mUsers.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var username: TextView? = null
            var fullname: TextView? = null
            var image_profile: CircleImageView? = null
            var btn_follow: Button? = null

            fun ViewHolder(itemView: View) {
                super.itemView
                username = itemView.findViewById(R.id.searching_user_name)
                fullname = itemView.findViewById(R.id.searching_user_fullname)
                image_profile = itemView.findViewById(R.id.searching_user_profile)
                btn_follow = itemView.findViewById(R.id.searching_following)
            }
        }
    }
}