package com.dicoding.agrovision.data.model

import com.google.gson.annotations.SerializedName

data class PredictionResponse(
	@SerializedName("accuracy")
	val accuracy: Float? = null,

	@SerializedName("class")
	val resultClass: String? = null
)

