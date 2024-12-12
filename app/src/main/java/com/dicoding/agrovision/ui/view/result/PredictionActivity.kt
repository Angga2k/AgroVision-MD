package com.dicoding.agrovision.ui.view.result

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.AgroVision.R
import com.dicoding.agrovision.PredictionViewModelFactory
import com.dicoding.agrovision.data.repository.PredictionRepository
import java.io.File


class PredictionActivity : AppCompatActivity() {

    private lateinit var resultImage: ImageView
    private lateinit var resultText: TextView
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        resultImage = findViewById(R.id.resultImage)
        resultText = findViewById(R.id.resultText)
        btnSave = findViewById(R.id.btnSave)

        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        val result = intent.getStringExtra(EXTRA_RESULT)
        val confidenceScore = intent.getFloatExtra(EXTRA_CONFIDENCE_SCORE, 0f)

        if (imageUriString.isNullOrEmpty() || result.isNullOrEmpty()) {
            Toast.makeText(this, "No data provided", Toast.LENGTH_SHORT).show()
            finish() // Exit the activity if data is incomplete
            return
        }

        val imageUri = Uri.parse(imageUriString)

        Glide.with(this)
            .load(imageUri)
            .placeholder(R.drawable.baseline_image_24)
            .error(R.drawable.error_image)
            .into(resultImage)

        // Display the result
        resultText.text = "Prediction: $result\nConfidence: ${String.format("%.2f", confidenceScore)}%"

        btnSave.setOnClickListener {
            Toast.makeText(this, "Feature to save predictions coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_IMAGE_URI = "imageUri"
        const val EXTRA_RESULT = "result"
        const val EXTRA_CONFIDENCE_SCORE = "confidenceScore"
    }
}


