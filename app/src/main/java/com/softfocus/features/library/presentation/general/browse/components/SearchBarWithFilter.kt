package com.softfocus.features.library.presentation.general.browse.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.softfocus.R
import com.softfocus.ui.theme.Green29
import com.softfocus.ui.theme.Green49
import com.softfocus.ui.theme.SourceSansRegular

/**
 * Barra de búsqueda con botón de filtro
 *
 * @param searchQuery Query de búsqueda actual
 * @param onSearchQueryChange Callback cuando cambia el query
 * @param onFilterClick Callback al hacer clic en el botón de filtro
 * @param modifier Modificador opcional
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarWithFilter(
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onFilterClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 19.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Campo de búsqueda
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            placeholder = {
                Text(
                    text = "Buscar...",
                    style = SourceSansRegular,
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "Buscar",
                    tint = Color.Gray
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            singleLine = true
        )

        // Botón de filtro
        IconButton(
            onClick = onFilterClick,
            modifier = Modifier
                .size(50.dp)
                .background(
                    color = Green49,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_filter),
                contentDescription = "Filtrar",
                tint = Color.White,
                modifier=Modifier.size(15.dp)
            )
        }
    }
}
