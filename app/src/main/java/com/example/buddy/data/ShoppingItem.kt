package com.example.buddy.data

data class ShoppingItem(
    val id: Int,
    val name: String,
    val subtitle: String = "",
    val category: String = "Pantry",
    val store: String = "All Stores",
    val isAutoDepleted: Boolean = false,
    val depletionPercent: Int? = null, // e.g. 10 for "10% left", 0 for "0 left"
    val isChecked: Boolean = false,
    val price: Double? = null,
    val addedByInitial: String = "A", // e.g. "A" (green badge) or "M" (orange badge)
    val imageUrl: String = ""
)
