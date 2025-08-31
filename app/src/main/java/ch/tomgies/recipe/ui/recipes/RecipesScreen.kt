package ch.tomgies.recipe.ui.recipes

import RecipeDetailBottomSheet
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.tomgies.recipe.R
import ch.tomgies.recipe.domain.entity.Recipe
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun RecipesScreen(viewModel: RecipeViewModel = hiltViewModel()) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.reloadRecipes()
    }

    RecipesScreen(
        uiState = uiState.value,
        onRefresh = viewModel::refresh,
        onReload = viewModel::reloadRecipes,
        loadMoreRecipes = viewModel::loadMoreRecipes,
        searchRecipes = viewModel::searchRecipes,
        resetError = viewModel::resetError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipesScreen(
    uiState: RecipeUiState,
    onRefresh: (String) -> Unit = {},
    onReload: () -> Unit = {},
    loadMoreRecipes: () -> Unit = {},
    searchRecipes: (String) -> Unit = {},
    resetError: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var showRecipeDetail by remember { mutableStateOf<Recipe?>(null) }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { onRefresh(searchQuery) },
        modifier = Modifier
    ) {

        PaginatedList(
            uiState = uiState,
            reachedLastListItem = { if (searchQuery.isEmpty()) loadMoreRecipes() },
            showRecipeDetail = { showRecipeDetail = it }
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

    showRecipeDetail?.let { recipe ->
        RecipeDetailBottomSheet(recipe = recipe, onDismiss = { showRecipeDetail = null })
    }

    ErrorDialog(uiState.error, resetError)
}

@Composable
fun PaginatedList(uiState: RecipeUiState, reachedLastListItem: () -> Unit, showRecipeDetail: (Recipe) -> Unit) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(Modifier.height(120.dp))
        }
        if (uiState.recipes.isEmpty() && !uiState.isLoading) {
            item {
                Text(
                    text = stringResource(R.string.recipes_not_available),
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
        items(
            items = uiState.recipes,
            key = { it.id }
        ) { recipe ->
            ListItem(recipe = recipe, showRecipeDetail = showRecipeDetail)
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
                reachedLastListItem()
            }
        }
    }
}

@Composable
fun ListItem(recipe: Recipe, showRecipeDetail: (Recipe) -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { showRecipeDetail(recipe) }
        ) {
            AsyncImage(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(recipe.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = recipe.title,
                contentScale = ContentScale.FillHeight
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(3f)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.width(20.dp),
                        imageVector = Icons.Default.StarRate,
                        contentDescription = Icons.Default.StarRate.name,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = recipe.rating.toString(),
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.width(20.dp),
                        imageVector = Icons.Default.Speed,
                        contentDescription = Icons.Default.Speed.name,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = recipe.difficulty)
                }
                Spacer(Modifier.weight(1f))
                Text(text = recipe.tags.joinToString())
            }
        }
    }
    Spacer(Modifier.height(16.dp))
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
                    placeholder = { Text(stringResource(R.string.recipes_search_hint)) },
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

@Composable
fun ErrorDialog(error: Throwable?, onDismiss: () -> Unit) {
    error?.let {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(R.string.error_dialog_title),
                    style = MaterialTheme.typography.headlineLarge
                )
            },
            text = {
                Text(text = error.message ?: stringResource(R.string.error_dialog_message_fallback))
            },
            confirmButton = {
                Button(
                    onClick = onDismiss
                ) {
                    Text(text = stringResource(R.string.global_ok))
                }
            }
        )
    }
}

class RecipeUiStateParamProvider : PreviewParameterProvider<RecipeUiState> {
    companion object {
        val PIZZA_ALFREDO = Recipe(id = 1, title = "Pizza Alfredo", rating = 10.0, difficulty = "Easy", tags = listOf("Pizza", "Mhhhh"), imageUrl = "", ingredients = listOf("Dough", "Mozzarella"), instructions = listOf("Step One", "Step Two"), prepTimeMinutes = 15)
        val PIZZA_MARGHERITA = Recipe(id = 2, title = "Pizza Margherita", rating = 10.0, difficulty = "Easy", tags = listOf("Pizza", "Mhhhh"), imageUrl = "", ingredients = listOf("Dough", "Mozzarella"), instructions = listOf("Step One", "Step Two"), prepTimeMinutes = 15)
    }

    override val values = sequenceOf(
        RecipeUiState(),
        RecipeUiState(listOf(PIZZA_ALFREDO)),
        RecipeUiState(
            recipes = listOf(PIZZA_ALFREDO, PIZZA_MARGHERITA),
            isLoading = true
        ),
        RecipeUiState(
            recipes = listOf(PIZZA_ALFREDO, PIZZA_MARGHERITA),
        ),
    )
}

@Composable
@Preview
private fun RecipesScreenPreview(@PreviewParameter(RecipeUiStateParamProvider::class) uiState: RecipeUiState) {
    RecipesScreen(
        uiState = uiState
    )
}