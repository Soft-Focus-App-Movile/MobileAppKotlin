package com.softfocus.features.admin.domain.models

data class AdminUser(
    val id: String,
    val email: String,
    val fullName: String,
    val userType: String,
    val isActive: Boolean,
    val lastLogin: String?,
    val createdAt: String,
    val isVerified: Boolean?
)
