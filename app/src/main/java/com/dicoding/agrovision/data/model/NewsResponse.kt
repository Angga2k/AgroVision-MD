package com.dicoding.agrovision.data.model

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)

data class Article(
    val title: String,
    val description: String,
    val url: String,
    val publishedAt: String
)


data class Source(
    val id: String?,
    val name: String
)
