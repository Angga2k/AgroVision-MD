package com.dicoding.agrovision.ui.view.main

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dicoding.AgroVision.R
import com.dicoding.AgroVision.databinding.FragmentHomeBinding
import com.dicoding.agrovision.ui.view.result.PredictionActivity
import com.yalantis.ucrop.UCrop
import java.io.File
import androidx.fragment.app.viewModels
import com.dicoding.agrovision.PredictionViewModelFactory
import com.dicoding.agrovision.data.repository.PredictionRepository
import com.dicoding.agrovision.ui.view.result.PredictionViewModel


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null

    // ViewModel untuk prediksi
    private val predictionViewModel: PredictionViewModel by viewModels {
        PredictionViewModelFactory(PredictionRepository())
    }

    // Declare ActivityResultLauncher for UCrop
    private lateinit var uCropLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UCrop activity result launcher
        uCropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultUri = UCrop.getOutput(result.data!!)
                resultUri?.let {
                    // Display cropped image
                    currentImageUri = it
                    binding.previewImageView.setImageURI(it)
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val error = UCrop.getError(result.data!!)
                error?.printStackTrace()
                showToast("Error cropping image: ${error?.message}")
            }
        }

        // Set up button click listeners
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.albumButton.setOnClickListener {
            startGallery()
        }

        binding.checkButton.setOnClickListener {
            analyzeImage()
        }
    }

    private fun startGallery() {
        val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        launcherGallery.launch(request)
    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            handleImageSelection(uri)
        } else {
            showToast("No picture selected")
        }
    }

    private fun handleImageSelection(uri: Uri) {
        currentImageUri = uri
        val contentUri = getFileUri(uri)
        if (contentUri != null) {
            startUCrop(contentUri)
        } else {
            showToast("Unable to access selected image")
        }
    }

    private fun getFileUri(uri: Uri): Uri? {
        return try {
            val file = File(requireContext().cacheDir, "temp_image.jpg")
            requireContext().contentResolver.openInputStream(uri).use { input ->
                file.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }
            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun startUCrop(uri: Uri) {
        val options = getUCropOptions()
        val destinationUri = getCroppedImageUri()

        try {
            val uCrop = UCrop.of(uri, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(1000, 1000)
                .withOptions(options)

            uCropLauncher.launch(uCrop.getIntent(requireContext()))
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Error starting crop: ${e.message}")
        }
    }

    private fun analyzeImage() {
        currentImageUri?.let { uri ->
            val file = getFileFromUri(uri)
            file?.let {
                // Memanggil ViewModel untuk melakukan prediksi dengan file gambar
                predictionViewModel.getPrediction(it)

                // Mengamati hasil prediksi
                observePredictionResult()
            }
        } ?: showToast("No image selected")
    }


    private fun observePredictionResult() {
        predictionViewModel.predictionResult.observe(viewLifecycleOwner) { result ->
            if (result != null && result.resultClass != null) {
                // Mengambil hasil prediksi dari ViewModel dan menavigasi ke Activity
                moveToPredictionActivity(
                    result.resultClass!!,  // Hasil prediksi
                    result.accuracy!!,     // Tingkat akurasi
                    currentImageUri!!      // URI gambar
                )
            } else {
                showToast("Prediction failed or result is null")
            }
        }

        predictionViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                showToast(error)  // Menampilkan pesan error jika ada
            }
        }
    }


    private fun moveToPredictionActivity(result: String, accuracy: Float, uri: Uri) {
        val intent = Intent(requireContext(), PredictionActivity::class.java)
        intent.putExtra(PredictionActivity.EXTRA_IMAGE_URI, uri.toString())  // URI gambar
        intent.putExtra(PredictionActivity.EXTRA_RESULT, result)            // Hasil prediksi
        intent.putExtra(PredictionActivity.EXTRA_CONFIDENCE_SCORE, accuracy) // Tingkat akurasi
        startActivity(intent)
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun getUCropOptions() = UCrop.Options().apply {
        setCompressionQuality(90)
        setToolbarColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
        setActiveControlsWidgetColor(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
        setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
        setToolbarWidgetColor(Color.WHITE)
    }

    private fun getCroppedImageUri() = Uri.fromFile(File(requireContext().cacheDir, "cropped_image"))

    private fun getFileFromUri(uri: Uri): File? {
        val file = File(requireContext().cacheDir, "temp_image.jpg")
        requireContext().contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
