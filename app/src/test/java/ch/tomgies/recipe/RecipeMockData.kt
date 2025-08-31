package ch.tomgies.recipe

import ch.tomgies.recipe.domain.entity.Recipe

class RecipeMockData {
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