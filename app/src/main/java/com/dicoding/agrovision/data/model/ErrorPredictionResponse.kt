package com.dicoding.agrovision.data.model

import com.google.gson.annotations.SerializedName

data class ErrorPredictionResponse(

	@field:SerializedName("error")
	val error: String? = null
)
