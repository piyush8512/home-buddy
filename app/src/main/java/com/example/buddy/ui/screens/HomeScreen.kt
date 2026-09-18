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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.buddy.ui.NavTab
import com.example.buddy.ui.PantryViewModel
import com.example.buddy.ui.calculateDaysRemaining
import com.example.buddy.ui.theme.BaseCanvas
import com.example.buddy.ui.theme.OnSurface
import com.example.buddy.ui.theme.OnSurfaceVariant
import com.example.buddy.ui.theme.PrimaryDark
import com.example.buddy.ui.theme.SecondaryFixed
import com.example.buddy.ui.theme.SecondarySage
import com.example.buddy.ui.theme.SurfaceWhite
import com.example.buddy.ui.theme.TerracottaAccent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun HomeScreen(
    viewModel: PantryViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.pantryItems.collectAsStateWithLifecycle()
    val expiringItems = items.filter {
        calculateDaysRemaining(it.expiryDateMillis) <= 5
    }

    val expiringSoonCount = expiringItems.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BaseCanvas)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // TOP STATUS + SEARCH
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = Color(0xFFE8E8E3)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 2.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF52796F))
                    )
                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )
                    Text(
                        text = "Pantry & Chilled Sync",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceVariant
                    )
                }
            }
            Spacer(
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F4F0))
                    .clickable {
                        // Search action
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = OnSurface,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        //Greetings
        Text(
            text = "Good morning, Piyush",
            fontSize = 24.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = when (expiringSoonCount) {
                0 -> "Everything looks good this week"
                1 -> "1 item needs your attention this week"
                else -> "$expiringSoonCount items need your attention this week"
            },
            fontSize = 14.sp,
            color = OnSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        //expring Alert
        if (expiringItems.isNotEmpty()) {
            val priorityItem = expiringItems.first()
            PriorityCard(
                itemName = priorityItem.name,
                daysRemaining = calculateDaysRemaining(
                    priorityItem.expiryDateMillis
                ),
                onClick = {
                    // Open item details
                }
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )
        }

        // Expiring Soon Priority List
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Expiring Soon",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelMedium,
                color = SecondarySage,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { viewModel.setBottomNav(NavTab.INVENTORY) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items.take(4).forEach { item ->
                PantryItemCard(
                    item = item,
                    onDelete = { viewModel.deleteItem(item) },
                    onToggleConsumed = { viewModel.toggleItemConsumed(item) }
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    count: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceWhite
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(count, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, fontSize = 11.sp)
        }
    }
}

// PRIORITY CARD

@Composable
private fun PriorityCard(
    itemName: String,
    daysRemaining: Int,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        color = SurfaceWhite,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Priority indicator
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFDAD1)),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(TerracottaAccent)
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            // Item information
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "PRIORITY CHECK",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = TerracottaAccent
                    )

                    Spacer(
                        modifier = Modifier.width(7.dp)
                    )

                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = OnSurfaceVariant
                    )

                    Spacer(
                        modifier = Modifier.width(7.dp)
                    )

                    Text(
                        text = when {
                            daysRemaining <= 0 -> {
                                "Expired"
                            }

                            daysRemaining == 1 -> {
                                "Expires in 24h"
                            }

                            else -> {
                                "Expires in ${daysRemaining * 24}h"
                            }
                        },
                        fontSize = 14.sp,
                        color = OnSurfaceVariant
                    )
                }

                Text(
                    text = itemName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = OnSurface,
                    maxLines = 1
                )
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            // Arrow button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF2F1ED)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = "Open item",
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
