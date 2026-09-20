package com.example.buddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.QrCodeScanner
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buddy.ui.NavTab
import com.example.buddy.ui.theme.BaseCanvas
import com.example.buddy.ui.theme.BorderHairline
import com.example.buddy.ui.theme.OnSurface
import com.example.buddy.ui.theme.OnSurfaceVariant
import com.example.buddy.ui.theme.PrimaryDark
import com.example.buddy.ui.theme.SecondarySage
import com.example.buddy.ui.theme.SurfaceWhite

@Composable
fun PantryBottomNav(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp, ambientColor = Color(0x0A000000), spotColor = Color(0x0A000000)),
        color = SurfaceWhite.copy(alpha = 0.96f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Outlined.Home,
                label = "Today",
                isSelected = selectedTab == NavTab.HOME,
                onClick = { onTabSelected(NavTab.HOME) },
                testTag = "nav_home"
            )

            NavItem(
                icon = Icons.Outlined.Kitchen,
                label = "Inventory",
                isSelected = selectedTab == NavTab.INVENTORY,
                onClick = { onTabSelected(NavTab.INVENTORY) },
                testTag = "nav_inventory"
            )

            // Center Scanner Highlight Button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .shadow(elevation = 6.dp, shape = CircleShape, spotColor = SecondarySage)
                    .background(SecondarySage)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(NavTab.SCAN) }
                    .testTag("nav_scan_center"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.QrCodeScanner,
                    contentDescription = "Scan",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            NavItem(
                icon = Icons.Outlined.Checklist,
                label = "Lists",
                isSelected = selectedTab == NavTab.LISTS,
                onClick = { onTabSelected(NavTab.LISTS) },
                testTag = "nav_lists"
            )

            NavItem(
                icon = Icons.Outlined.Group,
                label = "Household",
                isSelected = selectedTab == NavTab.HOUSEHOLD,
                onClick = { onTabSelected(NavTab.HOUSEHOLD) },
                testTag = "nav_household"
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) PrimaryDark else OnSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) PrimaryDark else OnSurfaceVariant,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 11.sp
        )
    }
}
