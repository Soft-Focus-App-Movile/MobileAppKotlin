package com.softfocus.features.library.presentation.general.browse.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softfocus.features.library.domain.models.ContentType
import com.softfocus.features.library.presentation.shared.getDisplayName
import com.softfocus.ui.theme.Green65
import com.softfocus.ui.theme.SourceSansRegular

/**
 * Componente de tabs de la biblioteca
 * Muestra tabs de "Contenido/Asignaciones" para pacientes
 * Y tabs de tipos de contenido (Movie, Music, Video, Place)
 *
 * @param isPatient Si el usuario es paciente
 * @param currentTab Tab actual ("content" o "assignments")
 * @param onTabChange Callback cuando cambia el tab principal
 * @param selectedType Tipo de contenido seleccionado
 * @param availableTabs Lista de tipos de contenido disponibles
 * @param onContentTypeSelected Callback cuando se selecciona un tipo
 */
@Composable
fun LibraryTabs(
    isPatient: Boolean,
    currentTab: String,
    onTabChange: (String) -> Unit,
    selectedType: ContentType,
    availableTabs: List<ContentType>,
    onContentTypeSelected: (ContentType) -> Unit
) {
    Column {
        // Tabs principales para pacientes: Contenido | Asignaciones
        if (isPatient) {
            val tabs = listOf("Contenido", "Asignaciones")
            val selectedTabIndex = if (currentTab == "content") 0 else 1

            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = Green65,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Green65
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, tabName ->
                    val isSelected = selectedTabIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = { onTabChange(if (index == 0) "content" else "assignments") },
                        text = {
                            Text(
                                text = tabName,
                                style = SourceSansRegular.copy(fontSize = 15.sp),
                                color = if (isSelected) Green65 else Color.White
                            )
                        }
                    )
                }
            }
        }

        // Tabs de tipos de contenido (solo si está en "content" o no es paciente)
        if (currentTab == "content" || !isPatient) {
            val contentTabIndex = availableTabs.indexOf(selectedType).takeIf { it >= 0 } ?: 0
            ScrollableTabRow(
                selectedTabIndex = contentTabIndex,
                containerColor = Color.Transparent,
                contentColor = Green65,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    if (tabPositions.isNotEmpty()) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[contentTabIndex]),
                            color = Green65
                        )
                    }
                },
                divider = {}
            ) {
                availableTabs.forEach { type ->
                    val isSelected = selectedType == type
                    Tab(
                        selected = isSelected,
                        onClick = { onContentTypeSelected(type) },
                        text = {
                            Text(
                                text = type.getDisplayName(),
                                style = SourceSansRegular.copy(fontSize = 15.sp),
                                color = if (isSelected) Green65 else Color.White
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
