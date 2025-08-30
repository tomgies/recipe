package ch.tomgies.recipe.common.di

import ch.tomgies.recipe.data.repository.RecipeRepositoryImpl
import ch.tomgies.recipe.domain.repository.RecipeRepository
import ch.tomgies.recipe.domain.usecase.GetRecipesUseCase
import ch.tomgies.recipe.domain.usecase.GetRecipesUseCaseImpl
import ch.tomgies.recipe.domain.usecase.LoadNextRecipesPageUseCase
import ch.tomgies.recipe.domain.usecase.LoadNextRecipesPageUseCaseImpl
import ch.tomgies.recipe.domain.usecase.ReloadRecipesUseCase
import ch.tomgies.recipe.domain.usecase.ReloadRecipesUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Provides
    fun provideGetRecipesUseCase(impl: GetRecipesUseCaseImpl): GetRecipesUseCase = impl

    @Provides
    fun provideLoadNextRecipesPageUseCase(impl: LoadNextRecipesPageUseCaseImpl): LoadNextRecipesPageUseCase = impl

    @Provides
    fun provideReloadRecipesUseCase(impl: ReloadRecipesUseCaseImpl): ReloadRecipesUseCase = impl

    @Provides
    @Singleton
    fun provideRecipeRepository(): RecipeRepository = RecipeRepositoryImpl()
}