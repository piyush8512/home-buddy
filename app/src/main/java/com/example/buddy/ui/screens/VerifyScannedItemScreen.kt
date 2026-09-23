package com.example.buddy.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.ViewGroup
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import java.io.ByteArrayOutputStream
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FlashlightOff
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.buddy.data.PantryItem
import com.example.buddy.ui.components.InventoryShelfIcon
import com.example.buddy.ui.ocr.MlKitTextScanner
import com.example.buddy.ui.ocr.ParseResult
import com.example.buddy.ui.ocr.ParsedProduct
import com.example.buddy.ui.theme.BaseCanvas
import com.example.buddy.ui.theme.OnSurface
import com.example.buddy.ui.theme.OnSurfaceVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

enum class OcrScreenMode {
    CAMERA,       // CameraX viewfinder with capture button
    PROCESSING,   // On-device ML Kit processing
    UNCLEAR,      // "Couldn't read clearly, please take another photo"
    REVIEW        // Structured item review & save to Room
}

data class SampleLabelPreset(
    val title: String,
    val text: String,
    val photoUrl: String,
    val isUnclear: Boolean = false
)

val SAMPLE_PRESETS = listOf(
    SampleLabelPreset(
        title = "Amul Taaza Milk (From Spec)",
        text = "AMUL TAAZA\n1 L\nMRP ₹62\nBEST BEFORE 12/10/26",
        photoUrl = "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=800&auto=format&fit=crop&q=80"
    ),
    SampleLabelPreset(
        title = "Silk Almond Milk",
        text = "SILK ALMOND MILK\nUnsweetened 64 fl oz\n$3.99\nBEST BY DEC 08 2025\nUPC 025293000987",
        photoUrl = "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=800&auto=format&fit=crop&q=80"
    ),
    SampleLabelPreset(
        title = "Chobani Greek Yogurt",
        text = "CHOBANI GREEK YOGURT\nPlain 0% Milkfat 32 oz\n$5.49\nEXP: 28/11/2025\nUPC 012546001289",
        photoUrl = "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=800&auto=format&fit=crop&q=80"
    ),
    SampleLabelPreset(
        title = "Barilla Penne Pasta",
        text = "BARILLA PENNE RIGATE\n16 oz\n$2.19\nUSE BY 18/08/2026\nUPC 076808500201",
        photoUrl = "https://images.unsplash.com/photo-1551462147-ff29053bfc14?w=800&auto=format&fit=crop&q=80"
    ),
    SampleLabelPreset(
        title = "Blurry / Unclear Photo (Test Error)",
        text = "~~..\n...",
        photoUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&auto=format&fit=crop&q=80",
        isUnclear = true
    )
)

