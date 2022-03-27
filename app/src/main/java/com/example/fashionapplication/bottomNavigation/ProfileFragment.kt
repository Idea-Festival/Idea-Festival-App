package com.example.fashionapplication.bottomNavigation

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.fashionapplication.Adapter.MyPhotoAdapter
import com.example.fashionapplication.data.PostDto
import com.example.fashionapplication.data.User
import com.example.fashionapplication.databinding.FragmentProfileBinding
import com.example.fashionapplication.function.OptionsActivity
import com.example.fashionapplication.function.writingPostActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {

    private lateinit var firebaseUser: FirebaseUser
    private var profileid: String? = null
    private lateinit var binding: FragmentProfileBinding
    private lateinit var adapter: MyPhotoAdapter
    private lateinit var postList: ArrayList<PostDto>
    private lateinit var auth: FirebaseAuth
    val pickImageFromAlbum = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pres: SharedPreferences = requireContext().getSharedPreferences("PRES", MODE_PRIVATE)
        profileid = pres.getString("profileid", "none")
        auth = FirebaseAuth.getInstance()

        binding = FragmentProfileBinding.inflate(inflater, container, false)


        binding.postingRecyclerview.setHasFixedSize(true)
        val layoutmanager: LinearLayoutManager = GridLayoutManager(context, 3)
        binding.postingRecyclerview.layoutManager = layoutmanager
        postList = arrayListOf()
        adapter = MyPhotoAdapter(context, postList)
        binding.postingRecyclerview.adapter = adapter

        userInfo()
        user()
        uploadProfileImage()    // 이미지 업로드

        binding.mainImg.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, pickImageFromAlbum)
        }

        if (profileid.equals(firebaseUser.uid)) {
            binding.profileFollowBtn.visibility = View.VISIBLE
        } else {
            checkFollow()
        }

        binding.profileFollowBtn.setOnClickListener {
            val btn = binding.profileFollowBtn.text.toString()
            if (btn.equals("follow")) {
                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)
                    .child("following").child(profileid!!).setValue(true)
                FirebaseDatabase.getInstance().reference.child("Follow").child(profileid!!)
                    .child("followers").child(firebaseUser.uid).setValue(true)
                addNotification()
            }
        }
        binding.addPost.setOnClickListener {
            startActivity(Intent(context, writingPostActivity::class.java))
        }

        binding.profileSetting.setOnClickListener {
            startActivity(Intent(context, OptionsActivity::class.java))
        }

        return binding.root
    }

    private fun user() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(auth.uid.toString())

        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.userName.text = user?.username
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun userInfo() {
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid!!)
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (context == null) {
                    return
                }
                val user = snapshot.getValue(User::class.java)
                if (snapshot.exists()) {
                    Glide.with(context!!).load(user?.imageurl).into(binding.mainImg)
                    binding.userName.text = user?.username
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun uploadProfileImage() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseDatabase.reference.child("Users").child(auth.uid.toString()).child("imageurl").addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val image = snapshot.getValue(String::class.java)
                Glide.with(context!!)
                    .load(image)
                    .circleCrop()
                    .into(binding.mainImg)
                Log.d("TAG", "onDataChange: ${image.toString()}")   // 이미지 https 주소
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun checkFollow() {
        val reference = FirebaseDatabase.getInstance().reference
            .child("Follow").child(firebaseUser.uid).child("following")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(profileid!!).exists()) {
                    binding.profileFollowBtn.text = "following"
                } else {
                    binding.profileFollowBtn.text = "follow"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addNotification() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance()
            .getReference("Notifications").child(profileid!!)

        val hashMap:HashMap<String, Any> = HashMap()
        hashMap.put("userid",firebaseUser.uid)
        hashMap.put("text", "start follow")
        hashMap.put("postid", "")
        hashMap.put("ispost", false)

        reference.push().setValue(hashMap)
    }

    var uriPhoto: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImageFromAlbum) {
            if (resultCode == Activity.RESULT_OK) {
                uriPhoto = data?.data
                binding.mainImg.setImageURI(uriPhoto)
                funImageUpload()
            }
        }
    }

    private fun funImageUpload() {
        val imageFileName = "${auth.uid}.png"
        val storageRef = FirebaseStorage.getInstance().reference.child("profileImage").child(imageFileName)

        storageRef.putFile(uriPhoto!!).addOnSuccessListener {
            Toast.makeText(context, "사진이 업로드됨", Toast.LENGTH_SHORT).show()
        }
    }
}