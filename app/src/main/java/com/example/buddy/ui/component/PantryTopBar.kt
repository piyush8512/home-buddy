package com.example.buddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.buddy.ui.theme.BaseCanvas
import com.example.buddy.ui.theme.OnSurface
import com.example.buddy.ui.theme.OnSurfaceVariant
import com.example.buddy.ui.theme.SecondarySage
import com.example.buddy.ui.theme.SurfaceContainer

import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue


@Composable
fun PantryTopBar(
    currentSpace: String = "Home",
    onSpaceSelected: (String) -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {

    var isLocationMenuExpanded by remember {
        mutableStateOf(false)
    }

    // Available locations
    val space = listOf(
        "Home",
        "Office",
        "Other"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BaseCanvas)
            .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
//            Text(
//                text = currentSpace,
//                style = MaterialTheme.typography.headlineMedium,
//                color = OnSurface,
//                fontWeight = FontWeight.SemiBold
//            )

            Box {
                Row(
                    modifier = Modifier
                        .clickable {
                            isLocationMenuExpanded = true
                        }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentSpace,
                        color = OnSurfaceVariant,
                        fontSize = 20.sp
                    )
                    // Down arrow
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Select Space",
                        tint = OnSurfaceVariant,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(start = 2.dp)
                    )
                }

                // DROPDOWN MENU
                DropdownMenu(
                    expanded = isLocationMenuExpanded,
                    onDismissRequest = {
                        isLocationMenuExpanded = false
                    }
                ) {
                    space.forEach { location ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = location
                                )
                            },
                            onClick = {
                                onSpaceSelected(location)
                                isLocationMenuExpanded = false
                            }
                        )
                    }
                }
            }

        }

        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(SurfaceContainer)
                .testTag("notification_button")
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = OnSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(15.dp))

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(SurfaceContainer)
        ) {
            AsyncImage(
                model = "https://randomuser.me/api/portraits/men/75.jpg",
                contentDescription = "User Profile Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            )
        }
    }
}
