package com.example.fashionapplication.function

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.fashionapplication.R
import com.example.fashionapplication.databinding.WritePostingBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class writingPostActivity: Fragment() {

    private lateinit var binding: WritePostingBinding
    private final val PICK_IMAGE_FROM_ALBUM = 200
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = WritePostingBinding.inflate(layoutInflater)
        return binding.root
    }
}