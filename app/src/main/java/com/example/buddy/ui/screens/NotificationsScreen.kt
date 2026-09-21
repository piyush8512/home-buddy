package com.example.buddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.buddy.ui.theme.BaseCanvas
import com.example.buddy.ui.theme.MutedSlate
import com.example.buddy.ui.theme.OnSurface
import com.example.buddy.ui.theme.SurfaceWhite

enum class NotificationFilter(val label: String, val badge: String? = null, val isBadgeUrgent: Boolean = false) {
    ALL("All", "5", false),
    URGENT("Urgent", "2", true),
    SHOPPING("Shopping", null, false),
    HOUSEHOLD("Household", null, false)
}

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    onNavigateToShopping: () -> Unit = {},
    onActionClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedFilter by remember { mutableStateOf(NotificationFilter.ALL) }
    var areAllRead by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BaseCanvas)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // --- 2. Activity & Alerts Header with All Synced pill ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Activity & Alerts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Real-time household updates",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSlate,
                    fontSize = 13.sp
                )
            }

            // "● All Synced" Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFEBE8E1))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF285E43))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "All Synced",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF285E43),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 3. Filter Pills Row: All 5, Urgent 2, Shopping, Household ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NotificationFilter.values().forEach { filter ->
                val isSelected = (selectedFilter == filter)
                FilterPill(
                    filter = filter,
                    isSelected = isSelected,
                    onClick = { selectedFilter = filter }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- 4. Notification Cards List ---
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card 1: High Priority (Visible for ALL or URGENT)
            if (selectedFilter == NotificationFilter.ALL || selectedFilter == NotificationFilter.URGENT) {
                NotificationCardSurface {
                    // Header Row: Red dot, HIGH PRIORITY, 10m ago
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFC04B32))
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = "HIGH PRIORITY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC04B32),
                                letterSpacing = 1.1.sp,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "10m ago",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Action needed: 2 items expired",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Greek Yogurt 500g and Amoxicillin 250mg expired yesterday. Tap to review and discard safely.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF55524A),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // "Review & Discard"
                        Surface(
                            onClick = { onActionClick("Discarding expired items") },
                            shape = RoundedCornerShape(18.dp),
                            color = Color.Black,
                            modifier = Modifier.testTag("review_discard_btn")
                        ) {
                            Text(
                                text = "Review & Discard",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }

                        // "Snooze 24h"
                        Surface(
                            onClick = { onActionClick("Notification snoozed for 24 hours") },
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFFF1EFEA),
                            modifier = Modifier.testTag("snooze_btn")
                        ) {
                            Text(
                                text = "Snooze 24h",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Card 2: Expiring Soon (Visible for ALL or URGENT)
            if (selectedFilter == NotificationFilter.ALL || selectedFilter == NotificationFilter.URGENT) {
                NotificationCardSurface {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFC56328))
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = "EXPIRING SOON",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC56328),
                                letterSpacing = 1.1.sp,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "2h ago",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "3 items expiring in 48 hours",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Oat Milk Barista, Baby Spinach, and Fresh Mozzarella should be consumed soon to avoid spoilage.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF55524A),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        onClick = { onActionClick("Opening suggested recipes for expiring items") },
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFF1EFEA),
                        modifier = Modifier.testTag("see_recipes_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MenuBook,
                                contentDescription = null,
                                tint = OnSurface,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "See Recipes & Use",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Card 3: Inventory Logic (Visible for ALL or SHOPPING)
            if (selectedFilter == NotificationFilter.ALL || selectedFilter == NotificationFilter.SHOPPING) {
                NotificationCardSurface {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2C2B28))
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = "INVENTORY LOGIC",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2B28),
                                letterSpacing = 1.1.sp,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "5h ago",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Auto-added to Shopping List",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Avocado Oil and Paracetamol dropped below threshold (10% remaining). Restock ticket opened for Whole Foods.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF55524A),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        onClick = onNavigateToShopping,
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFF1EFEA),
                        modifier = Modifier.testTag("open_shopping_list_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = null,
                                tint = OnSurface,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Open Shopping List",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Card 4: Pantry Activity (Alex Chen completed grocery trip)
            if (selectedFilter == NotificationFilter.ALL || selectedFilter == NotificationFilter.HOUSEHOLD) {
                NotificationCardSurface {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=200&auto=format&fit=crop&q=80",
                                contentDescription = "Alex Chen",
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = "PANTRY ACTIVITY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF285E43),
                                letterSpacing = 1.1.sp,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "Yesterday, 6:30 PM",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Alex Chen completed\ngrocery trip",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFE2F0E7))
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "+$18.40",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E5D3B),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "3 items restocked into Kitchen Fridge (Oat Milk x2, Organic Eggs 12pk). Logged to household split.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF55524A),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // Card 5: Lending Tracker (Sony WH-1000XM5 due back tomorrow)
            if (selectedFilter == NotificationFilter.ALL || selectedFilter == NotificationFilter.HOUSEHOLD) {
                NotificationCardSurface {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFFECE5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.SwapHoriz,
                                    contentDescription = null,
                                    tint = Color(0xFFC04B32),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = "LENDING TRACKER",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A4842),
                                letterSpacing = 1.1.sp,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "2d ago",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Sony WH-1000XM5 due back tomorrow",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Lent to Rahul on Oct 20. Target return date is set for tomorrow afternoon.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF55524A),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // "Ping Rahul"
                        Surface(
                            onClick = { onActionClick("Ping notification sent to Rahul!") },
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFFF1EFEA),
                            modifier = Modifier.testTag("ping_rahul_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Send,
                                    contentDescription = null,
                                    tint = OnSurface,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Ping Rahul",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OnSurface,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // "Extend Date"
                        Surface(
                            onClick = { onActionClick("Loan due date extended by 7 days") },
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFFF1EFEA),
                            modifier = Modifier.testTag("extend_date_btn")
                        ) {
                            Text(
                                text = "Extend Date",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // --- 5. Footer Actions: Mark all as read • Notification Settings ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        areAllRead = true
                        onActionClick("All notifications marked as read")
                    }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .testTag("mark_all_read_btn")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = MutedSlate,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (areAllRead) "All read" else "Mark all as read",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MutedSlate,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "•", color = MutedSlate, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onActionClick("Opening notification preferences") }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .testTag("notification_settings_btn")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = null,
                    tint = MutedSlate,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Notification Settings",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MutedSlate,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // End-to-end synced footer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = MutedSlate,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "End-to-end synced within Household Vault",
                style = MaterialTheme.typography.labelSmall,
                color = MutedSlate,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun NotificationCardSurface(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        color = SurfaceWhite
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun FilterPill(
    filter: NotificationFilter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) Color.Black else Color(0xFFF4F2EC)
    val textColor = if (isSelected) Color.White else OnSurface

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        modifier = Modifier.testTag("filter_pill_${filter.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = filter.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = textColor,
                fontSize = 13.sp
            )

            if (filter.badge != null) {
                Spacer(modifier = Modifier.width(6.dp))
                val badgeBg = when {
                    isSelected -> Color(0xFF333333)
                    filter.isBadgeUrgent -> Color(0xFFFFECE5)
                    else -> Color(0xFFE5E2DC)
                }
                val badgeTextColor = when {
                    isSelected -> Color.White
                    filter.isBadgeUrgent -> Color(0xFFC04B32)
                    else -> OnSurface
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(badgeBg)
                        .size(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter.badge,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
