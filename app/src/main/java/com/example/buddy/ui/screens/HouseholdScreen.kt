package com.example.buddy.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Garage
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.outlined.Warehouse
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.buddy.ui.theme.BaseCanvas
import com.example.buddy.ui.theme.BorderHairline
import com.example.buddy.ui.theme.MutedSlate
import com.example.buddy.ui.theme.OnSurface
import com.example.buddy.ui.theme.OnSurfaceVariant
import com.example.buddy.ui.theme.PrimaryDark
import com.example.buddy.ui.theme.SurfaceContainerLow
import com.example.buddy.ui.theme.SurfaceWhite

data class HouseholdMember(
    val id: Int,
    val name: String,
    val role: String,
    val isCurrentUser: Boolean = false,
    val permissionBadge: String,
    val badgeType: BadgeType,
    val avatarUrl: String,
    val isOnline: Boolean = false
)

enum class BadgeType {
    GREEN, GREY, VIEW_ONLY
}

data class StorageZoneItem(
    val name: String,
    val itemCount: Int,
    val statusText: String? = null,
    val statusColor: ZoneStatusType = ZoneStatusType.NONE,
    val icon: ImageVector,
    val hasChevron: Boolean = false
)

enum class ZoneStatusType {
    NONE, WARNING_RED, SAFE_GREEN, REFILL_MUTED, LENT_MUTED
}

