    package ch.tomgies.recipe

    import app.cash.turbine.test
    import ch.tomgies.recipe.data.api.RecipesApi
    import ch.tomgies.recipe.data.api.RecipesDTO
    import ch.tomgies.recipe.data.repository.RecipeRepositoryImpl
    import ch.tomgies.recipe.domain.entity.Recipe
    import io.mockk.coEvery
    import io.mockk.coVerify
    import io.mockk.mockk
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.ExperimentalCoroutinesApi
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.test.UnconfinedTestDispatcher
    import kotlinx.coroutines.test.resetMain
    import kotlinx.coroutines.test.runTest
    import kotlinx.coroutines.test.setMain
    import org.junit.After
    import org.junit.Assert.assertEquals
    import org.junit.Before
    import org.junit.Test
    import retrofit2.Response

    @OptIn(ExperimentalCoroutinesApi::class)
    class RecipeRepositoryTest {

        private val testDispatcher = UnconfinedTestDispatcher()
        private lateinit var api: RecipesApi
        private lateinit var sut: RecipeRepositoryImpl

        @Before
        fun setUp() {
            Dispatchers.setMain(testDispatcher)
            api = mockk()
            sut = RecipeRepositoryImpl(api)
        }

        @After
        fun tearDown() {
            Dispatchers.resetMain()
        }

        @Test
        fun `search emits recipes from API`() = runTest {
            val searchQuery = "Pizza"
            val expectedSearchResults = RecipesResponseMock.pizzaSearchResult

            coEvery { api.searchRecipes(searchQuery) } returns Response.success(RecipesDTO(
                recipes = RecipesResponseMock.pizzaSearchResult,
                total = 2,
                skip = 5,
                limit = 5
            ))

            sut.recipes.test {
                sut.search(searchQuery)
                // await initial emission
                awaitItem()
                // await emission of search
                val actualSearchResult = awaitItem()
                assertEquals(expectedSearchResults, actualSearchResult)
                cancelAndIgnoreRemainingEvents()
            }

            // verify weter the repo actually called
            coVerify { api.searchRecipes(searchQuery) }
        }
    }

    class RecipesResponseMock {
        companion object {
            val pizzaSearchResult = listOf(
                Recipe(
                    id = 1,
                    title = "Classic Margherita Pizza",
                    rating = 4.6,
                    difficulty = "Easy",
                    tags = listOf("Pizza", "Italian"),
                    imageUrl = "https://cdn.dummyjson.com/recipe-images/1.webp",
                    ingredients = listOf(
                        "Pizza dough",
                        "Tomato sauce",
                        "Fresh mozzarella cheese",
                        "Fresh basil leaves",
                        "Olive oil",
                        "Salt and pepper to taste"
                    ),
                    instructions = listOf(
                        "Preheat the oven to 475°F (245°C).",
                        "Roll out the pizza dough and spread tomato sauce evenly.",
                        "Top with slices of fresh mozzarella and fresh basil leaves.",
                        "Drizzle with olive oil and season with salt and pepper.",
                        "Bake in the preheated oven for 12-15 minutes or until the crust is golden brown.",
                        "Slice and serve hot."
                    ),
                    prepTimeMinutes = 20
                ),
                Recipe(
                    id = 45,
                    title = "Italian Margherita Pizza",
                    rating = 4.7,
                    difficulty = "Easy",
                    tags = listOf("Margherita pizza", "Italian", "Quick"),
                    imageUrl = "https://cdn.dummyjson.com/recipe-images/45.webp",
                    ingredients = listOf(
                        "Pizza dough",
                        "Tomatoes, thinly sliced",
                        "Fresh mozzarella, sliced",
                        "Fresh basil leaves",
                        "Olive oil",
                        "Garlic, minced",
                        "Salt and pepper to taste"
                    ),
                    instructions = listOf(
                        "Preheat the oven to the highest temperature your oven can go.",
                        "Roll out pizza dough on a floured surface and transfer to a pizza stone or baking sheet.",
                        "Brush the dough with olive oil and sprinkle minced garlic over the surface.",
                        "Arrange thinly sliced tomatoes and fresh mozzarella slices on the dough.",
                        "Bake until the crust is golden and the cheese is melted and bubbly.",
                        "Top with fresh basil leaves and season with salt and pepper. Slice and serve this classic Margherita Pizza."
                    ),
                    prepTimeMinutes = 20
                )
            )
        }
    }