package ch.tomgies.recipe.ui.recipes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.tomgies.recipe.domain.entity.Recipe

@Composable
fun RecipesScreen(viewModel: RecipeViewModel = hiltViewModel()) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    RecipesScreen(
        uiState = uiState.value,
        loadMoreRecipes = viewModel::loadMoreRecipes
    )
}

@Composable
private fun RecipesScreen(uiState: RecipeUiState, loadMoreRecipes: () -> Unit = {}) {
    when (uiState) {
        RecipeUiState.LoadingState -> LoadingStateDisplay()
        is RecipeUiState.DataState -> DataStateDisplay(recipes = uiState.recipes, isLoadingPage = uiState.isLoadingPage, loadMoreRecipes = loadMoreRecipes)
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
private fun DataStateDisplay(recipes: List<Recipe>, isLoadingPage: Boolean, loadMoreRecipes: () -> Unit) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        items(
            items = recipes,
            key = { it.id }
        ) { recipe ->
            Text(
                modifier = Modifier.padding(vertical = 20.dp),
                text = recipe.name
            )
            HorizontalDivider()
        }

        if (isLoadingPage) {
            item {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
    }

    LaunchedEffect(listState, recipes, isLoadingPage) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (!isLoadingPage && lastVisibleIndex != null && lastVisibleIndex >= recipes.size - 3) {
                    loadMoreRecipes()
                }
            }
    }
}

@Composable
private fun ErrorStateDisplay(error: Throwable, recipes: List<Recipe>) {

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
        uiState = uiState
    )
}