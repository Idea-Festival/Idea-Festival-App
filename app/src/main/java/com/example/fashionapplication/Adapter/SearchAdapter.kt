package com.example.fashionapplication.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapplication.R
import com.example.fashionapplication.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class SearchAdapter(mContext: Context, users: List<User>, isFragment:Boolean)
    : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var context: Context = mContext
    private var users: List<User> = users
    private var isFragment: Boolean = isFragment
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.search_recyclerview_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val user:User = users.get(position)

        holder.followBtn.visibility = View.VISIBLE
        isfollowing(user.id, holder.followBtn)
        holder.followBtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(p0: View?) {
                if (holder.followBtn.text.toString().equals("follow")) {
                    FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser!!.uid)
                        .child("following").child(user.id!!).setValue(true)
                    FirebaseDatabase.getInstance().reference.child("Follow").child(user.id!!)
                        .child("followers").child(firebaseUser!!.uid).setValue(true)

                    addNotification(user.id!!)
                } else {
                    FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser!!.uid)
                        .child("following").child(user.id!!).removeValue()
                    FirebaseDatabase.getInstance().reference.child("Follow").child(user.id!!)
                        .child("followers").child(firebaseUser!!.uid).removeValue()
                }
            }
        })
    }
    override fun getItemCount(): Int {
        return users.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile: CircleImageView = itemView.findViewById(R.id.searching_user_profile)
        var username: TextView = itemView.findViewById(R.id.searching_user_name)
        var followBtn: Button = itemView.findViewById(R.id.searching_following)
    }

    private fun addNotification(userid: String) {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid)

        val hashMap:HashMap<String, Any> = HashMap()
        hashMap.put("userid",firebaseUser!!.uid)
        hashMap.put("postid", "")
        hashMap.put("ispost", false)

        reference.push().setValue(hashMap)
    }

    private fun isfollowing(userid:String?, button: Button) {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val reference:DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("Follow").child(firebaseUser!!.uid).child("following")
        reference.addValueEventListener(object:ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(userid!!).exists()) {
                    button.text = "following"
                } else {
                    button.text = "follow"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}