@Composable
fun VerifyScannedItemScreen(
    onBackClick: () -> Unit,
    onItemSaved: (PantryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var screenMode by remember { mutableStateOf(OcrScreenMode.CAMERA) }
    var isTorchOn by remember { mutableStateOf(false) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // CameraX instance references
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    // Captured / Processed State
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var parsedProduct by remember { mutableStateOf<ParsedProduct?>(null) }
    var unclearMessage by remember { mutableStateOf("Couldn't read clearly, please take another photo.") }
    var showPresetMenu by remember { mutableStateOf(false) }

    // Form fields in Review Mode
    var reviewName by remember { mutableStateOf("") }
    var reviewBrand by remember { mutableStateOf("") }
    var reviewQuantity by remember { mutableStateOf("") }
    var reviewPrice by remember { mutableStateOf("3.99") }
    var reviewExpiryDateString by remember { mutableStateOf("") }
    var reviewExpiryMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var reviewStorageZone by remember { mutableStateOf("Fridge (Cold)") }
    var reviewBarcode by remember { mutableStateOf("025293000987") }
    var reviewConfidence by remember { mutableIntStateOf(92) }
    var reviewImageUrl by remember { mutableStateOf("") }
    var shareWithFamily by remember { mutableStateOf(true) }
    var lendableItem by remember { mutableStateOf(false) }

    fun processOcrText(text: String, samplePhotoUrl: String = "", bitmap: Bitmap? = null) {
        scope.launch {
            screenMode = OcrScreenMode.PROCESSING
            val result = withContext(Dispatchers.Default) {
                if (bitmap != null) {
                    MlKitTextScanner.processBitmap(bitmap)
                } else {
                    MlKitTextScanner.parseRawText(text)
                }
            }

            when (result) {
                is ParseResult.Success -> {
                    val p = result.product
                    parsedProduct = p
                    reviewName = p.name
                    reviewBrand = p.brand
                    reviewQuantity = p.quantity
                    reviewPrice = String.format(Locale.US, "%.2f", p.price ?: 3.99)
                    reviewExpiryDateString = p.expiryDateString
                    reviewExpiryMillis = p.expiryTimestampMillis
                    reviewStorageZone = p.storageZone
                    reviewBarcode = p.barcode ?: "025293000987"
                    reviewConfidence = p.confidence
                    reviewImageUrl = samplePhotoUrl.ifEmpty {
                        "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=800&auto=format&fit=crop&q=80"
                    }
                    screenMode = OcrScreenMode.REVIEW
                }
                is ParseResult.Unclear -> {
                    unclearMessage = result.message
                    screenMode = OcrScreenMode.UNCLEAR
                }
            }
        }
    }

    // Photo picker for selecting any real product photo from device storage
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    if (bitmap != null) {
                        withContext(Dispatchers.Main) {
                            capturedBitmap = bitmap
                            processOcrText("", bitmap = bitmap)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            unclearMessage = "Could not load selected photo. Please select another image."
                            screenMode = OcrScreenMode.UNCLEAR
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        unclearMessage = "Failed to load image: ${e.localizedMessage ?: "Unknown error"}. Please try another photo."
                        screenMode = OcrScreenMode.UNCLEAR
                    }
                }
            }
        }
    }

    fun capturePhoto() {
        val capture = imageCapture
        if (capture != null && hasCameraPermission) {
            screenMode = OcrScreenMode.PROCESSING
            capture.takePicture(
                cameraExecutor,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        val bitmap = imageProxyToBitmap(image)
                        image.close()
                        scope.launch(Dispatchers.Main) {
                            if (bitmap != null) {
                                capturedBitmap = bitmap
                                processOcrText("", bitmap = bitmap)
                            } else {
                                unclearMessage = "Couldn't read clearly from camera capture, please take another photo."
                                screenMode = OcrScreenMode.UNCLEAR
                            }
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        scope.launch(Dispatchers.Main) {
                            unclearMessage = "Camera capture failed (${exception.localizedMessage ?: "Hardware error"}). Please take another shot or pick an image from gallery."
                            screenMode = OcrScreenMode.UNCLEAR
                        }
                    }
                }
            )
        } else {
            // Permission or camera unavailable
            unclearMessage = "Camera is not available or permission was denied. Please grant camera permission or pick an image from your device storage."
            screenMode = OcrScreenMode.UNCLEAR
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BaseCanvas)
    ) {
        // --- TOP BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("verify_ocr_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OnSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                Text(
                    text = when (screenMode) {
                        OcrScreenMode.CAMERA -> "Position Product Label"
                        OcrScreenMode.PROCESSING -> "Scanning with ML Kit..."
                        OcrScreenMode.UNCLEAR -> "Scan Unclear"
                        OcrScreenMode.REVIEW -> "Verify Scanned Item"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 18.sp
                )
                Text(
                    text = when (screenMode) {
                        OcrScreenMode.CAMERA -> "Align label within the frame"
                        OcrScreenMode.PROCESSING -> "Running on-device OCR model"
                        OcrScreenMode.UNCLEAR -> "Low contrast or blurry text"
                        OcrScreenMode.REVIEW -> "On-Device ML Kit Text Recognition v2"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666),
                    fontSize = 11.5.sp
                )
            }

            // Pick photo from gallery button
            IconButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .size(40.dp)
                    .testTag("verify_ocr_gallery_btn")
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoLibrary,
                    contentDescription = "Pick Image from Gallery",
                    tint = OnSurface,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Quick Preset / Test Label Selector
            Box {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { showPresetMenu = true }
                        .testTag("ocr_samples_picker_btn"),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFDDDDDD))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = "Presets",
                            tint = Color(0xFF285E43),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sample Labels",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            fontSize = 11.5.sp
                        )
                    }
                }

                DropdownMenu(
                    expanded = showPresetMenu,
                    onDismissRequest = { showPresetMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Sample Test Labels (Optional)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = OnSurfaceVariant
                            )
                        },
                        onClick = { },
                        enabled = false
                    )
                    HorizontalDivider()
                    SAMPLE_PRESETS.forEach { preset ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = preset.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (preset.isUnclear) Color(0xFFD32F2F) else OnSurface
                                    )
                                    Text(
                                        text = preset.text.replace("\n", " • ").take(40),
                                        fontSize = 11.sp,
                                        color = OnSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                showPresetMenu = false
                                capturedBitmap = null
                                processOcrText(preset.text, preset.photoUrl)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Flashlight / Torch Action
            IconButton(
                onClick = { isTorchOn = !isTorchOn },
                modifier = Modifier
                    .size(40.dp)
                    .testTag("verify_ocr_torch_btn")
            ) {
                Icon(
                    imageVector = if (isTorchOn) Icons.Outlined.FlashlightOff else Icons.Outlined.FlashlightOn,
                    contentDescription = "Toggle Flash",
                    tint = if (isTorchOn) Color(0xFFD48806) else OnSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // --- BODY BY MODE ---
        when (screenMode) {
            OcrScreenMode.CAMERA -> {
                CameraViewfinderScreen(
                    hasCameraPermission = hasCameraPermission,
                    onImageCaptureConfigured = { imageCapture = it },
                    onCaptureClicked = { capturePhoto() },
                    onPickGalleryClicked = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                )
            }

            OcrScreenMode.PROCESSING -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BaseCanvas),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF285E43),
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Analyzing Image...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Running on-device ML Kit OCR & extracting product attributes",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant,
                                textAlign = TextAlign.Center,
                                fontSize = 12.5.sp
                            )
                        }
                    }
                }
            }

            OcrScreenMode.UNCLEAR -> {
                // --- UNCLEAR / BLURRY ALERT VIEW ---
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ocr_unclear_warning_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFF3E0),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.WarningAmber,
                                        contentDescription = "Unclear Photo",
                                        tint = Color(0xFFE65100),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Couldn’t read clearly, please take another photo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                textAlign = TextAlign.Center,
                                fontSize = 17.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = unclearMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666),
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { screenMode = OcrScreenMode.CAMERA },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("ocr_retake_after_unclear_btn"),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PhotoCamera,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Take Another Photo",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("ocr_pick_gallery_after_unclear_btn"),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF285E43))
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PhotoLibrary,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Pick Photo from Device",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = {
                                    // Let the user edit details directly without being blocked
                                    reviewName = if (reviewName.isNotEmpty()) reviewName else "New Grocery Item"
                                    screenMode = OcrScreenMode.REVIEW
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("ocr_manual_entry_after_unclear_btn"),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color(0xFFCCCCCC))
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = null,
                                    tint = Color(0xFF333333),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Enter Details Manually",
                                    color = Color(0xFF333333),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            OcrScreenMode.REVIEW -> {
                // --- REVIEW SCREEN (Confirmed Structured Data) ---
                ReviewScannedItemView(
                    scrollState = scrollState,
                    capturedBitmap = capturedBitmap,
                    reviewName = reviewName,
                    onNameChange = { reviewName = it },
                    reviewBrand = reviewBrand,
                    reviewQuantity = reviewQuantity,
                    onQuantityChange = { reviewQuantity = it },
                    reviewPrice = reviewPrice,
                    onPriceChange = { reviewPrice = it },
                    reviewExpiryDateString = reviewExpiryDateString,
                    reviewExpiryMillis = reviewExpiryMillis,
                    onExpiryMillisChange = { reviewExpiryMillis = it },
                    reviewStorageZone = reviewStorageZone,
                    onStorageZoneChange = { reviewStorageZone = it },
                    reviewBarcode = reviewBarcode,
                    reviewConfidence = reviewConfidence,
                    reviewImageUrl = reviewImageUrl,
                    shareWithFamily = shareWithFamily,
                    onShareWithFamilyChange = { shareWithFamily = it },
                    lendableItem = lendableItem,
                    onLendableItemChange = { lendableItem = it },
                    onRetakeClick = { screenMode = OcrScreenMode.CAMERA },
                    onConfirmSave = {
                        val parsedPriceVal = reviewPrice.toDoubleOrNull() ?: 3.99
                        val pantryItem = PantryItem(
                            name = reviewName,
                            category = if (reviewStorageZone.contains("Fridge")) "Dairy & Perishables" else "Dry Pantry Essentials",
                            packageSize = reviewQuantity,
                            barcode = reviewBarcode,
                            expiryDateMillis = reviewExpiryMillis,
                            storageZone = reviewStorageZone,
                            quantity = 1,
                            unit = "unit",
                            imageUrl = reviewImageUrl,
                            price = parsedPriceVal,
                            confidenceScore = reviewConfidence,
                            nutriScore = "A",
                            ecoImpact = "A",
                            subtitle = "$reviewBrand • $reviewQuantity"
                        )
                        onItemSaved(pantryItem)
                    }
                )
            }
        }
    }
}

