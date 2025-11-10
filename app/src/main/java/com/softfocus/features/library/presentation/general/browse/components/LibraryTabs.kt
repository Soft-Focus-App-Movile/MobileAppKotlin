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
 * Para PATIENT: Muestra todas las tabs de contenido (Movies, Music, Videos, Weather) + "Asignados"
 * Para PSYCHOLOGIST: Muestra solo Movies, Music, Videos
 * Para GENERAL: Muestra todas las tabs de contenido (Movies, Music, Videos, Weather) SIN "Asignados"
 *
 * @param isPatient Si el usuario es PATIENT (con terapeuta asignado)
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
        // Para PATIENT: Tabs de contenido + "Asignados"
        // Para PSYCHOLOGIST: Solo tabs de contenido
        // Para GENERAL: Solo tabs de contenido (sin asignados)
        if (isPatient) {
            val selectedTabIndex = if (currentTab == "assignments") {
                0
            } else {
                availableTabs.indexOf(selectedType).takeIf { it >= 0 }?.plus(1) ?: 1
            }

            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = Green65,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    if (tabPositions.isNotEmpty() && selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Green65
                        )
                    }
                },
                divider = {}
            ) {
                Tab(
                    selected = currentTab == "assignments",
                    onClick = { onTabChange("assignments") },
                    text = {
                        Text(
                            text = "Asignados",
                            style = SourceSansRegular.copy(fontSize = 15.sp),
                            color = if (currentTab == "assignments") Green65 else Color.White
                        )
                    }
                )

                availableTabs.forEach { type ->
                    val isSelected = currentTab == "content" && selectedType == type
                    Tab(
                        selected = isSelected,
                        onClick = {
                            onTabChange("content")
                            onContentTypeSelected(type)
                        },
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
        } else {
            // Para psicólogos y usuarios generales: Solo tabs de contenido
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
