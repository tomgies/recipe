package ch.tomgies.recipe.domain.entity

import com.squareup.moshi.Json

data class Recipe(
    val id: Int,
    @Json(name = "name")
    val title: String,
    val rating: Double,
    val difficulty: String,
    val tags: List<String>,
    val image: String
)
