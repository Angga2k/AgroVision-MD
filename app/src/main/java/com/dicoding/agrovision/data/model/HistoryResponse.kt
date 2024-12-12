package com.dicoding.agrovision.data.model

import com.google.gson.annotations.SerializedName

data class HistoryResponse(
	@SerializedName("accuracy")
	val accuracy: String,

	@SerializedName("date")
	val date: String,

	@SerializedName("image_url")
	val imageUrl: String,

	@SerializedName("prediction_id")
	val predictionId: String,

	@SerializedName("result")
	val result: String,

	@SerializedName("user_id")
	val userId: String
)

data class PredictionHistoryResponse(
	val status: String,
	val data: List<HistoryResponse>
)



