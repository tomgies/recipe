package ch.tomgies.recipe.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.tomgies.recipe.domain.usecase.GetRecipesUseCase
import ch.tomgies.recipe.domain.usecase.LoadNextRecipesPageUseCase
import ch.tomgies.recipe.domain.usecase.ReloadRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val loadNextRecipesPageUseCase: LoadNextRecipesPageUseCase,
    private val reloadRecipesUseCase: ReloadRecipesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecipeUiState>(RecipeUiState.LoadingState)
    val uiState: StateFlow<RecipeUiState> = _uiState

    init {
        viewModelScope.launch {
            getRecipesUseCase()
                .collect { newRecipeList ->
                    _uiState.emit(RecipeUiState.DataState(recipes = newRecipeList, isLoadingPage = false))
                }
        }
        reloadRecipes()
    }

    fun reloadRecipes() {
        viewModelScope.launch {
            reloadRecipesUseCase()
        }
    }

    fun loadMoreRecipes() {
        viewModelScope.launch {
            when(val state = _uiState.value) {
                is RecipeUiState.DataState -> _uiState.value = state.copy(isLoadingPage = true)
                else -> _uiState.value = RecipeUiState.DataState(recipes = state.recipes, isLoadingPage = true)
            }
            loadNextRecipesPageUseCase()
        }
    }
}