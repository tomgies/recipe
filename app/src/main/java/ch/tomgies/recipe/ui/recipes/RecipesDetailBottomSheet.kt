import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ch.tomgies.recipe.R
import ch.tomgies.recipe.domain.entity.Recipe
import ch.tomgies.recipe.ui.theme.Yellow
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailBottomSheet(recipe: Recipe, onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(recipe.imageUrl)
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.FillWidth,
                contentDescription = recipe.title,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                text = recipe.title,
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(Modifier.height(16.dp))

            Row {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = null,
                )
                Spacer(Modifier.width(4.dp))
                Text(text = stringResource(R.string.recipes_detail_prep_time, recipe.prepTimeMinutes))
            }
            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.recipes_detail_ingredients),
                style = MaterialTheme.typography.headlineLarge
            )
            recipe.ingredients.forEach {
                Row {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Yellow,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(text = it)
                }
            }
            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.recipes_detail_instructions_title),
                style = MaterialTheme.typography.headlineLarge
            )
            recipe.instructions.forEachIndexed { index, instruction ->
                Row {
                    Text(
                        text = stringResource(R.string.recipes_detail_instruction_step, index + 1)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(text = instruction)
                }
            }
        }
    }
}