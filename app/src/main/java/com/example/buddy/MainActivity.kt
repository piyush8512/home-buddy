package com.example.buddy

import android.graphics.Color
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.buddy.data.PantryItem

import androidx.compose.ui.platform.LocalContext

import com.example.buddy.ui.NavTab
import com.example.buddy.ui.PantryViewModel
import com.example.buddy.ui.components.PantryBottomNav
import com.example.buddy.ui.components.PantryTopBar
import com.example.buddy.ui.dialogs.AIAssistantDialog
import com.example.buddy.ui.dialogs.DatePickerModal
import com.example.buddy.ui.dialogs.EditItemDialog
import com.example.buddy.ui.dialogs.VerifyOcrDialog

import com.example.buddy.ui.screens.AccountProfileScreen
import com.example.buddy.ui.screens.HomeScreen
import com.example.buddy.ui.screens.HouseholdScreen
import com.example.buddy.ui.screens.InventoryScreen
import com.example.buddy.ui.screens.ItemDetailScreen
import com.example.buddy.ui.screens.ListsScreen
import com.example.buddy.ui.screens.NotificationsScreen
import com.example.buddy.ui.screens.QuickActionVoiceSyncScreen
import com.example.buddy.ui.screens.StorageZonesPlacesScreen
import com.example.buddy.ui.screens.VerifyScannedItemScreen

import com.example.buddy.ui.theme.BaseCanvas
import com.example.buddy.ui.theme.HomeBuddyTheme

import com.example.buddy.ui.screens.LoginScreen
import com.example.buddy.viewmodel.AuthState
import com.example.buddy.viewmodel.AuthViewModel

