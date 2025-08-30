package ch.tomgies.recipe.data.api

import ch.tomgies.recipe.domain.entity.Recipe

data class RecipesDTO(
    val recipes: List<Recipe>,
    val total: Int,
    val skip: Int,
    val limit: Int
)