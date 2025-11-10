package com.softfocus.features.admin.data.models.request

data class ChangeUserStatusRequestDto(
    val isActive: Boolean,
    val reason: String?
)
