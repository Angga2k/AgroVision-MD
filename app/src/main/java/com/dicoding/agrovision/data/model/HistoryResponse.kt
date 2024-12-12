package com.dicoding.agrovision.data.model

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("result")
	val result: String? = null,

	@field:SerializedName("prediction_id")
	val predictionId: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null
)
