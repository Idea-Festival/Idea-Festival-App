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
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.bumptech.glide.request.RequestOptions
import com.example.fashionapplication.R
import com.example.fashionapplication.data.FollowDto
import com.example.fashionapplication.data.PostDto
import com.example.fashionapplication.data.User
import com.example.fashionapplication.databinding.FragmentProfileBinding
import com.example.fashionapplication.function.writingPostActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {

    private lateinit var firebaseUser: FirebaseUser
    private var profileid: String? = null
    private lateinit var binding: FragmentProfileBinding
    private lateinit var adapter: ProfileFragmentRecyclerAdapter
    private lateinit var postList: ArrayList<PostDto>
    private lateinit var auth: FirebaseAuth
    val pickImageFromAlbum = 0
    private var currentUserUid: String? = null // 자신의 계정인지 아닌지 구분하는 변수

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pres: SharedPreferences = requireContext().getSharedPreferences("PRES", MODE_PRIVATE)
        profileid = pres.getString("profileid", "none")
        auth = FirebaseAuth.getInstance()

        currentUserUid = arguments?.getString("destinationUid")
        if (currentUserUid == null) {
            currentUserUid = auth.uid.toString()
        }
        getProfileImage(currentUserUid)

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        Log.d("SUCCESS", "onCreateView from main page activity get uid: ${arguments?.getString("uid")}")

        if (arguments?.getString("destinationUid") != null) {   // 메인의 아이템 클릭으로 온 경우
            if (arguments?.getString("destinationUid") == auth.uid) {
                Log.d("SUCCESS", "onCreateView profile id: $profileid")
                initRecyclerView()
                userInfo(profileid!!)
                user()
                uploadProfileImage()
                getProfileImage(profileid)
            } else {
                val uid = arguments?.getString("destinationUid")
                userInfo(uid!!)
                initRecyclerView()
                getProfileImage(uid)
            }
        } else {
            Log.d("SUCCESS", "onCreateView profile id: $profileid")
            initRecyclerView()
            userInfo(profileid!!)
            user()
            uploadProfileImage()    // 이미지 업로드
            getProfileImage(profileid)
        }

        // 자신의 프로필인 경우
        if (auth.uid == currentUserUid) {
            binding.profileFollowBtn.visibility = View.GONE
        } else {    // 자신의 프로필이 아닌 경우
            binding.profileFollowBtn.visibility = View.VISIBLE

            binding.profileFollowBtn.setOnClickListener {
                requestFollow()
            }
        }

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
            Navigation.findNavController(binding.root).navigate(R.id.action_profileFragment_to_optionsActivity)
        }
        getFollowerAndGetFollowing()

        return binding.root
    }

    private fun initRecyclerView() {
        binding.postingRecyclerview.setHasFixedSize(true)
        val layoutmanager: LinearLayoutManager = GridLayoutManager(activity, 3)
        binding.postingRecyclerview.layoutManager = layoutmanager
        postList = arrayListOf()
        adapter = ProfileFragmentRecyclerAdapter()
        binding.postingRecyclerview.adapter = adapter
    }

    inner class ProfileFragmentRecyclerAdapter: RecyclerView.Adapter<ProfileFragmentRecyclerAdapter.ViewHolder>() {

        private var postDto = arrayListOf<PostDto>()
        init {
            val fireStore = FirebaseFirestore.getInstance()
            var uid = arguments?.getString("destinationUid")
            if (uid == null) {
                uid = auth.uid
            }
            fireStore.collection("posts").get().addOnSuccessListener { result ->
                for (snapshot in result) {
                    if (snapshot["uid"].toString() == uid) {
                        postDto.add(snapshot.toObject(PostDto::class.java))
                    }
                }

                binding.postCount.text = postDto.size.toString() + "개"
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_fragment_recycler_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val width = resources.displayMetrics.widthPixels / 3
            holder.profileImage.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            Glide.with(holder.itemView.context).load(postDto[position].imageUrl).into(holder.profileImage)
            binding.postCount.text = postDto.size.toString() + "개"
            holder.itemView.layoutParams.height = 400
            holder.itemView.requestLayout()
        }

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val profileImage = itemView.findViewById<ImageView>(R.id.post_image_profileFragment)
        }

        override fun getItemCount(): Int {
            return postDto.size
        }
    }

    val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private fun requestFollow() {
        val tsDocFollowing = fireStore.collection("follow").document(currentUserUid!!)
        fireStore.runTransaction { transaction ->
            var followDto: FollowDto? = transaction.get(tsDocFollowing).toObject(FollowDto::class.java)

            if (followDto == null) {
                followDto = FollowDto()
                followDto.followingCount = 1
                followDto.followers[auth.uid!!] = true

                transaction.set(tsDocFollowing, followDto)
                binding.followerCount.text = followDto.followingCount.toString()
                return@runTransaction
            }

            if (followDto.followings.containsKey(auth.uid)) {   // follow 한 상태
                followDto.followingCount -= 1
                followDto.followers.remove(auth.uid)
                binding.followerCount.text = followDto.followingCount.toString()
            } else {    // 안한 상태
                followDto.followingCount += 1
                followDto.followers[auth.uid!!] = true
                binding.followerCount.text = followDto.followingCount.toString()
            }
            transaction.set(tsDocFollowing, followDto)
            return@runTransaction
        }

        val tsDocFollower = fireStore.collection("posts").document(auth.uid!!)
        fireStore.runTransaction { transaction ->
            var followDto = transaction.get(tsDocFollower).toObject(FollowDto::class.java)
            if (followDto == null) {
                followDto = FollowDto()
                followDto!!.followerCount = 1
                followDto!!.followers[currentUserUid!!] = true

                transaction.set(tsDocFollower, followDto!!)
                return@runTransaction
            }

            if (followDto!!.followers.containsKey(currentUserUid)) {    // 상대의 계정에 내가 팔로우된 경우
                followDto!!.followerCount -= 1
                followDto!!.followers.remove(currentUserUid)
            } else {    // 안한 경우
                followDto!!.followerCount += 1
                followDto!!.followers[currentUserUid!!] = true
            }
            transaction.set(tsDocFollower, followDto!!)
            return@runTransaction
        }
    }

    private fun getFollowerAndGetFollowing() {
        fireStore.collection("posts").document(auth.uid!!).addSnapshotListener { documentSnapshot, error ->
            if (documentSnapshot == null) return@addSnapshotListener

            val followDto = documentSnapshot.toObject(FollowDto::class.java)
            if (followDto?.followerCount != null) {
                binding.followerCount.text = followDto.followerCount.toString()

                if (followDto.followers.containsKey(currentUserUid)) {
                    binding.profileFollowBtn.text = "unFollow"
                } else {
                    if (auth.uid != currentUserUid) {
                        binding.profileFollowBtn.text = "Follow"
                        binding.profileFollowBtn.background.colorFilter = null
                    }
                }
            }
        }
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

    private fun userInfo(uid: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(uid)
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
        hashMap["userid"] = firebaseUser.uid
        hashMap["text"] = "start follow"
        hashMap["postid"] = ""
        hashMap["ispost"] = false

        reference.push().setValue(hashMap)
    }

    var uriPhoto: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImageFromAlbum) {
            if (resultCode == Activity.RESULT_OK) {
                uriPhoto = data?.data
                binding.mainImg.setImageURI(uriPhoto)
                funImageUpload(uriPhoto)
            }
        }
    }

    private fun funImageUpload(image: Uri?) {
        val imageFileName = "${auth.uid}.png"
        val storageRef = FirebaseStorage.getInstance().reference.child("profileImage").child(imageFileName)

        storageRef.putFile(image!!).continueWithTask { 
            return@continueWithTask storageRef.downloadUrl
        }.addOnSuccessListener {
            val map = HashMap<String, Any>()
            map["image"] = it.toString()
            FirebaseFirestore.getInstance().collection("profileImages").document(auth.uid!!).set(map)
        }
    }

    private fun getProfileImage(uid: String?) {
        fireStore.collection("profileImages").document(uid!!).addSnapshotListener { value, error ->
            if (value == null) return@addSnapshotListener
            if (value.data != null) {
                val url = value.data!!["image"]
                Glide.with(activity!!)
                    .load(url)
                    .apply(RequestOptions().circleCrop())
                    .into(binding.mainImg)
            }
        }
    }
}