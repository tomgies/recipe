package ch.tomgies.recipe.ui.recipes

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeUiState.LoadingState)
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    fun loadNextPage() {

    }
}