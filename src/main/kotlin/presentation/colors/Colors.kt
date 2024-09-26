import androidx.compose.ui.graphics.Color

object AppColors {
    val primaryColor = Color(0xFF012060)
    val secondaryColor = Color(0xFF244A93)
    val tertiaryColor = primaryColor.copy(alpha = 0.3f)
    val backgroundColor = tertiaryColor.copy(alpha = 0.2f)
}