package com.example.buddy.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Desk
import androidx.compose.material.icons.outlined.DragHandle
import androidx.compose.material.icons.outlined.Dry
import androidx.compose.material.icons.outlined.Garage
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.RoomPreferences
import androidx.compose.material.icons.outlined.SevereCold
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Upcoming
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.buddy.ui.theme.BaseCanvas
import com.example.buddy.ui.theme.MutedSlate
import com.example.buddy.ui.theme.OnSurface
import com.example.buddy.ui.theme.SurfaceWhite

data class ConfiguredSpace(
    val id: Int,
    val icon: ImageVector,
    val name: String,
    val itemsCount: Int,
    val alertPillText: String? = null,
    val alertPillType: AlertPillType = AlertPillType.NONE,
    val secondAlertPillText: String? = null,
    val secondAlertPillType: AlertPillType = AlertPillType.NONE,
    val pockets: String
)

enum class AlertPillType {
    NONE, WARNING_TERRACOTTA, EXPIRING_PEACH, SAFE_GREEN, REFILL_PEACH, LENT_MUTED
}

enum class RoomCategoryType(val label: String, val icon: ImageVector) {
    KITCHEN("Kitchen", Icons.Outlined.Kitchen),
    PANTRY("Pantry", Icons.Outlined.LocalDining),
    MEDICINE("Medicine", Icons.Outlined.LocalPharmacy),
    GARAGE("Garage", Icons.Outlined.Garage),
    OFFICE("Office", Icons.Outlined.Desk),
    OUTDOOR("Outdoor", Icons.Outlined.Park)
}

enum class PreservationClimate(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
) {
    ROOM_TEMP("Room Temp", "Ambient (18-22°C)", Icons.Outlined.Thermostat),
    CHILLED("Chilled", "Refrigerated (3-5°C)", Icons.Outlined.AcUnit),
    FROZEN("Frozen", "Deep freeze (<-18°C)", Icons.Outlined.SevereCold),
    DRY_DARK("Dry & Dark", "Cellar & Herb Vault", Icons.Outlined.Dry)
}