import kotlinx.coroutines.launch
import android.app.Activity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            HomeBuddyTheme {
                val authViewModel: AuthViewModel = viewModel()
                val context = LocalContext.current
                val activity = context as Activity
                val authState by authViewModel.authState.collectAsState()
                when (authState) {
                    AuthState.Idle,
                    AuthState.Loading,
                    is AuthState.Error -> {

//                        LoginScreen(
//                            authState = authState,
//                            onGoogleSignIn = {
//                                authViewModel.signInWithGoogle(activity)
//                            }
//                        )
                        BuddyApp()
                    }

                    AuthState.Success -> {
                        BuddyApp()
//                        LoginScreen(
//                            authState = authState,
//                            onGoogleSignIn = {
//                                authViewModel.signInWithGoogle(activity)
//                            }
//                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BuddyApp() {

    val viewModel: PantryViewModel = viewModel()
    val selectedTab by viewModel.selectedBottomNav
        .collectAsStateWithLifecycle()

    val showAIAssistant by viewModel.showAIAssistant
        .collectAsStateWithLifecycle()

    val showEditItem by viewModel.showEditItem
        .collectAsStateWithLifecycle()

    val showVerifyOcr by viewModel.showVerifyOcr
        .collectAsStateWithLifecycle()

    val showDatePicker by viewModel.showDatePicker
        .collectAsStateWithLifecycle()

    val showVerifyScannedItem by viewModel.showVerifyScannedItem
        .collectAsStateWithLifecycle()

    val scannedItem by viewModel.scannedItem
        .collectAsStateWithLifecycle()


    var currentSpace by remember {
        mutableStateOf("Home")
    }


    var selectedItem by remember {
        mutableStateOf<PantryItem?>(null)
    }


    var showProfile by remember {
        mutableStateOf(false)
    }


    var showQuickAction by remember {
        mutableStateOf(false)
    }


    var showNotification by remember {
        mutableStateOf(false)
    }


    var showStorage by remember {
        mutableStateOf(false)
    }


    BackHandler(enabled = showProfile) {
        showProfile = false
    }


    BackHandler(enabled = showStorage) {
        showStorage = false
    }


    BackHandler(enabled = showNotification) {
        showNotification = false
    }


    val showMainBars =
        selectedItem == null &&
                !showProfile &&
                !showStorage &&
                !showNotification &&
                selectedTab != NavTab.SCAN


    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()


    Scaffold(

        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets.statusBars
            ),

        containerColor = BaseCanvas,

        topBar = {
            if (showMainBars) {
                PantryTopBar(

                    currentSpace = currentSpace,

                    onSpaceSelected = { selectedSpace ->
                        currentSpace = selectedSpace
                    },

                    onNotificationClick = {
                        showNotification = true
                    },

                    onProfileClick = {
                        showProfile = true
                    }
                )
            }
        },

        bottomBar = {
            if (showMainBars) {
                PantryBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = {
                        viewModel.setBottomNav(it)
                    }
                )
            }
        },


        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (showProfile) {
                AccountProfileScreen(
                    onBackClick = {
                        showProfile = false
                    }
                )
            }

            else if (showStorage) {
                StorageZonesPlacesScreen(
                    onBackClick = {
                        showStorage = false
                    }
                )
            }

            else if (showQuickAction) {
                StorageZonesPlacesScreen(
                    onBackClick = {
                        showQuickAction = false
                    }
                )
            }

            else if (showVerifyScannedItem) {
                VerifyScannedItemScreen(
                    onBackClick = {
                        viewModel.setShowVerifyScannedItem(false)
                    },

                    onItemSaved = { pantryItem ->
                        viewModel.insertCustomPantryItem(
                            pantryItem
                        )
                        viewModel.setShowVerifyScannedItem(false)
                    }
                )
            }

            else if (showNotification) {
                NotificationsScreen(
                    onBackClick = {
                        showNotification = false
                    }
                )
            }

            else if (selectedItem != null) {
                ItemDetailScreen(
                    item = selectedItem!!,
                    onBack = {
                        selectedItem = null
                    },

                    onQuantityChange = { quantity ->
                        // TODO: update quantity
                    },

                    onToggleFavorite = {
                        // TODO: togglefavorite
                    },

                    onMarkAsConsumed = {
                        // TODO: mark item as consumed
                    },

                    onAddToRestockList = {
                        // TODO: add item to restock list
                    }
                )
            }

            else {
                when (selectedTab) {
                    NavTab.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onPriorityClick = { item ->
                                selectedItem = item
                            }
                        )
                    }

                    NavTab.INVENTORY -> {
                        InventoryScreen(
                            viewModel = viewModel
                        )
                    }

                    NavTab.SCAN -> {
                        QuickActionVoiceSyncScreen(
                            viewModel = viewModel,
                            onOcrScanClick = {
                                viewModel.setShowVerifyScannedItem(
                                    true
                                )
                            },

                            onBackClick = {
                                viewModel.setBottomNav(
                                    NavTab.HOME
                                )
                            }
                        )
                    }

                    NavTab.LISTS -> {
                        ListsScreen()
                    }

                    NavTab.HOUSEHOLD -> {
                        HouseholdScreen(
                            onAddZoneClick = {
                                showStorage = true
                            }
                        )
                    }
                }
            }
        }

        if (showAIAssistant) {
            AIAssistantDialog(
                onDismiss = {
                    viewModel.setShowAIAssistant(false)
                },
                itemName = scannedItem.title
            )
        }


        if (showEditItem) {
            EditItemDialog(
                itemState = scannedItem,
                onDismiss = {
                    viewModel.setShowEditItem(false)
                },

                onSave = {
                        title,
                        category,
                        packageSize,
                        storageZone ->

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
                onDismiss = {
                    viewModel.setShowVerifyOcr(false)
                }
            )
        }

        if (showDatePicker) {
            DatePickerModal(
                initialDateMillis = scannedItem.expiryMillis,
                onDateSelected = { selectedMillis ->
                    viewModel.setExpiryDateMillis(
                        selectedMillis
                    )
                },
                onDismiss = {
                    viewModel.setShowDatePicker(false)
                }
            )
        }
    }
}