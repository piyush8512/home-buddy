package com.example.buddy.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buddy.ui.NavTab

@Composable
fun PantryBottomNav(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = Color(0xFF141414)
    val inactiveColor = Color(0xFF767676)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = Color(0x1A000000),
                    spotColor = Color(0x22000000)
                )
                .border(
                    width = 1.dp,
                    color = Color(0x0F000000),
                    shape = CircleShape
                ),
            shape = CircleShape,
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Today
                NavItemPill(
                    icon = { tint ->
                        Icon(
                            imageVector = Icons.Outlined.GridView,
                            contentDescription = "Today",
                            tint = tint,
                            modifier = Modifier.size(23.dp)
                        )
                    },
                    label = "Today",
                    isSelected = selectedTab == NavTab.HOME,
                    onClick = { onTabSelected(NavTab.HOME) },
                    testTag = "nav_home",
                    modifier = Modifier.weight(1f)
                )

                // 2. Inventory (Custom Pantry Shelf icon matching reference)
                NavItemPill(
                    icon = { tint ->
                        InventoryShelfIcon(
                            tint = tint,
                            modifier = Modifier.size(23.dp)
                        )
                    },
                    label = "Inventory",
                    isSelected = selectedTab == NavTab.INVENTORY,
                    onClick = { onTabSelected(NavTab.INVENTORY) },
                    testTag = "nav_inventory",
                    modifier = Modifier.weight(1.1f)
                )

                // 3. Center Solid Black Action Button (+)
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(NavTab.SCAN) }
                        .testTag("nav_scan_center"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add Item or Voice Sync",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // 4. List
                NavItemPill(
                    icon = { tint ->
                        Icon(
                            imageVector = Icons.Outlined.Checklist,
                            contentDescription = "List",
                            tint = tint,
                            modifier = Modifier.size(23.dp)
                        )
                    },
                    label = "List",
                    isSelected = selectedTab == NavTab.LISTS,
                    onClick = { onTabSelected(NavTab.LISTS) },
                    testTag = "nav_lists",
                    modifier = Modifier.weight(1f)
                )

                // 5. Home
                NavItemPill(
                    icon = { tint ->
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = "Home",
                            tint = tint,
                            modifier = Modifier.size(23.dp)
                        )
                    },
                    label = "Home",
                    isSelected = selectedTab == NavTab.HOUSEHOLD,
                    onClick = { onTabSelected(NavTab.HOUSEHOLD) },
                    testTag = "nav_household",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NavItemPill(
    icon: @Composable (tint: Color) -> Unit,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val activeColor = Color(0xFF141414)
    val inactiveColor = Color(0xFF767676)
    val tint = if (isSelected) activeColor else inactiveColor

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon(tint)
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

@Composable
fun InventoryShelfIcon(
    tint: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 1.9.dp.toPx()
        val cap = StrokeCap.Round
        val w = size.width
        val h = size.height

        // Left upright post
        drawLine(
            color = tint,
            start = Offset(w * 0.20f, h * 0.12f),
            end = Offset(w * 0.20f, h * 0.88f),
            strokeWidth = strokeWidth,
            cap = cap
        )
        // Right upright post
        drawLine(
            color = tint,
            start = Offset(w * 0.80f, h * 0.12f),
            end = Offset(w * 0.80f, h * 0.88f),
            strokeWidth = strokeWidth,
            cap = cap
        )

        // Top horizontal beam
        drawLine(
            color = tint,
            start = Offset(w * 0.20f, h * 0.18f),
            end = Offset(w * 0.80f, h * 0.18f),
            strokeWidth = strokeWidth,
            cap = cap
        )
        // Middle horizontal shelf
        drawLine(
            color = tint,
            start = Offset(w * 0.20f, h * 0.52f),
            end = Offset(w * 0.80f, h * 0.52f),
            strokeWidth = strokeWidth,
            cap = cap
        )
        // Bottom horizontal shelf
        drawLine(
            color = tint,
            start = Offset(w * 0.20f, h * 0.84f),
            end = Offset(w * 0.80f, h * 0.84f),
            strokeWidth = strokeWidth,
            cap = cap
        )

        // Item container on middle shelf (left-aligned)
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.32f, h * 0.32f),
            size = Size(w * 0.20f, h * 0.20f),
            cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )

        // Item container on bottom shelf (right-aligned)
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.52f, h * 0.64f),
            size = Size(w * 0.20f, h * 0.20f),
            cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )
    }
}

