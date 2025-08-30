package ch.tomgies.recipe.ui.recipes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RecipesScreen(viewModel: RecipeViewModel = hiltViewModel()) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    RecipesScreen(
        uiState = uiState.value,
        loadNextPage = viewModel::loadNextPage
    )
}

@Composable
private fun RecipesScreen(uiState: RecipeUiState, loadNextPage: () -> Unit) {
    when (uiState) {
        RecipeUiState.LoadingState -> LoadingStateDisplay()
        is RecipeUiState.DataState -> DataStateDisplay(recipes = uiState.recipes, isLoadingPage = uiState.isLoadingPage, loadNextPage = loadNextPage)
        is RecipeUiState.ErrorState -> ErrorStateDisplay(error = uiState.error, recipes = uiState.recipes)
    }
}

@Composable
private fun LoadingStateDisplay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DataStateDisplay(recipes: List<String>, isLoadingPage: Boolean, loadNextPage: () -> Unit) {
    val listState = rememberLazyListState()

    val shouldLoadNextPage by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index == recipes.lastIndex
        }
    }

    LaunchedEffect(shouldLoadNextPage) {
        if (shouldLoadNextPage) {
            loadNextPage()
        }
    }

    LazyColumn(state = listState) {
        items(recipes) { recipe ->
            Text(text = recipe)
            HorizontalDivider()
        }
        if (isLoadingPage) {
            item {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun ErrorStateDisplay(error: Throwable, recipes: List<String>) {

}

class RecipeUiStateParamProvider : PreviewParameterProvider<RecipeUiState> {
    override val values = sequenceOf(
        RecipeUiState.LoadingState
    )
}

@Composable
@Preview
private fun RecipesScreenPreview(@PreviewParameter(RecipeUiStateParamProvider::class) uiState: RecipeUiState) {
    RecipesScreen(
        uiState = uiState,
        loadNextPage = {}
    )
}