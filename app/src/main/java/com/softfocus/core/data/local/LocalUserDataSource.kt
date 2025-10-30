package com.softfocus.core.data.local

import android.content.Context
import android.content.SharedPreferences

class LocalUserDataSource(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("soft_focus_prefs", Context.MODE_PRIVATE)

    fun saveTherapeuticRelationship(hasRelationship: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_HAS_RELATIONSHIP, hasRelationship)
            .apply()
    }

    fun hasTherapeuticRelationship(): Boolean {
        return sharedPreferences.getBoolean(KEY_HAS_RELATIONSHIP, false)
    }

    fun clearTherapeuticRelationship() {
        sharedPreferences.edit()
            .remove(KEY_HAS_RELATIONSHIP)
            .apply()
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val KEY_HAS_RELATIONSHIP = "has_therapeutic_relationship"
    }
}