/**
 * Live CameraX Viewfinder with alignment box overlay and bottom shutter capture button.
 */
@Composable
private fun CameraViewfinderScreen(
    hasCameraPermission: Boolean,
    onImageCaptureConfigured: (ImageCapture) -> Unit,
    onCaptureClicked: () -> Unit,
    onPickGalleryClicked: () -> Unit,
    onRequestPermission: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val capture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                            .build()

                        onImageCaptureConfigured(capture)

                        try {
                            cameraProvider.unbindAll()
                            val camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                capture
                            )

                            // Tap to focus on preview for sharp grocery label text
                            previewView.setOnTouchListener { view, motionEvent ->
                                if (motionEvent.action == android.view.MotionEvent.ACTION_UP) {
                                    val factory = previewView.meteringPointFactory
                                    val point = factory.createPoint(motionEvent.x, motionEvent.y)
                                    val action = androidx.camera.core.FocusMeteringAction.Builder(point).build()
                                    camera.cameraControl.startFocusAndMetering(action)
                                    view.performClick()
                                }
                                true
                            }
                        } catch (e: Exception) {
                            // Fallback if camera not available
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                }
            )
        } else {
            // Permission request prompt
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1B1B1B)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Camera Permission Needed",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "To scan grocery labels with on-device ML Kit OCR, allow camera access or pick a real product photo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onRequestPermission,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF285E43))
                    ) {
                        Text("Grant Permission")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onPickGalleryClicked,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoLibrary,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pick Product Photo from Storage")
                    }
                }
            }
        }

        // --- SCANNING BOX OVERLAY (Target reticle) ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(BorderStroke(2.dp, Color(0xFF4CAF50)), RoundedCornerShape(16.dp))
                    .background(Color(0x1A000000))
            ) {
                // Corner tags
                Text(
                    text = "POSITION PRODUCT LABEL HERE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                        .background(Color(0x99000000), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // --- SHUTTER CAPTURE CONTROLS ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color(0xCC000000))
                .padding(vertical = 18.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Take a real photo or select from gallery for on-device OCR",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery pick button
                IconButton(
                    onClick = onPickGalleryClicked,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0x4DFFFFFF))
                        .testTag("ocr_viewfinder_gallery_btn")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoLibrary,
                        contentDescription = "Pick Image from Storage",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Shutter Ring Button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(4.dp, Color.White), CircleShape)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onCaptureClicked() }
                        .testTag("ocr_shutter_capture_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF285E43))
                    )
                }

                // Symmetrical spacer
                Spacer(modifier = Modifier.size(52.dp))
            }
        }
    }
}

