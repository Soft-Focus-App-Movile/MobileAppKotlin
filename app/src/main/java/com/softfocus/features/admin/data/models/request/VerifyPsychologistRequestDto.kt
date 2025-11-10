package com.softfocus.features.admin.data.models.request

data class VerifyPsychologistRequestDto(
    val isApproved: Boolean,
    val notes: String?
)
