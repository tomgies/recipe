package ch.tomgies.recipe.ui.recipes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.tomgies.recipe.domain.usecase.GetRecipesUseCase
import ch.tomgies.recipe.domain.usecase.LoadNextRecipesPageUseCase
import ch.tomgies.recipe.domain.usecase.ReloadRecipesUseCase
import ch.tomgies.recipe.domain.usecase.SearchRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val loadNextRecipesPageUseCase: LoadNextRecipesPageUseCase,
    private val reloadRecipesUseCase: ReloadRecipesUseCase,
    private val searchRecipesUseCase: SearchRecipesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState

    init {
        viewModelScope.launch {
            getRecipesUseCase()
                .collect { newRecipeList ->
                    Log.d("MyLog", "new Recipes ${newRecipeList.size}")
                    _uiState.emit(uiState.value.copy(recipes = newRecipeList))
                }
        }
        reloadRecipes()
    }

    fun reloadRecipes() {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(isLoading = true)
            reloadRecipesUseCase()
            _uiState.value = uiState.value.copy(isLoading = false)
        }
    }

    fun loadMoreRecipes() {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(isLoadingMore = true)
            loadNextRecipesPageUseCase()
            _uiState.value = uiState.value.copy(isLoadingMore = false)
        }
    }

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(isLoading = true)
            val result = searchRecipesUseCase(query)
            _uiState.value = uiState.value.copy(isLoading = false)
            // TODO Error Handling
        }
    }

    fun refresh(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                _uiState.value = uiState.value.copy(isRefreshing = true)
                delay(500)
                reloadRecipesUseCase()
                _uiState.value = uiState.value.copy(isRefreshing = false)
            } else {
                _uiState.value = uiState.value.copy(isRefreshing = true)
                delay(500)
                val result = searchRecipesUseCase(query)
                _uiState.value = uiState.value.copy(isRefreshing = false)
            }
        }
    }
}