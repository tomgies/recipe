package ch.tomgies.recipe.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.tomgies.recipe.domain.usecase.GetRecipesUseCase
import ch.tomgies.recipe.domain.usecase.LoadNextRecipesPageUseCase
import ch.tomgies.recipe.domain.usecase.ReloadRecipesUseCase
import ch.tomgies.recipe.domain.usecase.SearchRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel responsible for managing the [uiState] and actions related to recipes in the app.
 *
 * @property getRecipesUseCase UseCase to observe cached recipes
 * @property loadNextRecipesPageUseCase UseCase for loading the next page of recipes
 * @property reloadRecipesUseCase UseCase for refreshing the recipe list
 * @property searchRecipesUseCase UseCase for searching recipes
 * @property coroutineDispatcher The dispatcher is overridden only by unitTests
 */
@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val loadNextRecipesPageUseCase: LoadNextRecipesPageUseCase,
    private val reloadRecipesUseCase: ReloadRecipesUseCase,
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    /** StateFlow UI state observation*/
    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState

    init {
        // start observation of the cached recipes
        viewModelScope.launch {
            withContext(coroutineDispatcher) {
                getRecipesUseCase()
                    .collect { newRecipeList ->
                        _uiState.emit(uiState.value.copy(recipes = newRecipeList))
                    }
            }
        }
    }

    /**
     * Reloads recipes and sets the loading state while executing.
     * Any errors are stored in the UI state.
     */
    fun reloadRecipes() {
        viewModelScope.launch {
            withContext(coroutineDispatcher) {
                _uiState.value = uiState.value.copy(isLoading = true)
                val result = reloadRecipesUseCase()
                _uiState.value = uiState.value.copy(isLoading = false, error = result.exceptionOrNull())
            }
        }
    }

    /**
     * Loads the next page of recipes.
     * Loading Flag is [isLoadingMore]
     */
    fun loadMoreRecipes() {
        viewModelScope.launch {
            withContext(coroutineDispatcher) {
                _uiState.value = uiState.value.copy(isLoadingMore = true)
                val result = loadNextRecipesPageUseCase()
                _uiState.value = uiState.value.copy(isLoadingMore = false, error = result.exceptionOrNull())
            }
        }
    }

    /**
     * Performs a recipe search based on the provided query.
     * Loading Flag is [isLoading]
     *
     * @param query Search term
     */
    fun searchRecipes(query: String) {
        viewModelScope.launch {
            withContext(coroutineDispatcher) {
                _uiState.value = uiState.value.copy(isLoading = true)
                val result = searchRecipesUseCase(query)
                _uiState.value = uiState.value.copy(isLoading = false, error = result.exceptionOrNull())
            }
        }
    }

    /**
     * Refreshes the recipe list either by reloading all recipes or performing a search,
     * depending on whether the user entered a search term.
     * Loading Flag is [isRefreshing]
     *
     * @param query Optional search term
     */
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

    /**
     * Resets the error state in the UI.
     */
    fun resetError() {
        _uiState.value = uiState.value.copy(error = null)
    }
}