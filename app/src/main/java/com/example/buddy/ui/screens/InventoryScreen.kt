package com.example.buddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.buddy.data.PantryItem
import com.example.buddy.ui.NavTab
import com.example.buddy.ui.PantryViewModel
import com.example.buddy.ui.calculateDaysRemaining
import com.example.buddy.ui.theme.BaseCanvas
import com.example.buddy.ui.theme.BorderHairline
import com.example.buddy.ui.theme.OnSecondaryFixed
import com.example.buddy.ui.theme.OnSurface
import com.example.buddy.ui.theme.OnSurfaceVariant
import com.example.buddy.ui.theme.PrimaryDark
import com.example.buddy.ui.theme.SecondaryFixed
import com.example.buddy.ui.theme.SecondarySage
import com.example.buddy.ui.theme.SurfaceContainerLow
import com.example.buddy.ui.theme.SurfaceWhite
import com.example.buddy.ui.theme.TerracottaAccent
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable

@Composable
fun InventoryScreen(
    viewModel: PantryViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.pantryItems.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedZone by remember { mutableStateOf("All") }

    val zones = listOf("All", "Fridge Door", "Main Fridge", "Crisper Drawer", "Pantry Shelf", "Deep Freezer")

    val filteredItems = items.filter { item ->
        (selectedZone == "All" || item.storageZone == selectedZone) &&
                (searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true) || item.category.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BaseCanvas)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search pantry items, categories...", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("inventory_search_input"),
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceContainerLow,
                focusedBorderColor = SecondarySage,
                unfocusedBorderColor = BorderHairline
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Zone Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            zones.forEach { zone ->
                val isSelected = selectedZone == zone
                Surface(
                    onClick = { selectedZone = zone },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) PrimaryDark else SurfaceContainerLow,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderHairline),
                    modifier = Modifier.testTag("zone_filter_$zone")
                ) {
                    Text(
                        text = zone,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.White else OnSurface,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Header Count & Quick Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredItems.size} Items Stored",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )

            Text(
                text = "Sorted by expiry date",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Items List
        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Kitchen,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No items in this section",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface
                    )
                    Text(
                        text = "Scan groceries to add them directly to your inventory.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { viewModel.setBottomNav(NavTab.SCAN) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, tint = SecondaryFixed)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Scanner")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredItems, key = { it.id }) { item ->
                    PantryItemCard(
                        item = item,
                        onDelete = { viewModel.deleteItem(item) },
                        onToggleConsumed = { viewModel.toggleItemConsumed(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun PantryItemCard(
    item: PantryItem,
    onDelete: () -> Unit,
    onToggleConsumed: () -> Unit
) {
    val daysRemaining = calculateDaysRemaining(item.expiryDateMillis)
    val isUrgent = daysRemaining <= 5

    Surface(
        modifier = Modifier
            .width(265.dp)
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        color = SurfaceWhite
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            // IMAGE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
            ) {

                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // STORAGE ZONE BADGE
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF5F4F0)
                ) {

                    Text(
                        text = item.storageZone,
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 2.dp
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceVariant
                    )
                }
            }

            // ITEM DETAILS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 11.dp,
                        end = 11.dp,
                        top = 9.dp,
                        bottom = 10.dp
                    )
            ) {

                // Name + package size
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = item.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = OnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = item.packageSize,
                        fontSize = 12.sp,
                        color = OnSurfaceVariant,
                        maxLines = 1
                    )
                }

                Spacer(
                    modifier = Modifier.height(9.dp)
                )

                // EXPIRY + CONSUMED BUTTON
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Expiry badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isUrgent) {
                                    Color(0xFFFFE0D9)
                                } else {
                                    SecondaryFixed
                                }
                            )
                            .padding(
                                horizontal = 9.dp,
                                vertical = 0.dp
                            )
                    ) {

                        Text(
                            text = when {
                                daysRemaining <= 0 ->
                                    "Expired"

                                daysRemaining == 1 ->
                                    "1 day left"

                                else ->
                                    "$daysRemaining days left"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isUrgent) {
                                TerracottaAccent
                            } else {
                                OnSecondaryFixed
                            }
                        )
                    }

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    // Consumed button
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F2EE))
                            .clickable {
                                onToggleConsumed()
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "✓",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}