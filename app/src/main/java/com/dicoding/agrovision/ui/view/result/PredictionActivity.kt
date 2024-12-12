package com.dicoding.agrovision.ui.view.result

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dicoding.AgroVision.R
import com.dicoding.agrovision.PredictionViewModelFactory
import com.dicoding.agrovision.data.local.UserPreference
import com.dicoding.agrovision.data.repository.PredictionRepository
import com.dicoding.agrovision.ui.view.main.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class PredictionActivity : AppCompatActivity() {

    private lateinit var resultImage: ImageView
    private lateinit var resultText: TextView
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var userPreference: UserPreference

    private val viewModel: PredictionViewModel by viewModels {
        PredictionViewModelFactory(PredictionRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        resultImage = findViewById(R.id.resultImage)
        resultText = findViewById(R.id.resultText)
        btnSave = findViewById(R.id.btnSave)
        progressBar = findViewById(R.id.progressBar)

        userPreference = UserPreference(applicationContext)

        // Get data from intent
        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        val result = intent.getStringExtra(EXTRA_RESULT)
        val confidenceScore = intent.getFloatExtra(EXTRA_CONFIDENCE_SCORE, 0f)

        if (imageUriString.isNullOrEmpty() || result.isNullOrEmpty()) {
            Toast.makeText(this, "No data provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val imageUri = Uri.parse(imageUriString)

        // Display the image and result
        Glide.with(this)
            .load(imageUri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)  // Nonaktifkan caching untuk memastikan gambar baru dimuat
            .skipMemoryCache(true)  // Menghindari penggunaan cache di memori
            .placeholder(R.drawable.baseline_image_24)
            .error(R.drawable.error_image)
            .into(resultImage)


        resultText.text = "Prediction: $result\nConfidence: ${String.format("%.2f", confidenceScore)}%"

        btnSave.setOnClickListener {
            progressBar.visibility = ProgressBar.VISIBLE
            viewModel.saveResult.observe(this@PredictionActivity) { isSuccess ->
                progressBar.visibility = ProgressBar.GONE
                if (isSuccess) {
                    Toast.makeText(this@PredictionActivity, "Prediction saved successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@PredictionActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Optional: clear the activity stack
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@PredictionActivity, "Failed to save prediction.", Toast.LENGTH_SHORT).show()
                }
            }

            viewModel.error.observe(this@PredictionActivity) { errorMessage ->
                errorMessage?.let {
                    progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this@PredictionActivity, "Error: $it", Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }


            lifecycleScope.launch {
                val token = userPreference.getToken().first()

                if (token.isNullOrEmpty()) {
                    progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this@PredictionActivity, "Token not found.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val authorization = "Bearer $token"

                val file = getFileFromUri(this@PredictionActivity, imageUri)
                if (file == null || !file.exists()) {
                    progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this@PredictionActivity, "Image file not found.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val multipartFile = MultipartBody.Part.createFormData("file", file.name, requestBody)

                val resultBody = result.toRequestBody("text/plain".toMediaTypeOrNull())
                val accuracyBody = confidenceScore.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                // Panggil fungsi savePrediction
                viewModel.savePrediction(authorization, multipartFile, resultBody, accuracyBody)
                Log.d("Token", "Authorization Token: $token")
                if (result.isNullOrEmpty() || confidenceScore == null) {
                    progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this@PredictionActivity, "Invalid result or confidence score.", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                Log.d("API Request", "Authorization: $authorization")
                Log.d("API Request", "File Name: ${file.name}, File Path: ${file.path}")
                Log.d("API Request", "Result: $result, Accuracy: $confidenceScore")





            }
        }


    }

    fun getFileFromUri(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        return tempFile
    }


    companion object {
        const val EXTRA_IMAGE_URI = "imageUri"
        const val EXTRA_RESULT = "result"
        const val EXTRA_CONFIDENCE_SCORE = "confidenceScore"
    }
}