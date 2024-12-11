package com.dicoding.agrovision.ui.view.main

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dicoding.AgroVision.R
import com.dicoding.AgroVision.databinding.FragmentHomeBinding
import com.yalantis.ucrop.UCrop
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tombol untuk membuka galeri
        binding.albumButton.setOnClickListener {
            startGallery()
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                handleImageSelection(uri)
            } else {
                showToast("No picture selected")
            }
        }
    }

    private fun handleImageSelection(uri: Uri) {
        currentImageUri = uri
        showImage()
        startUCrop(uri)
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            binding.previewImageView.setImageURI(uri)
        }
    }

    private fun startUCrop(uri: Uri) {
        val options = getUCropOptions()
        val uCrop = UCrop.of(uri, getCroppedImageUri())
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .withOptions(options)

        uCrop.start(requireContext(), this)
    }

    private fun getUCropOptions() = UCrop.Options().apply {
        setCompressionQuality(90)
        setToolbarColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
        setActiveControlsWidgetColor(
            ContextCompat.getColor(requireContext(), R.color.secondaryColor)
        )
        setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
        setToolbarWidgetColor(Color.WHITE)
    }

    private fun getCroppedImageUri() = Uri.fromFile(File(requireContext().cacheDir, "cropped_image"))

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
