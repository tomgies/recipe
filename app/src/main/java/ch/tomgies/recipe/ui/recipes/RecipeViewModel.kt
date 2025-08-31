package ch.tomgies.recipe.ui.recipes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.tomgies.recipe.domain.usecase.GetRecipesUseCase
import ch.tomgies.recipe.domain.usecase.LoadNextRecipesPageUseCase
import ch.tomgies.recipe.domain.usecase.ReloadRecipesUseCase
import ch.tomgies.recipe.domain.usecase.SearchRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val loadNextRecipesPageUseCase: LoadNextRecipesPageUseCase,
    private val reloadRecipesUseCase: ReloadRecipesUseCase,
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState

    init {
        viewModelScope.launch {
            withContext(coroutineDispatcher) {
                getRecipesUseCase()
                    .collect { newRecipeList ->
                        _uiState.emit(uiState.value.copy(recipes = newRecipeList))
                    }
            }
        }
    }

    fun reloadRecipes() {
        viewModelScope.launch {
            withContext(coroutineDispatcher) {
                _uiState.value = uiState.value.copy(isLoading = true)
                val result = reloadRecipesUseCase()
                _uiState.value = uiState.value.copy(isLoading = false, error = result.exceptionOrNull())
            }
        }
    }

    fun loadMoreRecipes() {
        viewModelScope.launch {
            withContext(coroutineDispatcher) {
                _uiState.value = uiState.value.copy(isLoadingMore = true)
                val result = loadNextRecipesPageUseCase()
                _uiState.value = uiState.value.copy(isLoadingMore = false, error = result.exceptionOrNull())
            }
        }
    }

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            withContext(coroutineDispatcher) {
                _uiState.value = uiState.value.copy(isLoading = true)
                val result = searchRecipesUseCase(query)
                _uiState.value = uiState.value.copy(isLoading = false, error = result.exceptionOrNull())
            }
        }
    }

    fun refresh(query: String) {
        viewModelScope.launch {
            withContext(coroutineDispatcher) {
                if (query.isEmpty()) {
                    _uiState.value = uiState.value.copy(isRefreshing = true)
                    val result = reloadRecipesUseCase()
                    _uiState.value = uiState.value.copy(isRefreshing = false, error = result.exceptionOrNull())
                } else {
                    _uiState.value = uiState.value.copy(isRefreshing = true)
                    val result = searchRecipesUseCase(query)
                    _uiState.value = uiState.value.copy(isRefreshing = false, error = result.exceptionOrNull())
                }
            }
        }
    }

    fun resetError() {
        _uiState.value = uiState.value.copy(error = null)
    }
}