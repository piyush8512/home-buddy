package com.example.buddy.data.user

data class User(
    val id: String,
    val firebaseUid: String,
    val email: String,
    val displayName: String?,
    val avatarUrl: String?,
    val is_active: Boolean
)