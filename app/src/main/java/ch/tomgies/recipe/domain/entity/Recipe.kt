package ch.tomgies.recipe.domain.entity

data class Recipe(
    val id: Int,
    val name: String,
    val rating: Double,
    val difficulty: String,
    val tags: List<String>,
)