@Composable
fun StorageZonesPlacesScreen(
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    onZoneCreated: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    var showHouseholdDropdown by remember { mutableStateOf(false) }
    var currentHousehold by remember { mutableStateOf("Household") }

    // Configured Spaces initial list
    var spacesList by remember {
        mutableStateOf(
            listOf(
                ConfiguredSpace(
                    id = 1,
                    icon = Icons.Outlined.Kitchen,
                    name = "Kitchen Fridge",
                    itemsCount = 18,
                    alertPillText = "2 low stock",
                    alertPillType = AlertPillType.WARNING_TERRACOTTA,
                    secondAlertPillText = "1 expiring soon",
                    secondAlertPillType = AlertPillType.EXPIRING_PEACH,
                    pockets = "Top Door Bin, Crisper, Freezer"
                ),
                ConfiguredSpace(
                    id = 2,
                    icon = Icons.Outlined.LocalDining,
                    name = "Main Pantry",
                    itemsCount = 24,
                    alertPillText = "All items safe",
                    alertPillType = AlertPillType.SAFE_GREEN,
                    pockets = "Shelf A (Grains), Shelf B (Cans), ..."
                ),
                ConfiguredSpace(
                    id = 3,
                    icon = Icons.Outlined.LocalPharmacy,
                    name = "Bathroom Medicine Cabinet",
                    itemsCount = 12,
                    alertPillText = "1 refill due",
                    alertPillType = AlertPillType.REFILL_PEACH,
                    pockets = "Priya's Meds, First Aid"
                ),
                ConfiguredSpace(
                    id = 4,
                    icon = Icons.Outlined.Desk,
                    name = "Living Room & Tech Drawer",
                    itemsCount = 8,
                    alertPillText = "1 lent out",
                    alertPillType = AlertPillType.LENT_MUTED,
                    pockets = "Cable Organizer, Audio Gear"
                ),
                ConfiguredSpace(
                    id = 5,
                    icon = Icons.Outlined.Garage,
                    name = "Garage Storage & Tools",
                    itemsCount = 14,
                    alertPillText = "2 lent to neighbor",
                    alertPillType = AlertPillType.LENT_MUTED,
                    pockets = "Tool Chest, Camping Gear"
                ),
                ConfiguredSpace(
                    id = 6,
                    icon = Icons.Outlined.Checkroom,
                    name = "Master Bedroom Wardrobe",
                    itemsCount = 8,
                    alertPillText = "All safe",
                    alertPillType = AlertPillType.SAFE_GREEN,
                    pockets = "Top Shelf, Hanging Rack, Footwe..."
                )
            )
        )
    }

    // New Zone Form States
    var newZoneName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(RoomCategoryType.PANTRY) }
    var selectedClimate by remember { mutableStateOf(PreservationClimate.CHILLED) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BaseCanvas)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("storage_zones_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OnSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Storage Zones & Places",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Home Inventory Architecture",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate,
                        fontSize = 12.sp
                    )
                }
            }

            // "+ Add Zone" Pill Button
            Surface(
                onClick = {
                    // Scroll down to the Add Zone card
                },
                shape = RoundedCornerShape(20.dp),
                color = Color.Black,
                modifier = Modifier.testTag("top_add_zone_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Zone",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- 3. Overview Metrics Card: Places, Stocked, Alerts ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            color = SurfaceWhite
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.RoomPreferences,
                            contentDescription = null,
                            tint = Color(0xFF285E43),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "PLACES",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = OnSurface,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${spacesList.size}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Active rooms",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate,
                        fontSize = 12.sp
                    )
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .width(1.dp)
                        .background(Color(0xFFEFECE6))
                )

                // Stocked
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Upcoming,
                            contentDescription = null,
                            tint = Color(0xFF285E43),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "STOCKED",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = OnSurface,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${spacesList.sumOf { it.itemsCount }}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tracked items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate,
                        fontSize = 12.sp
                    )
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .width(1.dp)
                        .background(Color(0xFFEFECE6))
                )

                // Alerts
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Whatshot,
                            contentDescription = null,
                            tint = Color(0xFFB84E34),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "ALERTS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color(0xFFB84E34),
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "2",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB84E34),
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Low warning",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB84E34),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 4. Configured Spaces Header: Hold to sort, Live Sync ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CONFIGURED SPACES (HOLD ☰ TO SORT)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp,
                color = MutedSlate,
                fontSize = 11.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    tint = Color(0xFF3F5E52),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Live Sync",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF3F5E52),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- 5. Spaces List ---
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            spacesList.forEach { space ->
                ConfiguredSpaceCard(
                    space = space,
                    onMoreClick = { }
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- 6. Add New Storage Zone Card (Step 1 of 2) ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = SurfaceWhite
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header with green dot, title, and "Step 1 of 2" badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF285E43))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add New Storage Zone",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            fontSize = 18.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF1EFEA))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Step 1 of 2",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MutedSlate,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Zone Name & Label Input
                Text(
                    text = "Zone Name & Label",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF6F4F0))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (newZoneName.isEmpty()) {
                        Text(
                            text = "e.g. Wine Cooler, Deep Freezer, Hallway C",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFA5A198),
                            fontSize = 14.sp
                        )
                    }
                    BasicTextField(
                        value = newZoneName,
                        onValueChange = { newZoneName = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = OnSurface,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("zone_name_input")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Category Room Type Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Category Room Type",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "${selectedCategory.label} Selected",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF285E43),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Buttons Grid (2 rows x 3 columns)
                val categories = RoomCategoryType.values()
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in 0 until 3) {
                            val cat = categories[i]
                            val isSelected = (selectedCategory == cat)
                            CategoryPillButton(
                                category = cat,
                                isSelected = isSelected,
                                onClick = { selectedCategory = cat },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in 3 until 6) {
                            val cat = categories[i]
                            val isSelected = (selectedCategory == cat)
                            CategoryPillButton(
                                category = cat,
                                isSelected = isSelected,
                                onClick = { selectedCategory = cat },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Storage Climate & Preservation Header
                Text(
                    text = "Storage Climate & Preservation",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2x2 Grid of Preservation Climates
                val climates = PreservationClimate.values()
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ClimateTile(
                            climate = climates[0],
                            isSelected = selectedClimate == climates[0],
                            onClick = { selectedClimate = climates[0] },
                            modifier = Modifier.weight(1f)
                        )
                        ClimateTile(
                            climate = climates[1],
                            isSelected = selectedClimate == climates[1],
                            onClick = { selectedClimate = climates[1] },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ClimateTile(
                            climate = climates[2],
                            isSelected = selectedClimate == climates[2],
                            onClick = { selectedClimate = climates[2] },
                            modifier = Modifier.weight(1f)
                        )
                        ClimateTile(
                            climate = climates[3],
                            isSelected = selectedClimate == climates[3],
                            onClick = { selectedClimate = climates[3] },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Create Storage Zone Button
                Surface(
                    onClick = {
                        val nameToUse = if (newZoneName.isNotBlank()) newZoneName.trim() else "Custom Storage Zone"
                        val newSpace = ConfiguredSpace(
                            id = spacesList.size + 1,
                            icon = selectedCategory.icon,
                            name = nameToUse,
                            itemsCount = 0,
                            alertPillText = "All safe",
                            alertPillType = AlertPillType.SAFE_GREEN,
                            pockets = "Main Compartment"
                        )
                        spacesList = spacesList + newSpace
                        newZoneName = ""
                        onZoneCreated(nameToUse)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("create_storage_zone_btn"),
                    shape = RoundedCornerShape(26.dp),
                    color = Color.Black
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Create Storage Zone",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
private fun ConfiguredSpaceCard(
    space: ConfiguredSpace,
    onMoreClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceWhite
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Drag handle
                Icon(
                    imageVector = Icons.Outlined.DragHandle,
                    contentDescription = "Hold to sort",
                    tint = Color(0xFFC4C0B6),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Room Icon Box
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F3EF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = space.icon,
                        contentDescription = null,
                        tint = OnSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title & Items count
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = space.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${space.itemsCount} items logged",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate,
                        fontSize = 12.sp
                    )
                }

                // More icon button
                IconButton(
                    onClick = onMoreClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "More options",
                        tint = OnSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Alert Pills if present
            if (space.alertPillText != null || space.secondAlertPillText != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.padding(start = 28.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (space.alertPillText != null) {
                        AlertPillBadge(
                            text = space.alertPillText,
                            type = space.alertPillType
                        )
                    }
                    if (space.secondAlertPillText != null) {
                        AlertPillBadge(
                            text = space.secondAlertPillText,
                            type = space.secondAlertPillType
                        )
                    }
                }
            }

            // Pockets / Sub-locations
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pockets: ${space.pockets}",
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 28.dp)
            )
        }
    }
}

@Composable
private fun AlertPillBadge(
    text: String,
    type: AlertPillType
) {
    val (bgColor, textColor) = when (type) {
        AlertPillType.WARNING_TERRACOTTA -> Color(0xFFFFECE5) to Color(0xFFC04B32)
        AlertPillType.EXPIRING_PEACH -> Color(0xFFFFF2E8) to Color(0xFFC56328)
        AlertPillType.SAFE_GREEN -> Color(0xFFE5F5EC) to Color(0xFF236544)
        AlertPillType.REFILL_PEACH -> Color(0xFFFFF0E8) to Color(0xFFBF5C28)
        AlertPillType.LENT_MUTED -> Color(0xFFF1EFEA) to Color(0xFF5A5850)
        AlertPillType.NONE -> Color(0xFFF1EFEA) to OnSurface
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun CategoryPillButton(
    category: RoomCategoryType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) Color.Black else Color(0xFFF6F4F0)
    val contentColor = if (isSelected) Color.White else OnSurface

    Surface(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(14.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = category.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ClimateTile(
    climate: PreservationClimate,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) Color(0xFF385A4D) else Color(0xFFF6F4F0)
    val titleColor = if (isSelected) Color.White else OnSurface
    val subtitleColor = if (isSelected) Color(0xFFD6E2DC) else MutedSlate
    val iconTint = if (isSelected) Color.White else Color(0xFF385A4D)

    Surface(
        onClick = onClick,
        modifier = modifier.height(68.dp),
        shape = RoundedCornerShape(16.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = climate.icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = climate.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = climate.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = subtitleColor,
                    fontSize = 11.sp,
                    lineHeight = 13.sp
                )
            }
        }
    }
}
