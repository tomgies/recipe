package ch.tomgies.recipe

import app.cash.turbine.test
import ch.tomgies.recipe.data.api.RecipesApi
import ch.tomgies.recipe.data.api.RecipesDTO
import ch.tomgies.recipe.data.repository.RecipeRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeRepositoryImplTest {

    private lateinit var api: RecipesApi
    private lateinit var sut: RecipeRepositoryImpl

    @Before
    fun setUp() {
        api = mockk()
        sut = RecipeRepositoryImpl(api)
    }

    @Test
    fun `search for recipes through API and emit result`() = runTest {
        val searchQuery = "Pizza"
        val expectedSearchResults = RecipeMockData.pizzaSearchResult

        coEvery { api.searchRecipes(searchQuery) } returns Response.success(
            RecipesDTO(
                recipes = RecipeMockData.pizzaSearchResult,
                total = 2,
                skip = 5,
                limit = 5
            )
        )

        sut.recipes.test {
            sut.search(searchQuery)
            // await initial emission
            awaitItem()
            // await emission of search
            val actualSearchResult = awaitItem()

            assertEquals(expectedSearchResults, actualSearchResult)
            cancelAndIgnoreRemainingEvents()
        }

        // verify whether the repo actually called the api or not
        coVerify { api.searchRecipes(searchQuery) }
    }
}