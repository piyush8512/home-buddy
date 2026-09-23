package com.example.buddy.ui.screens


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.buddy.data.PantryItem
import com.example.buddy.ui.PantryViewModel
import com.example.buddy.ui.theme.BaseCanvas
import com.example.buddy.ui.theme.MutedSlate
import com.example.buddy.ui.theme.OnSurface
import com.example.buddy.ui.theme.SurfaceWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

data class DynamicTranscript(
    val id: String,
    val text: String,
    val time: String,
    val confidence: String,
    val buddyReply: String,
    val parsedItemName: String? = null,
    val parsedAction: String? = null
)

@Composable
fun QuickActionVoiceSyncScreen(
    viewModel: PantryViewModel,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    onOcrScanClick: () -> Unit = {},
    onNavigateToShopping: () -> Unit = {},
    onMessage: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var isLiveVoiceActive by remember { mutableStateOf(true) }
    var oatMilkConfirmed by remember { mutableStateOf(false) }
    var sourdoughConfirmed by remember { mutableStateOf(false) }
    var inputQuery by remember { mutableStateOf("") }

    val extraTranscripts = remember { mutableStateListOf<DynamicTranscript>() }

    // Pulsing audio wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")
    val waveHeight1 by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave1"
    )
    val waveHeight2 by infiniteTransition.animateFloat(
        initialValue = 14f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave2"
    )
    val waveHeight3 by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave3"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BaseCanvas)
    ) {
        // --- 1. Top Navigation Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("quick_action_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OnSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Quick Action",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                fontSize = 19.sp
            )

            // User Profile Avatar
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .clickable { onOcrScanClick() }
                    .testTag("quick_action_ocr_scan_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.DocumentScanner,
                    contentDescription = "Scan Item via OCR",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // --- Main Scrollable Content ---
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            // --- 2. Title & Status Header ---
            Text(
                text = "SHARMA RESIDENCE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF285E43),
                letterSpacing = 1.2.sp,
                fontSize = 11.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Voice & Natural Sync",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 22.sp
                )

                // "● Live Voice Active" Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isLiveVoiceActive) Color(0xFFE2F0E7) else Color(0xFFF1EFEA))
                        .clickable { isLiveVoiceActive = !isLiveVoiceActive }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("live_voice_status_pill")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isLiveVoiceActive) Color(0xFF1E5D3B) else MutedSlate)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isLiveVoiceActive) "Live Voice Active" else "Voice Paused",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isLiveVoiceActive) Color(0xFF1E5D3B) else MutedSlate,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // --- 3. Assistant Flow ---

            // Step A: Buddy Initial Greeting
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Buddy avatar circular button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF222220)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Buddy",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        color = SurfaceWhite
                    ) {
                        Text(
                            text = "Good morning Priya. You can speak or type to add items, update quantities, or check what's currently in stock.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2C2B27),
                            fontSize = 14.5.sp,
                            lineHeight = 21.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "08:41 AM · Buddy",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedSlate,
                        fontSize = 11.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step B: User Voice Transcript (Black Bubble, Right-aligned)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .shadow(2.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF181715)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = null,
                                tint = Color(0xFFCCCCCC),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "VOICE TRANSCRIPT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCCCCCC),
                                letterSpacing = 1.1.sp,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "“We bought 2 cartons of Oat Milk expiring Nov 4, and finished the Sourdough Bread yesterday.”",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            fontSize = 14.5.sp,
                            lineHeight = 21.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "99.4% confidence · 08:42 AM",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF285E43),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Priya's user avatar
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
                        contentDescription = "Priya",
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step C: Buddy Parsed Inventory Updates with Confirm Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Buddy avatar circle
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF222220)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Buddy",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        color = SurfaceWhite
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "I parsed 2 inventory updates. Please confirm below before applying to Sharma Home:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF2C2B27),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Item 1 Card: Oat Milk Barista Edition
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF7F5EE)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Top Pill: ⊕ NEW ITEM TO ADD
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFE2F0E7))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Outlined.AddCircleOutline,
                                                contentDescription = null,
                                                tint = Color(0xFF1E5D3B),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = "NEW ITEM TO ADD",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E5D3B),
                                                letterSpacing = 0.8.sp,
                                                fontSize = 10.5.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Oat Milk Barista Editi...",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurface,
                                            fontSize = 16.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Food thumbnail
                                        AsyncImage(
                                            model = "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=200&auto=format&fit=crop&q=80",
                                            contentDescription = "Oat Milk",
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(RoundedCornerShape(10.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Line 1: Qty & Location
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.Inventory2,
                                            contentDescription = null,
                                            tint = Color(0xFF706D65),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Qty: 2 cartons · Kitchen Fridge",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF55524A),
                                            fontSize = 13.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Line 2: Expiry
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.CalendarToday,
                                            contentDescription = null,
                                            tint = Color(0xFF706D65),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Expiry: Nov 4 (11 days safe)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF55524A),
                                            fontSize = 13.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Badges: Nutri-Score B, Eco-Score A, Pantry Slot A4
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFEBE8E1))
                                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "Nutri-Score B",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF44423E),
                                                fontSize = 11.sp
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFD4EBD9))
                                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "Eco-Score A",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1B5E38),
                                                fontSize = 11.sp
                                            )
                                        }

                                        Text(
                                            text = "Pantry Slot A4",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF7A7770),
                                            fontSize = 12.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Action buttons
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            onClick = {
                                                if (!oatMilkConfirmed) {
                                                    oatMilkConfirmed = true
                                                    // Insert into real pantry database
                                                    val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 11) }
                                                    viewModel.insertCustomPantryItem(
                                                        PantryItem(
                                                            name = "Oat Milk Barista Edition",
                                                            category = "Beverages & Dairy Alternatives",
                                                            packageSize = "1L carton",
                                                            barcode = "0711928471",
                                                            expiryDateMillis = cal.timeInMillis,
                                                            storageZone = "Kitchen Fridge",
                                                            quantity = 2,
                                                            unit = "cartons",
                                                            confidenceScore = 99
                                                        )
                                                    )
                                                    onMessage("Added 2 cartons of Oat Milk to Kitchen Fridge!")
                                                }
                                            },
                                            shape = RoundedCornerShape(20.dp),
                                            color = if (oatMilkConfirmed) Color(0xFF285E43) else Color.Black,
                                            modifier = Modifier.testTag("confirm_oat_milk_btn")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (oatMilkConfirmed) "Added to Fridge" else "Confirm & Add to Fridge",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }

                                        Surface(
                                            onClick = { onMessage("Editing Oat Milk specifications") },
                                            shape = RoundedCornerShape(20.dp),
                                            color = Color(0xFFEBE8E1),
                                            modifier = Modifier.testTag("edit_oat_milk_btn")
                                        ) {
                                            Text(
                                                text = "Edit",
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

                            Spacer(modifier = Modifier.height(14.dp))

                            // Item 2 Card: Artisan Sourdough Loaf (Inventory Update)
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF7F5EE)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Top Pill: ✂ INVENTORY UPDATE
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFFFECE5))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Outlined.CheckCircleOutline,
                                                contentDescription = null,
                                                tint = Color(0xFFC04B32),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = "INVENTORY UPDATE",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFC04B32),
                                                letterSpacing = 0.8.sp,
                                                fontSize = 10.5.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Artisan Sourdough L...",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurface,
                                            fontSize = 16.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Food thumbnail
                                        AsyncImage(
                                            model = "https://images.unsplash.com/photo-1589367920969-ab8e050bbb04?w=200&auto=format&fit=crop&q=80",
                                            contentDescription = "Sourdough Bread",
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(RoundedCornerShape(10.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Line 1: Action: Mark as Consumed / Finished
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.CheckCircleOutline,
                                            contentDescription = null,
                                            tint = Color(0xFF706D65),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Action: Mark as Consumed / Finished",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF55524A),
                                            fontSize = 13.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Line 2: Restock Trigger
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.ShoppingBag,
                                            contentDescription = null,
                                            tint = Color(0xFF706D65),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Restock Trigger: Added to Whole Foods list",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF55524A),
                                            fontSize = 13.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Confirm button
                                    Surface(
                                        onClick = {
                                            if (!sourdoughConfirmed) {
                                                sourdoughConfirmed = true
                                                onMessage("Artisan Sourdough marked consumed & queued in Whole Foods list!")
                                            }
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (sourdoughConfirmed) Color(0xFF285E43) else Color.Black,
                                        modifier = Modifier.testTag("confirm_sourdough_btn")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (sourdoughConfirmed) "Marked Finished" else "Confirm Mark Finished",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "08:42 AM · Buddy · Natural Parser v4.2",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedSlate,
                        fontSize = 11.5.sp
                    )
                }
            }

            // Dynamic User Interaction Stream (if user tapped suggested prompts or typed input)
            extraTranscripts.forEach { transcript ->
                Spacer(modifier = Modifier.height(18.dp))

                // User Bubble
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                            .shadow(2.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF181715)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Mic,
                                    contentDescription = null,
                                    tint = Color(0xFFCCCCCC),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "VOICE TRANSCRIPT",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFCCCCCC),
                                    letterSpacing = 1.sp,
                                    fontSize = 10.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "“${transcript.text}”",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${transcript.confidence} · ${transcript.time}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF285E43),
                        fontSize = 11.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Buddy Answer Bubble
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF222220)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Buddy",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, RoundedCornerShape(18.dp)),
                            shape = RoundedCornerShape(18.dp),
                            color = SurfaceWhite
                        ) {
                            Text(
                                text = transcript.buddyReply,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF2C2B27),
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${transcript.time} · Buddy",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedSlate,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 4. Suggested Prompts Section ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SUGGESTED PROMPTS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MutedSlate,
                    letterSpacing = 1.1.sp,
                    fontSize = 11.5.sp
                )

                Text(
                    text = "Tap to send",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF285E43),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clickable { onMessage("Tap any prompt below to auto-transcribe") }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Horizontal Scrolling Suggested Prompt Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Chip 1: What is expiring this weekend?
                Surface(
                    onClick = {
                        extraTranscripts.add(
                            DynamicTranscript(
                                id = "prompt_1",
                                text = "What is expiring this weekend?",
                                time = "08:43 AM",
                                confidence = "99.8% confidence",
                                buddyReply = "You have 2 items expiring this weekend: Whole Milk Greek Yogurt (expired yesterday in Fridge Door) and Baby Spinach (expires in 2 days). Would you like recipe ideas?"
                            )
                        )
                        scope.launch {
                            delay(100)
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF4F2EC),
                    modifier = Modifier.testTag("prompt_expiring_weekend")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WarningAmber,
                            contentDescription = null,
                            tint = Color(0xFFC04B32),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "What is expiring this weekend?",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface,
                            fontSize = 12.5.sp
                        )
                    }
                }

                // Chip 2: Add Paracetamol 500mg
                Surface(
                    onClick = {
                        extraTranscripts.add(
                            DynamicTranscript(
                                id = "prompt_2",
                                text = "Add Paracetamol 500mg to Bathroom Medicine Cabinet",
                                time = "08:44 AM",
                                confidence = "99.2% confidence",
                                buddyReply = "Added Paracetamol 500mg (20 tablets) to Bathroom Medicine Cabinet • Shelf B. Expiry set to Oct 2026."
                            )
                        )
                        scope.launch {
                            delay(100)
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF4F2EC),
                    modifier = Modifier.testTag("prompt_add_paracetamol")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AddCircleOutline,
                            contentDescription = null,
                            tint = Color(0xFF285E43),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add Paracetamol 500mg",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface,
                            fontSize = 12.5.sp
                        )
                    }
                }

                // Chip 3: Check Avocado Oil
                Surface(
                    onClick = {
                        extraTranscripts.add(
                            DynamicTranscript(
                                id = "prompt_3",
                                text = "How much Avocado Oil is remaining?",
                                time = "08:45 AM",
                                confidence = "99.5% confidence",
                                buddyReply = "Avocado Oil 500ml is at 10% capacity. It has been auto-queued to your Whole Foods shopping list."
                            )
                        )
                        scope.launch {
                            delay(100)
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF4F2EC),
                    modifier = Modifier.testTag("prompt_check_oil")
                ) {
                    Text(
                        text = "Check Avocado Oil level",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                        fontSize = 12.5.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // --- 5. Bottom Voice & Text Input Bar ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                color = SurfaceWhite
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Keyboard,
                        contentDescription = "Keyboard",
                        tint = MutedSlate,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    BasicTextField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_text_input"),
                        textStyle = TextStyle(
                            color = OnSurface,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (inputQuery.isEmpty()) {
                                Text(
                                    text = "Ask or tell Buddy anything...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF9E9B93),
                                    fontSize = 14.5.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Black pill button with soundwaves visualizer + Mic
                    Surface(
                        onClick = {
                            isLiveVoiceActive = !isLiveVoiceActive
                            onMessage(if (isLiveVoiceActive) "Voice listening active" else "Voice listening paused")
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black,
                        modifier = Modifier.testTag("quick_action_mic_pill_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            // Animated mini audio waveform bars
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.height(18.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(2.5.dp)
                                        .height(if (isLiveVoiceActive) waveHeight1.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(2.5.dp)
                                        .height(if (isLiveVoiceActive) waveHeight2.dp else 10.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(2.5.dp)
                                        .height(if (isLiveVoiceActive) waveHeight3.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = "Voice Active",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Upward send arrow button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEBE8E1))
                            .clickable {
                                if (inputQuery.isNotBlank()) {
                                    val queryText = inputQuery
                                    inputQuery = ""
                                    extraTranscripts.add(
                                        DynamicTranscript(
                                            id = "custom_${System.currentTimeMillis()}",
                                            text = queryText,
                                            time = "Just now",
                                            confidence = "100% parsed",
                                            buddyReply = "Understood. I processed '$queryText' for Sharma Home."
                                        )
                                    )
                                    scope.launch {
                                        delay(100)
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                }
                            }
                            .testTag("quick_action_send_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowUpward,
                            contentDescription = "Send",
                            tint = OnSurface,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-status line: Listening ambient stream... | Swipe down to pause
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.GraphicEq,
                        contentDescription = null,
                        tint = if (isLiveVoiceActive) Color(0xFF285E43) else MutedSlate,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isLiveVoiceActive) "Listening ambient stream..." else "Stream paused",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isLiveVoiceActive) Color(0xFF285E43) else MutedSlate,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = if (isLiveVoiceActive) "Swipe down to pause" else "Tap mic to resume",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedSlate,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
