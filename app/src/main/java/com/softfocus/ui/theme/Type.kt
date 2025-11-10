package com.softfocus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.softfocus.R

// Font Families
val SourceSansPro = FontFamily(
    Font(R.font.source_sans_pro_light, FontWeight.Light),
    Font(R.font.source_sans_pro_regular, FontWeight.Normal),
    Font(R.font.source_sans_pro_semibold, FontWeight.SemiBold),
    Font(R.font.source_sans_pro_bold, FontWeight.Bold)
)

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium)
)

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal)
)

val RobotoCustom = FontFamily(
    Font(R.font.roboto_medium, FontWeight.Medium)
)

val CrimsonText = FontFamily(
    Font(R.font.crimson_text_regular, FontWeight.Normal),
    Font(R.font.crimson_text_semibold, FontWeight.SemiBold),
    Font(R.font.crimson_text_bold, FontWeight.Bold)
)

val SortsMillGoudy = FontFamily(
    Font(R.font.sorts_mill_goudy_regular, FontWeight.Normal)
)

// Text Styles predefinidos

// Crimson Text Styles
val CrimsonBold = TextStyle(
    fontFamily = CrimsonText,
    fontWeight = FontWeight.Bold,
    lineHeight = 1.2.sp,
    letterSpacing = 0.sp
)

val CrimsonSemiBold = TextStyle(
    fontFamily = CrimsonText,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 1.2.sp,
    letterSpacing = 0.sp
)

val CrimsonRegular = TextStyle(
    fontFamily = CrimsonText,
    fontWeight = FontWeight.Normal,
    lineHeight = 1.2.sp,
    letterSpacing = 0.sp
)
val CrimsonMixed = TextStyle(
    fontFamily = CrimsonText,
    fontWeight = FontWeight.Medium,
    lineHeight = 1.2.sp,
    letterSpacing = 0.sp
)

// Source Sans Pro Styles
val SourceSansLight = TextStyle(
    fontFamily = SourceSansPro,
    fontWeight = FontWeight.Light,
    lineHeight = 1.5.sp,
    letterSpacing = 0.sp
)

val SourceSansRegular = TextStyle(
    fontFamily = SourceSansPro,
    fontWeight = FontWeight.Normal,
    lineHeight = 1.5.sp,
    letterSpacing = 0.15.sp
)

val SourceSansSemiBold = TextStyle(
    fontFamily = SourceSansPro,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 1.5.sp,
    letterSpacing = 0.15.sp
)

val SourceSansBold = TextStyle(
    fontFamily = SourceSansPro,
    fontWeight = FontWeight.Bold,
    lineHeight = 1.5.sp,
    letterSpacing = 0.sp
)

// Inter Styles
val InterRegular = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    lineHeight = 1.4.sp,
    letterSpacing = 0.1.sp
)

val InterMedium = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Medium,
    lineHeight = 1.4.sp,
    letterSpacing = 0.1.sp
)

// Poppins Style
val PoppinsRegular = TextStyle(
    fontFamily = Poppins,
    fontWeight = FontWeight.Normal,
    lineHeight = 1.5.sp,
    letterSpacing = 0.sp
)

// Roboto Style
val RobotoMedium = TextStyle(
    fontFamily = RobotoCustom,
    fontWeight = FontWeight.Medium,
    lineHeight = 1.4.sp,
    letterSpacing = 0.1.sp
)

// Sorts Mill Goudy Style
val SortsMillRegular = TextStyle(
    fontFamily = SortsMillGoudy,
    fontWeight = FontWeight.Normal,
    lineHeight = 1.3.sp,
    letterSpacing = 0.sp
)

// Material 3 Typography (usando estilos por defecto)
val Typography = Typography()
