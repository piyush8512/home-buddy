package com.example.buddy.ui.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.buddy.data.PantryItem
import com.example.buddy.ui.component.DetailTopBar
import com.example.buddy.ui.theme.BaseCanvas
import com.example.buddy.ui.theme.MutedSlate
import com.example.buddy.ui.theme.OnPrimary
import com.example.buddy.ui.theme.OnSurface
import com.example.buddy.ui.theme.OnSurfaceVariant
import com.example.buddy.ui.theme.OutlineVariant
import com.example.buddy.ui.theme.PrimaryDark
import com.example.buddy.ui.theme.SecondaryFixed
import com.example.buddy.ui.theme.SecondarySage
import com.example.buddy.ui.theme.SurfaceContainer
import com.example.buddy.ui.theme.SurfaceContainerHigh
import com.example.buddy.ui.theme.SurfaceContainerHighest
import com.example.buddy.ui.theme.SurfaceContainerLow
import com.example.buddy.ui.theme.SurfaceWhite
import com.example.buddy.ui.theme.TerracottaAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ItemDetailScreen(
    item: PantryItem,
    onBack: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onToggleFavorite: () -> Unit,
    onMarkAsConsumed: () -> Unit,
    onAddToRestockList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Expiry calculation
    val now = System.currentTimeMillis()
    val diffDays = ((item.expiryDateMillis - now) / (1000 * 60 * 60 * 24)).toInt()
    val isPast = diffDays < 0
    val expiryDateFormat = SimpleDateFormat("MMM d", Locale.US)
    val formattedExpiry = expiryDateFormat.format(Date(item.expiryDateMillis))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BaseCanvas)
    ) {

        DetailTopBar(
            title = "Item Details",
            onBackClick = onBack
        )

        // --- Scrollable Details Content ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // 1. Hero Product Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(24.dp))
                    .testTag("item_details_hero_card"),
                shape = RoundedCornerShape(24.dp),
                color = SurfaceWhite
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Large Image Box with Overlays
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(SurfaceContainerLow)
                    ) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Top-Left Category Badge: ● DAIRY & CULTURED
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = SurfaceWhite.copy(alpha = 0.95f),
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2D6A4F))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = extractCategoryBadge(item.category),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp,
                                    color = OnSurface,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Top-Right Favorite Heart Icon
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SurfaceWhite.copy(alpha = 0.95f))
                                .clickable { onToggleFavorite() }
                                .testTag("toggle_favorite_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (item.isFavorite) TerracottaAccent else OnSurface,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Brand & Temperature Classification Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val brand = item.name.split(" ").firstOrNull() ?: "CHOBANI"
                        Text(
                            text = "${brand.uppercase()} • ${item.packageSize.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MutedSlate,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEAF5EE))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Refrigerated",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF2D6A4F),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Item Headline
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        lineHeight = 28.sp,
                        fontSize = 22.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Subtitle
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate,
                        lineHeight = 18.sp,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Freshness Window Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceContainerLow
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerHighest),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = null,
                                tint = OnSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Freshness Window",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedSlate,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val freshnessText = buildAnnotatedString {
                                append("Best before $formattedExpiry • ")
                                if (isPast) {
                                    withStyle(SpanStyle(color = TerracottaAccent, fontWeight = FontWeight.Bold)) {
                                        append("${Math.abs(diffDays)} day${if (Math.abs(diffDays) > 1) "s" else ""} past")
                                    }
                                } else {
                                    withStyle(SpanStyle(color = Color(0xFF2D6A4F), fontWeight = FontWeight.Bold)) {
                                        append("$diffDays days safe")
                                    }
                                }
                            }
                            Text(
                                text = freshnessText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = OnSurface,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceContainerHighest)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Sniff check ok",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Nutritional & Eco Profile Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NUTRITIONAL & ECO PROFILE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = OnSurfaceVariant,
                    fontSize = 11.sp
                )
                Text(
                    text = "Verified Source",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedSlate,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Two Cards Side-by-Side: Nutri-Score & Eco Impact
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Nutri-Score Card
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(1.dp, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    color = SurfaceWhite
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Nutri-Score",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface,
                                fontSize = 12.sp
                            )
                            Icon(
                                imageVector = Icons.Outlined.Verified,
                                contentDescription = "Verified",
                                tint = Color(0xFF2D6A4F),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFC8EAD8)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.nutriScore,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF012116)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Excellent\nbalance",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface,
                                lineHeight = 15.sp,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress Score Track
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(SurfaceContainerHighest)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.72f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFF2D4B3E))
                            )
                        }
                    }
                }

                // Eco Impact Card
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(1.dp, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    color = SurfaceWhite
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Eco Impact",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface,
                                fontSize = 12.sp
                            )
                            Icon(
                                imageVector = Icons.Outlined.Spa,
                                contentDescription = "Eco",
                                tint = OnSurface,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.ecoImpact,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1.8 kg CO₂e /\nkg",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface,
                                lineHeight = 15.sp,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Tub #5 PP • Recyclable",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2D6A4F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Nutritional Macros Row Card
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
                        .padding(horizontal = 14.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MacroColumn(value = "${item.calories}", unit = "", label = "Calories")
                    MacroColumn(value = item.protein.replace("g", ""), unit = "g", label = "Protein")
                    MacroColumn(value = item.carbs.replace("g", ""), unit = "g", label = "Carbs")
                    MacroColumn(value = item.fat.replace("g", ""), unit = "g", label = "Total Fat")
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Storage & Quantity Section
            Text(
                text = "STORAGE & QUANTITY",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = OnSurfaceVariant,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceWhite
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerLow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Kitchen,
                                contentDescription = null,
                                tint = OnSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Kitchen Fridge",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.shelfLocation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedSlate,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Stepper Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceContainerLow)
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .clickable { onQuantityChange(maxOf(1, item.quantity - 1)) }
                                .testTag("details_qty_minus"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Remove,
                                contentDescription = "Decrease",
                                tint = OnSurface,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Text(
                            text = "${item.quantity}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            modifier = Modifier.padding(horizontal = 12.dp),
                            fontSize = 15.sp
                        )

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .clickable { onQuantityChange(item.quantity + 1) }
                                .testTag("details_qty_plus"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = "Increase",
                                tint = OnSurface,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Purchase History Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PURCHASE HISTORY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = OnSurfaceVariant,
                    fontSize = 11.sp
                )
                Text(
                    text = "Target • Sep 28",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedSlate,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceWhite
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Top Price & Trend Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", item.price)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "current unit",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedSlate,
                                modifier = Modifier.padding(bottom = 3.dp),
                                fontSize = 12.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.TrendingUp,
                                contentDescription = null,
                                tint = TerracottaAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "+$0.50 (9.1%)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TerracottaAccent,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Smooth Canvas Price Trend Curve
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        val w = size.width
                        val h = size.height

                        // 3 Key points: Jun ($5.49), Aug ($5.49), Sep/Last ($5.99)
                        val p1X = w * 0.08f
                        val p1Y = h * 0.82f

                        val p2X = w * 0.50f
                        val p2Y = h * 0.74f

                        val p3X = w * 0.94f
                        val p3Y = h * 0.16f

                        // Curve Path
                        val linePath = Path().apply {
                            moveTo(p1X, p1Y)
                            // Smooth bezier from P1 to P2
                            val cx1 = (p1X + p2X) / 2
                            cubicTo(cx1, p1Y, cx1, p2Y, p2X, p2Y)
                            // Smooth bezier from P2 to P3
                            val cx2 = (p2X + p3X) / 2
                            cubicTo(cx2, p2Y, cx2, p3Y, p3X, p3Y)
                        }

                        // Gradient fill path
                        val fillPath = Path().apply {
                            addPath(linePath)
                            lineTo(p3X, h)
                            lineTo(p1X, h)
                            close()
                        }

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF2D6A4F).copy(alpha = 0.20f),
                                    Color(0xFF2D6A4F).copy(alpha = 0.01f)
                                ),
                                startY = p3Y,
                                endY = h
                            ),
                            style = Fill
                        )

                        // Draw Curve Stroke
                        drawPath(
                            path = linePath,
                            color = Color(0xFF2D6A4F),
                            style = Stroke(width = 2.5.dp.toPx())
                        )

                        // Draw Dots
                        drawCircle(color = Color(0xFF2D6A4F), radius = 3.5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(p1X, p1Y))
                        drawCircle(color = Color.White, radius = 2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(p1X, p1Y))

                        drawCircle(color = Color(0xFF2D6A4F), radius = 3.5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(p2X, p2Y))
                        drawCircle(color = Color.White, radius = 2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(p2X, p2Y))

                        // Highlighted End Dot
                        drawCircle(color = Color(0xFF010102), radius = 4.5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(p3X, p3Y))
                        drawCircle(color = Color(0xFFC8EAD8), radius = 2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(p3X, p3Y))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Timeline Labels Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Jun ($5.49)", style = MaterialTheme.typography.labelSmall, color = MutedSlate, fontSize = 11.sp)
                        Text("Aug ($5.49)", style = MaterialTheme.typography.labelSmall, color = MutedSlate, fontSize = 11.sp)
                        Text("Last ($5.99)", style = MaterialTheme.typography.labelSmall, color = OnSurface, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Action Buttons
            Button(
                onClick = onMarkAsConsumed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("mark_consumed_btn"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (item.isConsumed) "Mark as Active" else "Mark as Consumed",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onAddToRestockList,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("add_to_restock_btn"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, OutlineVariant)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = null,
                        tint = OnSurface,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add to Restock List",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun MacroColumn(value: String, unit: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                fontSize = 24.sp
            )
            if (unit.isNotEmpty()) {
                Text(
                    text = unit,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    modifier = Modifier.padding(bottom = 3.dp),
                    fontSize = 14.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MutedSlate,
            fontSize = 11.sp
        )
    }
}

private fun extractCategoryBadge(category: String): String {
    return if (category.contains("•")) {
        category.substringAfter("•").trim().uppercase()
    } else {
        category.uppercase()
    }
}