/**
 * Review Card screen showing on-device ML Kit structured outputs.
 */
@Composable
private fun ReviewScannedItemView(
    scrollState: androidx.compose.foundation.ScrollState,
    capturedBitmap: Bitmap?,
    reviewName: String,
    onNameChange: (String) -> Unit,
    reviewBrand: String,
    reviewQuantity: String,
    onQuantityChange: (String) -> Unit,
    reviewPrice: String,
    onPriceChange: (String) -> Unit,
    reviewExpiryDateString: String,
    reviewExpiryMillis: Long,
    onExpiryMillisChange: (Long) -> Unit,
    reviewStorageZone: String,
    onStorageZoneChange: (String) -> Unit,
    reviewBarcode: String,
    reviewConfidence: Int,
    reviewImageUrl: String,
    shareWithFamily: Boolean,
    onShareWithFamilyChange: (Boolean) -> Unit,
    lendableItem: Boolean,
    onLendableItemChange: (Boolean) -> Unit,
    onRetakeClick: () -> Unit,
    onConfirmSave: () -> Unit
) {
    val daysRemaining = remember(reviewExpiryMillis) {
        val now = System.currentTimeMillis()
        val diff = reviewExpiryMillis - now
        (diff / (1000L * 60 * 60 * 24)).coerceAtLeast(0).toInt()
    }

    val formattedDate = remember(reviewExpiryMillis) {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        sdf.format(Date(reviewExpiryMillis))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // --- PREVIEW CROP CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF285E43),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (capturedBitmap != null) "Real Scanned Photo & ML Kit OCR" else "Captured Photo & ML Kit OCR",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            fontSize = 13.5.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFD5F3DF)
                    ) {
                        Text(
                            text = "✓ $reviewConfidence% Match",
                            color = Color(0xFF135B2B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2B221A))
                ) {
                    if (capturedBitmap != null) {
                        Image(
                            bitmap = capturedBitmap.asImageBitmap(),
                            contentDescription = reviewName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        AsyncImage(
                            model = reviewImageUrl,
                            contentDescription = reviewName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // OCR Bounding Box overlay
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(BorderStroke(2.dp, Color(0xFF2979FF)), RoundedCornerShape(8.dp))
                            .background(Color(0x332979FF))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column {
                            Text(
                                text = reviewName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Qty: $reviewQuantity • Exp: $formattedDate",
                                color = Color(0xFFE0E0E0),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- STRUCTURED FORM CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Product Name
                Text(
                    text = "Product Name",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 13.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF7F7F6),
                    border = BorderStroke(1.dp, Color(0xFFE8E8E6))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = reviewName,
                            onValueChange = onNameChange,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ocr_item_name_input"),
                            textStyle = TextStyle(
                                color = OnSurface,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            cursorBrush = SolidColor(OnSurface),
                            singleLine = true
                        )
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quantity & Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Quantity
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Quantity / Pack Size",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF7F7F6),
                            border = BorderStroke(1.dp, Color(0xFFE8E8E6))
                        ) {
                            BasicTextField(
                                value = reviewQuantity,
                                onValueChange = onQuantityChange,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("ocr_quantity_input"),
                                textStyle = TextStyle(
                                    color = OnSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                singleLine = true
                            )
                        }
                    }

                    // Price
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Price",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF7F7F6),
                            border = BorderStroke(1.dp, Color(0xFFE8E8E6))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$ ",
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                                BasicTextField(
                                    value = reviewPrice,
                                    onValueChange = onPriceChange,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ocr_price_input"),
                                    textStyle = TextStyle(
                                        color = OnSurface,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Expiry Date Header & Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Extracted Expiry Date",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        fontSize = 13.5.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFD5F3DF)
                    ) {
                        Text(
                            text = "Safe for $daysRemaining days",
                            color = Color(0xFF135B2B),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF7F7F6),
                        border = BorderStroke(1.dp, Color(0xFFE8E8E6))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formattedDate,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface,
                                fontSize = 14.sp
                            )
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = null,
                                tint = Color(0xFF285E43),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                val cal = Calendar.getInstance()
                                cal.timeInMillis = reviewExpiryMillis
                                cal.add(Calendar.DAY_OF_YEAR, 7)
                                onExpiryMillisChange(cal.timeInMillis)
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF3F3F1)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(44.dp)
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+7d", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                val cal = Calendar.getInstance()
                                cal.timeInMillis = reviewExpiryMillis
                                cal.add(Calendar.DAY_OF_YEAR, 30)
                                onExpiryMillisChange(cal.timeInMillis)
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF3F3F1)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(44.dp)
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+30d", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Storage Zone
                Text(
                    text = "Storage Placement",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    fontSize = 13.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Fridge (Cold)", "Pantry (Dry)", "Freezer (Frozen)", "Cabinet (Ambient)").forEach { zone ->
                        val isSelected = reviewStorageZone == zone
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onStorageZoneChange(zone) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFF285E43) else Color(0xFFF3F3F1)
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(42.dp)
                                    .padding(horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = zone.split(" ")[0],
                                    color = if (isSelected) Color.White else OnSurface,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.5.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Confirm & Save Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(CircleShape)
                .clickable { onConfirmSave() }
                .testTag("ocr_confirm_save_btn"),
            shape = CircleShape,
            color = Color.Black
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Confirm & Save to Pantry",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Retake Photo Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRetakeClick() }
                .padding(vertical = 8.dp)
                .testTag("ocr_retake_photo_btn"),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = null,
                    tint = Color(0xFF555555),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Retake Photo",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF555555),
                    fontSize = 13.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Converts CameraX ImageProxy to an Android Bitmap supporting JPEG, YUV_420_888 and rotation.
 */
private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
    return try {
        val rotationDegrees = image.imageInfo.rotationDegrees
        val bitmap = when (image.format) {
            ImageFormat.JPEG -> {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
            ImageFormat.YUV_420_888 -> {
                val yBuffer = image.planes[0].buffer
                val uBuffer = image.planes[1].buffer
                val vBuffer = image.planes[2].buffer

                val ySize = yBuffer.remaining()
                val uSize = uBuffer.remaining()
                val vSize = vBuffer.remaining()

                val nv21 = ByteArray(ySize + uSize + vSize)
                yBuffer.get(nv21, 0, ySize)
                vBuffer.get(nv21, ySize, vSize)
                uBuffer.get(nv21, ySize + vSize, uSize)

                val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
                val out = ByteArrayOutputStream()
                yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 90, out)
                val imageBytes = out.toByteArray()
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            }
            else -> {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        }

        if (bitmap != null && rotationDegrees != 0) {
            val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    } catch (e: Exception) {
        null
    }
}
