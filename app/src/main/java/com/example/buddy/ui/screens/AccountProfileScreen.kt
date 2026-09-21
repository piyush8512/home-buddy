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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.buddy.ui.theme.BorderHairline
import com.example.buddy.ui.theme.MutedSlate
import com.example.buddy.ui.theme.OnSurface
import com.example.buddy.ui.theme.OnSurfaceVariant
import com.example.buddy.ui.theme.PrimaryDark
import com.example.buddy.ui.theme.SurfaceWhite

@Composable
fun AccountProfileScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    var showHouseholdDropdown by remember { mutableStateOf(false) }
    var currentHousehold by remember { mutableStateOf("Household") }

    // Toggle Preferences States
    var nutriEcoScoreEnabled by remember { mutableStateOf(true) }
    var hapticFeedbackEnabled by remember { mutableStateOf(true) }
    var biometricLockEnabled by remember { mutableStateOf(true) }

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
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1EFEA))
                    .clickable { onBackClick() }
                    .testTag("account_back_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OnSurface,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "Account & Profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                fontSize = 19.sp
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1EFEA))
                    .clickable { }
                    .testTag("edit_profile_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Profile",
                    tint = OnSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- 3. User Hero Card: Avatar with active dot, Name, Admin Badge, Email, Plan Banner ---
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
                // User Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(64.dp)) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
                            contentDescription = "Priya Sharma",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        // Online indicator dot
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF236544))
                                .border(2.dp, Color.White, CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Priya Sharma",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFD4EEDF))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Admin",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E5D3B),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "priya.sharma@example.com",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedSlate,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Family Unlimited Plan Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF6F4F0)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE5ECE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.WorkspacePremium,
                                contentDescription = null,
                                tint = Color(0xFF236544),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Family Unlimited",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Renews Oct 2026",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedSlate,
                                fontSize = 12.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFE5E2DC))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 4. Section: HOUSEHOLD & SYNC ---
        Text(
            text = "HOUSEHOLD & SYNC",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = MutedSlate,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            color = SurfaceWhite
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)
            ) {
                // Active Household
                SettingsActionRow(
                    icon = Icons.Outlined.Group,
                    title = "Active Household",
                    subtitle = "Sharma Home (4 members)",
                    onClick = { }
                )

                SettingsDivider()

                // Sync Engine
                SettingsActionRow(
                    icon = Icons.Outlined.Sync,
                    title = "Sync Engine",
                    subtitle = "Offline-First & iCloud (Up to date)",
                    hasGreenDot = true,
                    onClick = { }
                )

                SettingsDivider()

                // Data Export & Backup
                SettingsActionRow(
                    icon = Icons.Outlined.CloudDownload,
                    title = "Data Export & Backup",
                    subtitle = "JSON / CSV",
                    onClick = { }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 5. Section: INVENTORY & SCANNER PREFERENCES ---
        Text(
            text = "INVENTORY & SCANNER PREFERENCES",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = MutedSlate,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            color = SurfaceWhite
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)
            ) {
                // Default Expiry Warning
                SettingsActionRow(
                    icon = Icons.Outlined.NotificationsActive,
                    title = "Default Expiry Warning",
                    trailingValue = "7 days before",
                    onClick = { }
                )

                SettingsDivider()

                // Nutri-Score & Eco-Score Switch
                SettingsSwitchRow(
                    icon = Icons.Outlined.Spa,
                    title = "Nutri-Score & Eco-Score",
                    subtitle = "Open Food Facts data",
                    checked = nutriEcoScoreEnabled,
                    onCheckedChange = { nutriEcoScoreEnabled = it }
                )

                SettingsDivider()

                // Currency & Units
                SettingsActionRow(
                    icon = Icons.Outlined.Tune,
                    title = "Currency & Units",
                    trailingValue = "USD ($), Metric (g, ml)",
                    onClick = { }
                )

                SettingsDivider()

                // Haptic Feedback Switch
                SettingsSwitchRow(
                    icon = Icons.Outlined.Vibration,
                    title = "Haptic Feedback",
                    checked = hapticFeedbackEnabled,
                    onCheckedChange = { hapticFeedbackEnabled = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 6. Section: SECURITY & APP ---
        Text(
            text = "SECURITY & APP",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = MutedSlate,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            color = SurfaceWhite
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)
            ) {
                // Face ID / Passcode lock
                SettingsSwitchRow(
                    icon = Icons.Outlined.Fingerprint,
                    title = "Face ID / Passcode lock",
                    checked = biometricLockEnabled,
                    onCheckedChange = { biometricLockEnabled = it }
                )

                SettingsDivider()

                // End-to-End Encryption
                SettingsActionRow(
                    icon = Icons.Outlined.Shield,
                    title = "End-to-End Encryption",
                    subtitle = "Vault Key Verified",
                    onClick = { }
                )

                SettingsDivider()

                // App Version
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF3F1EC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = OnSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = "App Version",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "v2.4.0 (Build 384)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- 7. Log Out Button & Keychain Footer ---
        Surface(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("logout_button"),
            shape = RoundedCornerShape(26.dp),
            color = Color(0xFFFFECE7)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = "Log Out",
                    tint = Color(0xFFC04B32),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log Out",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC04B32),
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Logged in via iCloud keychain",
            style = MaterialTheme.typography.labelSmall,
            color = MutedSlate,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailingValue: String? = null,
    hasGreenDot: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF3F1EC)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OnSurface,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface,
                fontSize = 14.sp
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (hasGreenDot) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF236544))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate,
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (trailingValue != null) {
            Text(
                text = trailingValue,
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MutedSlate,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF3F1EC)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OnSurface,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface,
                fontSize = 14.sp
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSlate,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

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
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFF4F2ED))
    )
}
