package com.example.buddy.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.buddy.data.ShoppingItem
import com.example.buddy.ui.PantryViewModel
import com.example.buddy.ui.theme.BaseCanvas
import com.example.buddy.ui.theme.BorderHairline
import com.example.buddy.ui.theme.MutedSlate
import com.example.buddy.ui.theme.OnSurface
import com.example.buddy.ui.theme.OnSurfaceVariant
import com.example.buddy.ui.theme.PrimaryDark
import com.example.buddy.ui.theme.SecondarySage
import com.example.buddy.ui.theme.SurfaceContainer
import com.example.buddy.ui.theme.SurfaceContainerHigh
import com.example.buddy.ui.theme.SurfaceContainerLow
import com.example.buddy.ui.theme.SurfaceWhite
import com.example.buddy.ui.theme.TerracottaAccent
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun ListsScreen(
    viewModel: PantryViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val items by viewModel.restockItems.collectAsStateWithLifecycle()
    val selectedStore by viewModel.selectedStoreFilter.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var showHouseholdMenu by remember { mutableStateOf(false) }
    var householdName by remember { mutableStateOf("Sharma Home") }

    val autoDepletedItems = items.filter { it.isAutoDepleted }
    val householdItems = items.filter { !it.isAutoDepleted }
    val checkedItems = items.filter { it.isChecked }
    val totalCheckedPrice = checkedItems.sumOf { it.price ?: 0.0 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BaseCanvas)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {


        // --- 2. Heading Section: Shopping List + Organize Button ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Shopping List",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${items.count { !it.isChecked }} items to pick up",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedSlate,
                    fontSize = 15.sp
                )
            }

            // Organize Pill Button
            Surface(
                onClick = { },
                shape = RoundedCornerShape(22.dp),
                color = SurfaceContainerLow,
                border = BorderStroke(1.dp, BorderHairline),
                modifier = Modifier.testTag("organize_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = "Organize",
                        tint = OnSurface,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Organize",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- 3. Filter Pills Row ---
        val storePills = listOf(
            Triple("All Stores", "4", "All Stores"),
            Triple("Whole Foods", "3", "Whole Foods"),
            Triple("Pharmacy", "1", "Pharmacy"),
            Triple("Farmer's Market", "2", "Farmer's Market")
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            storePills.forEach { (label, count, key) ->
                val isSelected = selectedStore == key
                Surface(
                    onClick = { viewModel.setStoreFilter(key) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) PrimaryDark else SurfaceWhite,
                    border = if (isSelected) null else BorderStroke(1.dp, BorderHairline),
                    shadowElevation = if (isSelected) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else OnSurface,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = count,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) Color.White.copy(alpha = 0.65f) else MutedSlate,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 4. Section: AUTO-DEPLETED ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Sensors,
                    contentDescription = null,
                    tint = Color(0xFF2D6A4F),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AUTO-DEPLETED",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = Color(0xFF2D6A4F),
                    fontSize = 12.sp
                )
            }

            Text(
                text = "Low inventory trigger",
                style = MaterialTheme.typography.labelSmall,
                color = MutedSlate,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Auto-Depleted Cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            autoDepletedItems.forEach { item ->
                ShoppingItemCard(
                    item = item,
                    onToggleCheck = { viewModel.toggleRestockItem(item.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 5. Section: ADDED BY HOUSEHOLD ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.People,
                    contentDescription = null,
                    tint = MutedSlate,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ADDED BY HOUSEHOLD",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = OnSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "${householdItems.count { it.isChecked }} checked",
                style = MaterialTheme.typography.labelSmall,
                color = MutedSlate,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Added by Household Cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            householdItems.forEach { item ->
                ShoppingItemCard(
                    item = item,
                    onToggleCheck = { viewModel.toggleRestockItem(item.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 6. Info Callout: Smart Pantry Sync Banner ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFEAF5EE)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Sage Leaf Icon inside pill
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCEEBD9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2D6A4F),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Smart Pantry Sync",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Completing this trip will instantly refill your inventory tracking and reset depletion dates for $householdName.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF3B4840),
                        lineHeight = 20.sp,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- 7. Bottom Card: In-Cart Summary & Complete Trip Action ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            color = SurfaceWhite
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Cart status header
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
                                .background(Color(0xFF2D6A4F))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${checkedItems.size} items in cart",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = OnSurface,
                            fontSize = 14.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "Est. ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedSlate,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", if (totalCheckedPrice > 0) totalCheckedPrice else 8.69)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Complete Trip Button
                Button(
                    onClick = {
                        viewModel.completeTripAndUpdateStock()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("complete_trip_btn"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Complete Trip & Update Stock",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun ShoppingItemCard(
    item: ShoppingItem,
    onToggleCheck: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(18.dp))
            .clickable { onToggleCheck() }
            .testTag("shopping_item_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Checkbox
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        if (item.isChecked) Color(0xFF35524A) else Color(0xFFF1F0EC)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (item.isChecked) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Checked",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Thumbnail Image Box
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerLow)
            ) {
                if (item.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Title & Subtitle / Location
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.isChecked) MutedSlate else OnSurface,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.subtitle.ifEmpty { item.category },
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSlate,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right side badge: either Depletion pill or Avatar badge (A / M)
            if (item.depletionPercent != null) {
                val isZero = item.depletionPercent == 0
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFEAE4))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isZero) "0 left" else "${item.depletionPercent}% left",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC04B32),
                        fontSize = 11.sp
                    )
                }
            } else if (item.addedByInitial.isNotEmpty()) {
                val isGreen = item.addedByInitial.equals("A", ignoreCase = true)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            if (isGreen) Color(0xFFCEEBD9) else Color(0xFFFFD9CE)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.addedByInitial,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGreen) Color(0xFF235A42) else Color(0xFF9E3A1F)
                    )
                }
            }
        }
    }
}
