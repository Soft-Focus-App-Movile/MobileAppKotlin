package com.softfocus.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Modo de tema elegido por el usuario. SYSTEM = seguir el ajuste del sistema. */
enum class ThemeMode { SYSTEM, LIGHT, DARK }

/**
 * Maneja el modo de tema elegido por el usuario y lo persiste en SharedPreferences.
 *
 * [mode] es un estado de Compose: al cambiarlo con [setMode], cualquier Composable que lo
 * lea (p. ej. la raíz de la app en MainActivity) se recompone y toda la app cambia de tema.
 */
object ThemeManager {
    private const val PREFS = "softfocus_theme_prefs"
    private const val KEY_MODE = "theme_mode"

    var mode by mutableStateOf(ThemeMode.SYSTEM)
        private set

    private var loaded = false

    /** Carga la preferencia guardada. Idempotente: llamar al arrancar la app. */
    fun load(context: Context) {
        if (loaded) return
        val saved = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_MODE, ThemeMode.SYSTEM.name)
        mode = runCatching { ThemeMode.valueOf(saved ?: ThemeMode.SYSTEM.name) }
            .getOrDefault(ThemeMode.SYSTEM)
        loaded = true
    }

    /** Cambia el modo y lo guarda. La app se re-renderiza al instante. */
    fun setMode(context: Context, newMode: ThemeMode) {
        mode = newMode
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_MODE, newMode.name)
            .apply()
    }
}
