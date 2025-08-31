package ch.tomgies.recipe.ui.theme


import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import ch.tomgies.recipe.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)
val AmaticSC = GoogleFont("Amatic SC")

val AmaticSCFontFamily = FontFamily(
    Font(googleFont = AmaticSC, fontProvider = provider)
)

val Typography = Typography(
    titleLarge = Typography().titleLarge.copy(
        fontFamily = AmaticSCFontFamily,
        color = Yellow,
        fontWeight = FontWeight.W500
    ),
    displayMedium = Typography().displayMedium.copy(
        fontFamily = AmaticSCFontFamily,
        color = Yellow,
        fontWeight = FontWeight.W500
    ),
    headlineLarge = Typography().headlineLarge.copy(
        fontFamily = AmaticSCFontFamily,
        color = Yellow,
        fontWeight = FontWeight.W500
    )
)