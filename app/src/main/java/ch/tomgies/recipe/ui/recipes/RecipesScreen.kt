package ch.tomgies.recipe.ui.recipes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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

@Composable
fun RecipesScreen(viewModel: RecipeViewModel = hiltViewModel()) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    RecipesScreen(
        uiState = uiState.value,
        reloadRecipes = viewModel::reloadRecipes,
        loadMoreRecipes = viewModel::loadMoreRecipes
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipesScreen(uiState: RecipeUiState, reloadRecipes: () -> Unit = {}, loadMoreRecipes: () -> Unit = {}) {
    val listState = rememberLazyListState()

    PullToRefreshBox(
        isRefreshing = uiState.isReloading,
        onRefresh = reloadRecipes,
        modifier = Modifier
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            items(
                items = uiState.recipes,
                key = { it.id }
            ) { recipe ->
                Text(
                    modifier = Modifier.padding(vertical = 20.dp),
                    text = recipe.name
                )
                HorizontalDivider()
            }

            if (uiState.isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }

        LaunchedEffect(listState, uiState.recipes, uiState.isLoadingMore) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collect { lastVisibleIndex ->
                    if (!uiState.isLoadingMore && lastVisibleIndex != null && lastVisibleIndex >= uiState.recipes.size - 3) {
                        loadMoreRecipes()
                    }
                }
        }
    }
}

class RecipeUiStateParamProvider : PreviewParameterProvider<RecipeUiState> {
    override val values = sequenceOf(
        RecipeUiState()
    )
}

@Composable
@Preview
private fun RecipesScreenPreview(@PreviewParameter(RecipeUiStateParamProvider::class) uiState: RecipeUiState) {
    RecipesScreen(
        uiState = uiState
    )
}