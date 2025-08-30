package ch.tomgies.recipe.ui.recipes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.tomgies.recipe.domain.entity.Recipe
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun RecipesScreen(viewModel: RecipeViewModel = hiltViewModel()) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    RecipesScreen(
        uiState = uiState.value,
        onRefresh = viewModel::refresh,
        onReload = viewModel::reloadRecipes,
        loadMoreRecipes = viewModel::loadMoreRecipes,
        searchRecipes = viewModel::searchRecipes
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipesScreen(
    uiState: RecipeUiState,
    onRefresh: (String) -> Unit = {},
    onReload: () -> Unit = {},
    loadMoreRecipes: () -> Unit = {},
    searchRecipes: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { onRefresh(searchQuery) },
        modifier = Modifier
    ) {

        ListWithPagination(
            uiState = uiState,
            loadMoreRecipes = loadMoreRecipes
        )

        RecipeSearchBar(
            query = searchQuery,
            onQueryChanged = { searchQuery = it },
            onSearch = searchRecipes,
            onClearSearch = {
                searchQuery = ""
                onReload()
            }
        )
    }
}

@Composable
fun ListWithPagination(uiState: RecipeUiState, loadMoreRecipes: () -> Unit) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(Modifier.height(120.dp))
        }
        if (uiState.recipes.isEmpty() && !uiState.isLoading) {
            item {
                Text("No Recipes available")
            }
        }
        items(
            items = uiState.recipes,
            key = { it.id }
        ) { recipe ->
            ListItem(recipe = recipe, onClick = {})
        }

        if (uiState.isLoadingMore || uiState.isLoading) {
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

    // trigger load more data when reach current bottom of list
    LaunchedEffect(listState, uiState.recipes, uiState.isLoadingMore) {
        snapshotFlow {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val viewportHeight = listState.layoutInfo.viewportEndOffset
            lastVisibleIndex to viewportHeight
        }.collect { (lastVisibleIndex, viewportHeight) ->
            val listIsScrollable = listState.layoutInfo.totalItemsCount > 0 && listState.layoutInfo.visibleItemsInfo.lastOrNull()?.let { it.offset + it.size > viewportHeight } == true

            if (!uiState.isLoadingMore
                && lastVisibleIndex != null
                && lastVisibleIndex >= uiState.recipes.size - 1
                && listIsScrollable
            ) {
                loadMoreRecipes()
            }
        }
    }
}

@Composable
fun ListItem(recipe: Recipe, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(50.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(recipe.image)
                .crossfade(true)
                .build(),
            contentDescription = recipe.title,
        )
        Text(recipe.title)
    }
    HorizontalDivider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChanged,
                    onSearch = {
                        if (query.isNotEmpty()) {
                            onSearch(query)
                        }
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Search Recipes") },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = onClearSearch) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    }
                )
            },
            expanded = false,
            onExpandedChange = { },
        ) {}
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