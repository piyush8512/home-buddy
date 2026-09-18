package com.example.buddy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pantry_items")
data class PantryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String = "Food & Pantry • Dairy & Cultured",
    val packageSize: String = "907g tub",
    val barcode: String = "0125460012",
    val expiryDateMillis: Long,
    val storageZone: String = "Fridge Door",
    val quantity: Int = 1,
    val unit: String = "tub",
    val imageUrl: String = "https://lh3.googleusercontent.com/aida-public/AB6AXuAFIqQTH2gNzVof-4dV8MHAzTEr1jgA4AL6rVpEQeuzO1PIaUQ0vo1daIZEg_6IT3HSCqDoe-V-h_PniE8YsUQ7SNucWMmWrk-UCC3UQxM-nqyFZeeN5SeRdEb1EconuL-os6BvhVk9Hq7T5cmWYr7iQwlpMzvKVSWqpW28PxNPiE4H_oFgFC2x2P3_Gr_vhLVEnGZwnpDwUI26J2TB4KL3AIJOMgoZhEnlQmrJSGhT1LxKXJZf8YijNg",
    val addedDateMillis: Long = System.currentTimeMillis(),
    val confidenceScore: Int = 96,
    val isConsumed: Boolean = false
)
