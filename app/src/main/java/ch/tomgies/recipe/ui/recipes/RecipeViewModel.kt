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

    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState

    init {
        viewModelScope.launch {
            getRecipesUseCase()
                .collect { newRecipeList ->
                    _uiState.emit(RecipeUiState(recipes = newRecipeList, isLoadingMore = false))
                }
        }
        reloadRecipes()
    }

    fun reloadRecipes() {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(isReloading = true)
            reloadRecipesUseCase()
        }
    }

    fun loadMoreRecipes() {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(isLoadingMore = true)
            loadNextRecipesPageUseCase()
            _uiState.value = uiState.value.copy(isLoadingMore = false)
        }
    }
}