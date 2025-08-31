package ch.tomgies.recipe.domain.usecase

import ch.tomgies.recipe.domain.entity.Recipe
import ch.tomgies.recipe.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetRecipesUseCase {
    operator fun invoke(): Flow<List<Recipe>>
}

class GetRecipesUseCaseImpl @Inject constructor(
    val recipeRepo: RecipeRepository
) : GetRecipesUseCase {
    override fun invoke(): Flow<List<Recipe>> = recipeRepo.recipes
}