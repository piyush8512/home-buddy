package com.example.buddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buddy.ui.NavTab
import com.example.buddy.ui.PantryViewModel
import com.example.buddy.ui.components.PantryBottomNav
import com.example.buddy.ui.components.PantryTopBar
import com.example.buddy.ui.dialogs.AIAssistantDialog
import com.example.buddy.ui.dialogs.DatePickerModal
import com.example.buddy.ui.dialogs.EditItemDialog
import com.example.buddy.ui.dialogs.VerifyOcrDialog
import com.example.buddy.ui.screens.HomeScreen
import com.example.buddy.ui.screens.HouseholdScreen
import com.example.buddy.ui.screens.InventoryScreen
import com.example.buddy.ui.screens.ListsScreen
import com.example.buddy.ui.screens.ScanScreen
import com.example.buddy.ui.theme.BaseCanvas
import com.example.buddy.ui.theme.HomeBuddyTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeBuddyTheme {
                val viewModel: PantryViewModel = viewModel()
                val selectedTab by viewModel.selectedBottomNav.collectAsStateWithLifecycle()
                val showAIAssistant by viewModel.showAIAssistant.collectAsStateWithLifecycle()
                val showEditItem by viewModel.showEditItem.collectAsStateWithLifecycle()
                val showVerifyOcr by viewModel.showVerifyOcr.collectAsStateWithLifecycle()
                val showDatePicker by viewModel.showDatePicker.collectAsStateWithLifecycle()
                val scannedItem by viewModel.scannedItem.collectAsStateWithLifecycle()
                var currentSpace by remember {mutableStateOf("Home")
                }


                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars),
                    containerColor = BaseCanvas,
                    topBar = {
                        PantryTopBar(
                            currentSpace = currentSpace,
                            onSpaceSelected = { selectedSpace ->
                                currentSpace = selectedSpace
                            },
                            onNotificationClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("All items in pantry are currently monitored.")
                                }
                            }
                        )
                    },
                    bottomBar = {
                        PantryBottomNav(
                            selectedTab = selectedTab,
                            onTabSelected = { viewModel.setBottomNav(it) }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTab) {
                            NavTab.HOME -> HomeScreen(viewModel = viewModel)
                            NavTab.INVENTORY -> InventoryScreen(viewModel = viewModel)
                            NavTab.SCAN -> ScanScreen(
                                viewModel = viewModel,
                                onBackClick = { viewModel.setBottomNav(NavTab.HOME) }
                            )
                            NavTab.LISTS -> ListsScreen()
                            NavTab.HOUSEHOLD -> HouseholdScreen()
                        }
                    }
                }

                // Modal Dialogs & Sheets
                if (showAIAssistant) {
                    AIAssistantDialog(
                        onDismiss = { viewModel.setShowAIAssistant(false) },
                        itemName = scannedItem.title
                    )
                }

                if (showEditItem) {
                    EditItemDialog(
                        itemState = scannedItem,
                        onDismiss = { viewModel.setShowEditItem(false) },
                        onSave = { title, category, packageSize, storageZone ->
                            viewModel.updateScannedItem(
                                title = title,
                                category = category,
                                packageSize = packageSize,
                                storageZone = storageZone
                            )
                        }
                    )
                }

                if (showVerifyOcr) {
                    VerifyOcrDialog(
                        onDismiss = { viewModel.setShowVerifyOcr(false) }
                    )
                }

                if (showDatePicker) {
                    DatePickerModal(
                        initialDateMillis = scannedItem.expiryMillis,
                        onDateSelected = { selectedMillis ->
                            viewModel.setExpiryDateMillis(selectedMillis)
                        },
                        onDismiss = { viewModel.setShowDatePicker(false) }
                    )
                }
            }
        }
    }
}
