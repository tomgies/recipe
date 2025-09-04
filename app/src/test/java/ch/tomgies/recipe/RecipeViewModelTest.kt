package ch.tomgies.recipe

import app.cash.turbine.test
import ch.tomgies.recipe.domain.entity.Recipe
import ch.tomgies.recipe.domain.usecase.GetRecipesUseCase
import ch.tomgies.recipe.domain.usecase.LoadNextRecipesPageUseCase
import ch.tomgies.recipe.domain.usecase.ReloadRecipesUseCase
import ch.tomgies.recipe.domain.usecase.SearchRecipesUseCase
import ch.tomgies.recipe.ui.recipes.RecipeUiState
import ch.tomgies.recipe.ui.recipes.RecipeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class RecipeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val recipesFlow = MutableStateFlow<List<Recipe>>(emptyList())
    private lateinit var searchRecipesUseCase: SearchRecipesUseCase
    private lateinit var getRecipesUseCase: GetRecipesUseCase
    private lateinit var sut: RecipeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        searchRecipesUseCase = mockk<SearchRecipesUseCase>(relaxed = true)
        getRecipesUseCase = mockk<GetRecipesUseCase>(relaxed = true)
        val mockLoadNext = mockk<LoadNextRecipesPageUseCase>(relaxed = true)
        val mockReload = mockk<ReloadRecipesUseCase>(relaxed = true)

        sut = RecipeViewModel(
            getRecipesUseCase = getRecipesUseCase,
            loadNextRecipesPageUseCase = mockLoadNext,
            reloadRecipesUseCase = mockReload,
            searchRecipesUseCase = searchRecipesUseCase,
        )

        coEvery { getRecipesUseCase() } returns recipesFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given search query updates uiState correctly`() = runTest {
        coEvery { searchRecipesUseCase("pizza") } coAnswers {
            recipesFlow.emit(RecipeMockData.pizzaSearchResult)
            Result.success(Unit)
        }

        val expectedResult = RecipeUiState(recipes = RecipeMockData.pizzaSearchResult)

        sut.uiState.test {
            // await initial empty state
            awaitItem()

            sut.searchRecipes("pizza")
            testDispatcher.scheduler.advanceUntilIdle()

            // start loading
            val actualLoadingState = awaitItem()
            assertEquals(true, actualLoadingState.isLoading)
            assertEquals(emptyList<Recipe>(), actualLoadingState.recipes)

            // stop loading
            val actualStopLoadingResult = awaitItem()
            assertEquals(false, actualStopLoadingResult.isLoading)
            assertEquals(emptyList<Recipe>(), actualStopLoadingResult.recipes)

            // search result state
            val actualSearchResult = awaitItem()
            assertEquals(expectedResult, actualSearchResult)
            assertEquals(false, actualSearchResult.isLoading)
            assertEquals(null, actualLoadingState.error)
        }
    }
}