@Composable
fun HouseholdScreen(
    modifier: Modifier = Modifier,
    onInviteClick: () -> Unit = {},
    onAddZoneClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    var showHouseholdMenu by remember { mutableStateOf(false) }
    var currentHousehold by remember { mutableStateOf("Sharma Home") }

    // Toggle states for alerts & triggers
    var expiryNoticeEnabled by remember { mutableStateOf(true) }
    var criticalAlertsEnabled by remember { mutableStateOf(true) }
    var lowStockReorderEnabled by remember { mutableStateOf(true) }
    var lentRemindersEnabled by remember { mutableStateOf(true) }

    val members = remember {
        listOf(
            HouseholdMember(
                id = 1,
                name = "Priya Sharma",
                role = "Household Admin",
                isCurrentUser = true,
                permissionBadge = "All Notifications",
                badgeType = BadgeType.GREEN,
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
                isOnline = true
            ),
            HouseholdMember(
                id = 2,
                name = "Alex Chen",
                role = "Co-Resident",
                permissionBadge = "Food & Pantry Only",
                badgeType = BadgeType.GREY,
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80",
                isOnline = false
            ),
            HouseholdMember(
                id = 3,
                name = "Rahul Sharma",
                role = "Member",
                permissionBadge = "Tech & Meds Only",
                badgeType = BadgeType.GREY,
                avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&auto=format&fit=crop&q=80",
                isOnline = false
            ),
            HouseholdMember(
                id = 4,
                name = "Maya Sharma",
                role = "Child Profile",
                permissionBadge = "View Only",
                badgeType = BadgeType.VIEW_ONLY,
                avatarUrl = "https://images.unsplash.com/photo-1544717305-2782549b5136?w=200&auto=format&fit=crop&q=80",
                isOnline = false
            )
        )
    }

    val storageZones = remember {
        listOf(
            StorageZoneItem(
                name = "Kitchen Fridge",
                itemCount = 18,
                statusText = "2 low",
                statusColor = ZoneStatusType.WARNING_RED,
                icon = Icons.Outlined.Kitchen
            ),
            StorageZoneItem(
                name = "Main Pantry",
                itemCount = 24,
                statusText = "All safe",
                statusColor = ZoneStatusType.SAFE_GREEN,
                icon = Icons.Outlined.Warehouse
            ),
            StorageZoneItem(
                name = "Medicine Cabinet",
                itemCount = 12,
                statusText = "1 refill due",
                statusColor = ZoneStatusType.REFILL_MUTED,
                icon = Icons.Outlined.Medication
            ),
            StorageZoneItem(
                name = "Living Room & Tech",
                itemCount = 8,
                statusText = "1 lent out",
                statusColor = ZoneStatusType.LENT_MUTED,
                icon = Icons.Outlined.Tv
            ),
            StorageZoneItem(
                name = "Garage Storage",
                itemCount = 14,
                icon = Icons.Outlined.Home,
                hasChevron = true
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BaseCanvas)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        // --- 2. Household Overview Card ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = SurfaceWhite
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Family Unlimited Tag + Home Circle Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE3F3EB))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Family Unlimited",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF236544),
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1EFEA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = null,
                            tint = OnSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Sharma Household",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 26.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "4 members • 6 storage zones",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedSlate,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Three Stats Metric Tiles: Total Items, Active Alerts, Sync State
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Items
                    StatMetricTile(
                        modifier = Modifier.weight(1f),
                        label = "Total Items",
                        value = "76",
                        valueColor = OnSurface
                    )

                    // Active Alerts
                    StatMetricTile(
                        modifier = Modifier.weight(1f),
                        label = "Active Alerts",
                        value = "3",
                        valueColor = Color(0xFFC04B32)
                    )

                    // Sync State
                    StatMetricTile(
                        modifier = Modifier.weight(1f),
                        label = "Sync State",
                        value = "Live",
                        valueColor = Color(0xFF236544),
                        showDot = true
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Row: [ Invite Member ] [ QR Button ]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onInviteClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("invite_member_btn"),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PersonAdd,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Invite Member",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Surface(
                        onClick = { },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("qr_code_btn"),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF1EFEA)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.QrCode,
                                contentDescription = "QR Code",
                                tint = OnSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- 3. Members & Permissions Section ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Members & Permissions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 18.sp
                )
                Text(
                    text = "4 co-managers registered",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSlate,
                    fontSize = 13.sp
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { }
                    .padding(vertical = 4.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Role matrix",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = OnSurfaceVariant,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Members List Container Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            color = SurfaceWhite
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                members.forEach { member ->
                    MemberRow(member = member)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- 4. Household Alerts & Triggers Section ---
        Column {
            Text(
                text = "Household Alerts & Triggers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                fontSize = 18.sp
            )
            Text(
                text = "Shared automated notifications for this home",
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Alerts Switches Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            color = SurfaceWhite
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // 1. 7-Day Expiry Notice
                AlertSwitchRow(
                    title = "7-Day Expiry Notice",
                    subtitle = "Early reminders for smooth meal planning",
                    checked = expiryNoticeEnabled,
                    onCheckedChange = { expiryNoticeEnabled = it }
                )

                // 2. Critical 48h & Spoiled Alerts
                AlertSwitchRow(
                    title = "Critical 48h & Spoiled Alerts",
                    subtitle = "Instant push to minimize household food waste",
                    checked = criticalAlertsEnabled,
                    onCheckedChange = { criticalAlertsEnabled = it }
                )

                // 3. Low-Stock Reorder Triggers
                AlertSwitchRow(
                    title = "Low-Stock Reorder Triggers",
                    subtitle = "Auto-appends exhausted pantry items to Lists",
                    checked = lowStockReorderEnabled,
                    onCheckedChange = { lowStockReorderEnabled = it }
                )

                // 4. Lent Item Reminders
                AlertSwitchRow(
                    title = "Lent Item Reminders",
                    subtitle = "Track return deadlines for shared equipment",
                    checked = lentRemindersEnabled,
                    onCheckedChange = { lentRemindersEnabled = it }
                )

                // 5. Daily Digest Window
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Digest Window",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Combined summary for active chores",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        onClick = { },
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF1EFEA)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "9:00 AM\nBatch",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface,
                                fontSize = 11.sp,
                                lineHeight = 13.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = null,
                                tint = OnSurface,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- 5. Storage Zones Section ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Storage Zones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 18.sp
                )
                Text(
                    text = "6 active tracked spaces",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSlate,
                    fontSize = 13.sp
                )
            }

            TextButton(onClick = { }) {
                Text(
                    text = "Reorder",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = OnSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Storage Zone Cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            storageZones.forEach { zone ->
                StorageZoneCard(zone = zone)
            }

            // Add New Room or Zone Button
            Surface(
                onClick = { onAddZoneClick()},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("add_zone_btn"),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFEFECE6)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        tint = Color(0xFF2A5E44),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add New Room or Zone",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- 6. Security Footer ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE6E3DC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "End-to-End Household Encryption • Offline-first local\nsync enabled",
                style = MaterialTheme.typography.labelSmall,
                color = MutedSlate,
                fontSize = 11.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun StatMetricTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    valueColor: Color,
    showDot: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF6F4F0)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MutedSlate,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showDot) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF236544))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                }
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = valueColor,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun MemberRow(member: HouseholdMember) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with online green dot
        Box(
            modifier = Modifier.size(44.dp)
        ) {
            AsyncImage(
                model = member.avatarUrl,
                contentDescription = member.name,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            if (member.isOnline) {
                Box(
                    modifier = Modifier
                        .size(11.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF236544))
                        .border(1.5.dp, Color.White, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface,
                    fontSize = 14.sp
                )
                if (member.isCurrentUser) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEDE9E3))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "You",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            fontSize = 10.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = member.role,
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Permission Badge Pill
        val (bgColor, textColor) = when (member.badgeType) {
            BadgeType.GREEN -> Pair(Color(0xFFD4EEDF), Color(0xFF1E5D3B))
            BadgeType.GREY -> Pair(Color(0xFFEDEAE4), OnSurface)
            BadgeType.VIEW_ONLY -> Pair(Color(0xFFEDEAE4), MutedSlate)
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = member.permissionBadge,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = textColor,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun AlertSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF3F5E52),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFDCD8D0)
            )
        )
    }
}

@Composable
private fun StorageZoneCard(zone: StorageZoneItem) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon in rounded square box
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF3F1EC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = zone.icon,
                    contentDescription = null,
                    tint = OnSurface,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = zone.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${zone.itemCount} items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSlate,
                    fontSize = 13.sp
                )
            }

            if (zone.statusText != null) {
                val (pillBg, pillTextColor) = when (zone.statusColor) {
                    ZoneStatusType.WARNING_RED -> Pair(Color(0xFFFFEAE4), Color(0xFFBA452B))
                    ZoneStatusType.SAFE_GREEN -> Pair(Color(0xFFDCEFE3), Color(0xFF236544))
                    ZoneStatusType.REFILL_MUTED, ZoneStatusType.LENT_MUTED -> Pair(Color(0xFFEDEAE3), MutedSlate)
                    ZoneStatusType.NONE -> Pair(Color.Transparent, Color.Transparent)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(pillBg)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = zone.statusText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = pillTextColor,
                        fontSize = 11.sp
                    )
                }
            } else if (zone.hasChevron) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MutedSlate,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
