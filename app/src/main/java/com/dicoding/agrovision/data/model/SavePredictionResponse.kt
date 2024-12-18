package com.dicoding.agrovision.data.model

import com.google.gson.annotations.SerializedName

data class SavePredictionResponse(

	@field:SerializedName("prediction_id")
	val predictionId: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
