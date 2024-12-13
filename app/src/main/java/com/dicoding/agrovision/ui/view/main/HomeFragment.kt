package com.dicoding.agrovision.ui.view.main

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null

    // ViewModel for prediction
    private val predictionViewModel: PredictionViewModel by viewModels {
        PredictionViewModelFactory(PredictionRepository())
    }

    // Declare ActivityResultLaunchers for UCrop and TakePicture
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var uCropLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // User is logged in, retrieve name from Firestore
            val userId = currentUser.uid
            val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

            userRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val displayName = documentSnapshot.getString("name") ?: "User" // Default to "User" if name is not available
                    binding.welcomeText.text = "Selamat datang, $displayName!"
                } else {
                    // Handle case where user data does not exist in Firestore
                    binding.welcomeText.text = "Selamat datang!"
                }
            }.addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error fetching user data: ${exception.message}")
                binding.welcomeText.text = "Selamat datang!"
            }
        } else {
            // User is not logged in
            binding.welcomeText.text = "Silakan login terlebih dahulu"
        }
    }


    override fun onStop() {
        super.onStop()
        // Remove the listener to avoid memory leaks
        FirebaseAuth.getInstance().removeAuthStateListener { firebaseAuth ->
            // You don't need any implementation here
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // Initialize UCrop activity result launcher
        uCropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultUri = UCrop.getOutput(result.data!!)
                resultUri?.let {
                    currentImageUri = it
                    binding.previewImageView.setImageURI(it)
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val error = UCrop.getError(result.data!!)
                error?.printStackTrace()
                showToast("Error cropping image: ${error?.message}")
            }
        }

        // Initialize camera image capture launcher
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                // After taking the picture, we will handle the crop
                startUCrop(currentImageUri!!)
            } else {
                showToast("Failed to take picture")
            }
        }

        // Set up buttons for gallery, camera, and check action
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Album button to pick an image from gallery
        binding.albumButton.setOnClickListener {
            startGallery()
        }

        // Camera button to capture a photo
        binding.cameraButton.setOnClickListener {
            takePicture()
        }

        // Check button to analyze the captured image
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

    private fun takePicture() {
        if (isCameraPermissionGranted()) {
            val imageFileName = "temp_image_${System.currentTimeMillis()}.jpg"
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val contentResolver = requireContext().contentResolver
            currentImageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            // Ensure the URI is not null before launching the camera
            currentImageUri?.let { uri ->
                takePictureLauncher.launch(uri)
            } ?: run {
                showToast("Failed to capture image: URI is null.")
            }
        } else {
            requestCameraPermission() // Request permission if not granted
        }
    }



    private fun startUCrop(uri: Uri) {
        // Prepare for cropping the image using UCrop
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_image"))
        val options = getUCropOptions()

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
                predictionViewModel.getPrediction(it)
                observePredictionResult()
            }
        } ?: showToast("No image selected")
    }

    private fun observePredictionResult() {
        predictionViewModel.predictionResult.observe(viewLifecycleOwner) { result ->
            if (result != null && result.resultClass != null) {
                moveToPredictionActivity(
                    result.resultClass!!,  // Prediction result
                    result.accuracy!!,     // Confidence score
                    currentImageUri!!      // Image URI
                )
            } else {
                showToast("Prediction failed or result is null")
            }
        }

        predictionViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                showToast(error)
            }
        }
    }

    private fun moveToPredictionActivity(result: String, accuracy: Float, uri: Uri) {
        val intent = Intent(requireContext(), PredictionActivity::class.java)
        intent.putExtra(PredictionActivity.EXTRA_IMAGE_URI, uri.toString())
        intent.putExtra(PredictionActivity.EXTRA_RESULT, result)
        intent.putExtra(PredictionActivity.EXTRA_CONFIDENCE_SCORE, accuracy)
        startActivity(intent)
    }

    private fun getUCropOptions() = UCrop.Options().apply {
        setCompressionQuality(90)
        setToolbarColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
        setActiveControlsWidgetColor(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
        setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
        setToolbarWidgetColor(Color.WHITE)
    }

    private fun getFileFromUri(uri: Uri): File? {
        val file = File(requireContext().cacheDir, "temp_image.jpg")
        requireContext().contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private val CAMERA_PERMISSION_REQUEST_CODE = 101

    // Check if camera permission is granted
    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request camera permission
    private fun requestCameraPermission() {
        if (!isCameraPermissionGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            takePicture()
        }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture() // Call takePicture if permission is granted
            } else {
                showToast("Camera permission is required to take a picture")
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


