package com.example.buddy.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.buddy.data.PantryDatabase
import com.example.buddy.data.PantryItem
import com.example.buddy.data.PantryRepository
import com.example.buddy.data.ShoppingItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class ScanMode(val label: String) {
    AUTO_OCR("Auto OCR"),
    BARCODE_ONLY("Barcode Only"),
    VOICE_CHAT("Voice & Chat")
}

sealed class SaveState {
    object Idle : SaveState()
    object Saving : SaveState()
    object Saved : SaveState()
}

data class ScannedItemState(
    val title: String = "Chobani Greek Yogurt",
    val category: String = "Food & Pantry • Dairy & Cultured",
    val packageSize: String = "907g tub",
    val barcode: String = "0125460012",
    val confidence: Int = 96,
    val dateConfidence: Int = 98,
    val expiryMillis: Long = getDefaultExpiryMillis(),
    val storageZone: String = "Fridge Door",
    val quantity: Int = 1,
    val unit: String = "tub",
    val imageUrl: String = "https://lh3.googleusercontent.com/aida-public/AB6AXuAFIqQTH2gNzVof-4dV8MHAzTEr1jgA4AL6rVpEQeuzO1PIaUQ0vo1daIZEg_6IT3HSCqDoe-V-h_PniE8YsUQ7SNucWMmWrk-UCC3UQxM-nqyFZeeN5SeRdEb1EconuL-os6BvhVk9Hq7T5cmWYr7iQwlpMzvKVSWqpW28PxNPiE4H_oFgFC2x2P3_Gr_vhLVEnGZwnpDwUI26J2TB4KL3AIJOMgoZhEnlQmrJSGhT1LxKXJZf8YijNg"
)

private fun getDefaultExpiryMillis(): Long {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, 21)
    return cal.timeInMillis
}

class PantryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PantryRepository

    init {
        val dao = PantryDatabase.getDatabase(application).pantryDao()
        repository = PantryRepository(dao)
        seedSampleDataIfNeeded()
    }

    val pantryItems: StateFlow<List<PantryItem>> = repository.allActiveItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _scannedItem = MutableStateFlow(ScannedItemState())
    val scannedItem: StateFlow<ScannedItemState> = _scannedItem.asStateFlow()

    private val _scanMode = MutableStateFlow(ScanMode.AUTO_OCR)
    val scanMode: StateFlow<ScanMode> = _scanMode.asStateFlow()

    private val _isTorchOn = MutableStateFlow(false)
    val isTorchOn: StateFlow<Boolean> = _isTorchOn.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private val _selectedBottomNav = MutableStateFlow(NavTab.HOME)
    val selectedBottomNav: StateFlow<NavTab> = _selectedBottomNav.asStateFlow()




    // Dialogs
    private val _showAIAssistant = MutableStateFlow(false)
    val showAIAssistant: StateFlow<Boolean> = _showAIAssistant.asStateFlow()

    private val _showEditItem = MutableStateFlow(false)
    val showEditItem: StateFlow<Boolean> = _showEditItem.asStateFlow()

    private val _showVerifyOcr = MutableStateFlow(false)
    val showVerifyOcr: StateFlow<Boolean> = _showVerifyOcr.asStateFlow()

    private val _showDatePicker = MutableStateFlow(false)
    val showDatePicker: StateFlow<Boolean> = _showDatePicker.asStateFlow()
    private val _selectedStoreFilter = MutableStateFlow("All Stores")
    val selectedStoreFilter: StateFlow<String> = _selectedStoreFilter.asStateFlow()

    fun setStoreFilter(store: String) {
        _selectedStoreFilter.value = store
    }

    fun insertCustomPantryItem(item: PantryItem) {
        viewModelScope.launch {
            repository.insertItem(item)
        }
    }


    fun completeTripAndUpdateStock(): Int {
        val checkedItems = _restockItems.value.filter { it.isChecked }
        // Uncheck or remove, reset replenishment dates in inventory
        _restockItems.value = _restockItems.value.filter { !it.isChecked }
        return checkedItems.size
    }

    private val _restockItems = MutableStateFlow<List<ShoppingItem>>(
        listOf(
            ShoppingItem(
                id = 1,
                name = "Avocado Oil 500ml",
                subtitle = "Pantry Shelf • Whole Foods",
                category = "Oils & Vinegars",
                store = "Whole Foods",
                isAutoDepleted = true,
                depletionPercent = 10,
                isChecked = false,
                price = 11.49,
                imageUrl = "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=200&auto=format&fit=crop&q=80"
            ),
            ShoppingItem(
                id = 2,
                name = "Chobani Greek Yogurt",
                subtitle = "Fridge • Top shelf",
                category = "Dairy & Cultured",
                store = "Whole Foods",
                isAutoDepleted = true,
                depletionPercent = 0,
                isChecked = false,
                price = 5.99,
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBsbBccmV_cpkMZikRBOjJEne4xF0LGtBf67A_YLRPToheRwa19EVxEJaMFS7ueVyWz7jlZHsdwmw7IsYBqRmi8_0IERQYj_ND9IFs_hhvzJ0iE_eOyIQ2-fPl5Gl0cj4HsyAuXDokAHOwPjSpsDkADVYjKF_dyl8AYkP8H3a5lLb-CCmThH-MocVA5549_0XEPkvTdbghA0GgCMMjaAOMIhJtQDvN3j7EUMiwtoODy2B7hdUtzZrWhMg"
            ),
            ShoppingItem(
                id = 3,
                name = "Organic Bananas",
                subtitle = "$2.49 • In cart",
                category = "Produce",
                store = "Whole Foods",
                isAutoDepleted = false,
                isChecked = true,
                price = 2.49,
                addedByInitial = "A",
                imageUrl = "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=200&auto=format&fit=crop&q=80"
            ),
            ShoppingItem(
                id = 4,
                name = "Artisan Sourdough",
                subtitle = "$6.20 • In cart",
                category = "Bakery",
                store = "Whole Foods",
                isAutoDepleted = false,
                isChecked = true,
                price = 6.20,
                addedByInitial = "M",
                imageUrl = "https://images.unsplash.com/photo-1589367920969-ab8e050bbb04?w=200&auto=format&fit=crop&q=80"
            )
        )
    )
    val restockItems: StateFlow<List<ShoppingItem>> = _restockItems.asStateFlow()

    fun addToRestockList(item: PantryItem) {
        val current = _restockItems.value
        val nextId = (current.maxOfOrNull { it.id } ?: 0) + 1
        val itemTitle = if (item.packageSize.isNotEmpty()) "${item.name} (${item.packageSize})" else item.name
        val newItem = ShoppingItem(
            id = nextId,
            name = itemTitle,
            category = if (item.category.contains("•")) item.category.substringAfter("•").trim() else item.category
        )
        _restockItems.value = listOf(newItem) + current
    }

    fun toggleRestockItem(itemId: Int) {
        _restockItems.value = _restockItems.value.map {
            if (it.id == itemId) it.copy(isChecked = !it.isChecked) else it
        }
    }

    fun addCustomRestockItem(name: String, category: String) {
        if (name.isBlank()) return
        val current = _restockItems.value
        val nextId = (current.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = ShoppingItem(id = nextId, name = name, category = category)
        _restockItems.value = listOf(newItem) + current
    }

    fun deleteRestockItem(itemId: Int) {
        _restockItems.value = _restockItems.value.filter { it.id != itemId }
    }

    fun setBottomNav(tab: NavTab) {
        _selectedBottomNav.value = tab
    }

    fun setScanMode(mode: ScanMode) {
        _scanMode.value = mode
    }

    fun toggleTorch() {
        _isTorchOn.value = !_isTorchOn.value
    }

    fun setShowAIAssistant(show: Boolean) {
        _showAIAssistant.value = show
    }

    fun setShowEditItem(show: Boolean) {
        _showEditItem.value = show
    }

    fun setShowVerifyOcr(show: Boolean) {
        _showVerifyOcr.value = show
    }

    fun setShowDatePicker(show: Boolean) {
        _showDatePicker.value = show
    }

    fun updateScannedItem(
        title: String? = null,
        category: String? = null,
        packageSize: String? = null,
        storageZone: String? = null,
        quantity: Int? = null,
        expiryMillis: Long? = null
    ) {
        _scannedItem.value = _scannedItem.value.copy(
            title = title ?: _scannedItem.value.title,
            category = category ?: _scannedItem.value.category,
            packageSize = packageSize ?: _scannedItem.value.packageSize,
            storageZone = storageZone ?: _scannedItem.value.storageZone,
            quantity = quantity ?: _scannedItem.value.quantity,
            expiryMillis = expiryMillis ?: _scannedItem.value.expiryMillis
        )
    }

    fun adjustExpiryDays(days: Int) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = _scannedItem.value.expiryMillis
        cal.add(Calendar.DAY_OF_YEAR, days)
        _scannedItem.value = _scannedItem.value.copy(expiryMillis = cal.timeInMillis)
    }

    fun setExpiryDateMillis(millis: Long) {
        _scannedItem.value = _scannedItem.value.copy(expiryMillis = millis)
    }

    fun incrementQuantity() {
        _scannedItem.value = _scannedItem.value.copy(quantity = _scannedItem.value.quantity + 1)
    }

    fun decrementQuantity() {
        if (_scannedItem.value.quantity > 1) {
            _scannedItem.value = _scannedItem.value.copy(quantity = _scannedItem.value.quantity - 1)
        }
    }

    fun setStorageZone(zone: String) {
        _scannedItem.value = _scannedItem.value.copy(storageZone = zone)
    }

    fun confirmAndAddToPantry() {
        viewModelScope.launch {
            _saveState.value = SaveState.Saving
            delay(500)

            val item = _scannedItem.value
            val entity = PantryItem(
                name = item.title,
                category = item.category,
                packageSize = item.packageSize,
                barcode = item.barcode,
                expiryDateMillis = item.expiryMillis,
                storageZone = item.storageZone,
                quantity = item.quantity,
                unit = item.unit,
                imageUrl = item.imageUrl,
                confidenceScore = item.confidence
            )
            repository.insertItem(entity)
            _saveState.value = SaveState.Saved
            delay(1500)
            _saveState.value = SaveState.Idle
        }
    }

    fun deleteItem(item: PantryItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun toggleItemConsumed(item: PantryItem) {
        viewModelScope.launch {
            repository.updateItem(item.copy(isConsumed = !item.isConsumed))
        }
    }

    private fun seedSampleDataIfNeeded() {
        viewModelScope.launch {
            val existing = repository.allActiveItems.first()
            if (existing.isEmpty()) {
                val cal1 = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 4) }
                val cal2 = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 12) }
                val cal3 = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 28) }
                val cal4 = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 45) }

                repository.insertItem(
                    PantryItem(
                        name = "Organic Whole Milk",
                        category = "Dairy & Eggs",
                        packageSize = "1 Gallon",
                        barcode = "0412201948",
                        expiryDateMillis = cal1.timeInMillis,
                        storageZone = "Fridge Door",
                        quantity = 1,
                        unit = "jug",
                        confidenceScore = 99
                    )
                )
                repository.insertItem(
                    PantryItem(
                        name = "Baby Spinach Clamshell",
                        category = "Produce • Fresh Greens",
                        packageSize = "312g pack",
                        barcode = "0714300018",
                        expiryDateMillis = cal2.timeInMillis,
                        storageZone = "Crisper Drawer",
                        quantity = 2,
                        unit = "packs",
                        confidenceScore = 95
                    )
                )
                repository.insertItem(
                    PantryItem(
                        name = "Almond Milk Unsweetened",
                        category = "Plant-Based Dairy",
                        packageSize = "946ml carton",
                        barcode = "0252930024",
                        expiryDateMillis = cal3.timeInMillis,
                        storageZone = "Pantry Shelf",
                        quantity = 3,
                        unit = "cartons",
                        confidenceScore = 97
                    )
                )
                repository.insertItem(
                    PantryItem(
                        name = "Wild Sockeye Salmon Fillets",
                        category = "Meat & Seafood",
                        packageSize = "680g bag",
                        barcode = "0823450912",
                        expiryDateMillis = cal4.timeInMillis,
                        storageZone = "Deep Freezer",
                        quantity = 1,
                        unit = "bag",
                        confidenceScore = 98
                    )
                )
            }
        }
    }
}

enum class NavTab(val title: String) {
    HOME("Today"),
    INVENTORY("Inventory"),
    SCAN("Scan"),
    LISTS("Lists"),
    HOUSEHOLD("Household")
}

fun formatExpiryDate(millis: Long): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    return formatter.format(millis)
}

fun calculateDaysRemaining(millis: Long): Int {
    val now = System.currentTimeMillis()
    val diff = millis - now
    return (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
}
