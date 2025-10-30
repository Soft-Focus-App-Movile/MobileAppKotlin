package com.softfocus.features.therapy.domain.repositories

import com.softfocus.features.therapy.domain.models.TherapeuticRelationship

interface TherapyRepository {

    suspend fun getMyRelationship(): Result<TherapeuticRelationship?>

    suspend fun connectWithPsychologist(connectionCode: String): Result<String>
}
