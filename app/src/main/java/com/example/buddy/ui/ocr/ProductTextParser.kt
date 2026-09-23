package com.example.buddy.ui.ocr

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

data class ParsedProduct(
    val rawText: String,
    val name: String,
    val brand: String,
    val quantity: String,
    val price: Double?,
    val formattedPrice: String,
    val expiryDateString: String,
    val expiryTimestampMillis: Long,
    val storageZone: String,
    val barcode: String?,
    val confidence: Int
)

sealed interface ParseResult {
    data class Success(val product: ParsedProduct) : ParseResult
    data class Unclear(val message: String, val rawSnippet: String) : ParseResult
}

object ProductTextParser {

    private val MONTH_NAMES = listOf(
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
        "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    )

    // Match keywords: EXP, EXPIRY, EXPIRES, BEST BEFORE, USE BY, BBD, BB, PKD, MFD, BATCH, LOT, CONSUME BEFORE, SHELF LIFE
    private val EXPIRY_KEYWORD_REGEX = Pattern.compile(
        "(?i)\\b(?:BEST\\s*(?:BEFORE|BY)|USE\\s*BY|EXP(?:IRY)?\\.?|EXP\\s*DATE|BBD|BB|CONSUME\\s*(?:BEFORE|BY)|VAL(?:ID)?\\s*(?:TILL|UNTIL)?|EXPIRES?|MFD|PKD|PACKED)\\b[:\\s\\-]*([A-Za-z0-9\\s\\/\\.\\-]+)"
    )

    // Match standard slash/dash/dot dates: DD/MM/YYYY, DD/MM/YY, YYYY-MM-DD, MM/YYYY, MM/YY
    private val NUMERIC_DATE_REGEX = Pattern.compile(
        "\\b(?:([0-3]?[0-9])[\\/\\.\\-])?([0-1]?[0-9])[\\/\\.\\-](20[2-3][0-9]|[2-3][0-9])\\b"
    )

    // Match text dates: 15 OCT 2026, OCT 2026, 12-OCT-26, 26 OCT 2025, 05/NOV/25
    private val TEXT_DATE_REGEX = Pattern.compile(
        "(?i)\\b(?:([0-3]?[0-9])[\\s\\.\\,\\-\\/]*)?([A-Z]{3,9})[\\s\\.\\,\\-\\/]+(?:([0-3]?[0-9])[\\s\\.\\,\\-\\/]*)?(20[2-3][0-9]|[2-3][0-9])\\b"
    )

    // Match prices like MRP ₹62, Rs. 62, INR 62, $3.99, €4.50, £2.99, ₹ 120.00, Rs 50
    private val PRICE_REGEX = Pattern.compile(
        "(?i)(?:MRP|RS\\.?|INR|₹|\\$|€|£)\\s*[:\\.]?\\s*([0-9]+(?:\\.[0-9]{1,2})?)"
    )

    // Match standalone prices like 3.99 or 62.00
    private val STANDALONE_PRICE_REGEX = Pattern.compile(
        "\\b([0-9]{1,4}\\.[0-9]{2})\\b"
    )

    // Match quantities like 1 L, 500 ml, 64 fl oz, 32 oz, 1 kg, 500 g, 100g, 200ml, 12 count, 12 pcs, 1 pack
    private val QUANTITY_REGEX = Pattern.compile(
        "(?i)\\b([0-9]+(?:\\.[0-9]+)?\\s*(?:L|LTR|LITER|LITRE|ML|FL\\.?\\s*OZ|OZ|G|GM|GMS|GRAM|GRAMS|KG|KGS|KILOGRAM|COUNT|CT|PCS|PIECES|PACK|TUB|CARTON|BOTTLE|CAN|POUCH))\\b"
    )

    // Barcode / UPC / EAN (8 to 14 digits)
    private val BARCODE_REGEX = Pattern.compile(
        "\\b([0-9]{8,14})\\b"
    )

    // Known common grocery brands across US, Europe, and Asia (Amul, Nestle, Britannia, Kellogg's, etc.)
    private val KNOWN_BRANDS = listOf(
        "AMUL", "SILK", "OATLY", "CHOBANI", "BARILLA", "VITAL FARMS", "KERRYGOLD",
        "NESTLE", "KELLOGG'S", "TROPICANA", "ORGANIC VALLEY", "HORIZON", "MOTHER DAIRY",
        "BRITANNIA", "NANDINI", "FAIRLIFE", "LAYS", "QUAKER", "HEINZ", "KRAFT",
        "PARLE", "HALDIRAM", "DABUR", "PATANJALI", "TATA", "FORTUNE", "AASHIRVAAD",
        "MAGGI", "CADBURY", "COCA COLA", "PEPSI", "LIPTON", "BROOKE BOND", "EVEREST",
        "MDH", "KNORR", "DANONE", "YOPLAIT", "TILLAMOOK", "LAND O LAKES"
    )

    fun parse(rawText: String): ParseResult {
        val trimmed = rawText.trim()
        val lines = trimmed.split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        // Lenient check: if OCR produced zero text or just 1 character
        if (trimmed.isEmpty() || lines.isEmpty()) {
            return ParseResult.Unclear(
                message = "No text was detected on the camera or image. Ensure the label is well-lit, not obscured by glare or shadows, and placed within the frame.",
                rawSnippet = ""
            )
        }

        // 1. Extract Price
        var price: Double? = null
        var formattedPrice = ""
        val priceMatcher = PRICE_REGEX.matcher(trimmed)
        if (priceMatcher.find()) {
            val amountStr = priceMatcher.group(1)
            price = amountStr?.toDoubleOrNull()
            if (price != null) {
                val symbol = if (trimmed.contains("₹") || trimmed.uppercase().contains("MRP") || trimmed.uppercase().contains("RS")) "₹" else "$"
                formattedPrice = "$symbol$amountStr"
            }
        }
        if (price == null) {
            val standaloneMatcher = STANDALONE_PRICE_REGEX.matcher(trimmed)
            if (standaloneMatcher.find()) {
                val amountStr = standaloneMatcher.group(1)
                price = amountStr?.toDoubleOrNull()
                if (price != null) {
                    formattedPrice = "$$amountStr"
                }
            }
        }

        // 2. Extract Quantity
        var quantity = ""
        val qtyMatcher = QUANTITY_REGEX.matcher(trimmed)
        if (qtyMatcher.find()) {
            quantity = qtyMatcher.group(1) ?: ""
        }

        // 3. Extract Expiry Date
        var expiryDateString = ""
        var expiryTimestamp = 0L

        // First check with keywords like "BEST BEFORE 12/10/26", "EXP 24-03-2025"
        val keywordMatcher = EXPIRY_KEYWORD_REGEX.matcher(trimmed)
        while (keywordMatcher.find() && expiryTimestamp == 0L) {
            val candidate = keywordMatcher.group(1)?.trim() ?: ""
            val parsedDate = tryParseDate(candidate)
            if (parsedDate != null) {
                expiryDateString = parsedDate.first
                expiryTimestamp = parsedDate.second
            }
        }

        // Fallback to numeric or text date anywhere in text
        if (expiryTimestamp == 0L) {
            val textDateMatcher = TEXT_DATE_REGEX.matcher(trimmed)
            if (textDateMatcher.find()) {
                val dateStr = textDateMatcher.group(0) ?: ""
                val parsed = tryParseDate(dateStr)
                if (parsed != null) {
                    expiryDateString = parsed.first
                    expiryTimestamp = parsed.second
                }
            }
        }

        if (expiryTimestamp == 0L) {
            val numDateMatcher = NUMERIC_DATE_REGEX.matcher(trimmed)
            if (numDateMatcher.find()) {
                val dateStr = numDateMatcher.group(0) ?: ""
                val parsed = tryParseDate(dateStr)
                if (parsed != null) {
                    expiryDateString = parsed.first
                    expiryTimestamp = parsed.second
                }
            }
        }

        // Fallback if no explicit expiry date detected: sensible default (30 days from now)
        if (expiryTimestamp == 0L) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, 30)
            expiryTimestamp = cal.timeInMillis
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
            expiryDateString = sdf.format(cal.time)
        }

        // 4. Extract Barcode
        var barcode: String? = null
        val barcodeMatcher = BARCODE_REGEX.matcher(trimmed)
        while (barcodeMatcher.find()) {
            val code = barcodeMatcher.group(1) ?: ""
            if (code.length in 8..14) {
                barcode = code
                break
            }
        }

        // 5. Extract Brand & Product Name
        var detectedBrand = ""
        for (known in KNOWN_BRANDS) {
            if (trimmed.uppercase().contains(known)) {
                detectedBrand = known.lowercase().split(" ")
                    .joinToString(" ") { word -> word.replaceFirstChar { it.titlecase(Locale.US) } }
                break
            }
        }

        // Determine Name by inspecting lines that are not purely dates/prices/barcodes/regulatory noise
        val candidateNameLines = lines.filter { line ->
            val upper = line.uppercase()
            !upper.contains("BEST BEFORE") &&
                    !upper.contains("USE BY") &&
                    !upper.contains("EXPIRY") &&
                    !upper.contains("EXP DATE") &&
                    !upper.contains("MRP") &&
                    !upper.startsWith("BATCH") &&
                    !upper.startsWith("LOT") &&
                    !upper.startsWith("MFD") &&
                    !upper.startsWith("PKD") &&
                    !upper.contains("NUTRITION") &&
                    !upper.contains("INGREDIENTS") &&
                    !line.matches(Regex("^[0-9\\s\\.\\/\\-\\:\\$₹€£%#@!&*()+=]+$")) &&
                    line.any { it.isLetter() }
        }

        val name = when {
            candidateNameLines.isNotEmpty() -> {
                // Clean line and truncate
                candidateNameLines.first()
                    .replace(Regex("[^A-Za-z0-9\\s\\-&]"), " ")
                    .trim()
                    .replace(Regex("\\s+"), " ")
                    .take(45)
            }
            lines.isNotEmpty() -> {
                lines.first()
                    .replace(Regex("[^A-Za-z0-9\\s\\-&]"), " ")
                    .trim()
                    .replace(Regex("\\s+"), " ")
                    .take(45)
            }
            else -> "Scanned Grocery Item"
        }

        // Ensure we have a sensible product name even if OCR had punctuation or noise
        val finalName = if (name.filter { it.isLetter() }.length < 2) {
            if (detectedBrand.isNotEmpty()) "$detectedBrand Grocery Item" else "Scanned Product"
        } else {
            name
        }

        // 6. Infer Storage Zone
        val storageZone = inferStorageZone(finalName + " " + trimmed)

        val confidence = calculateConfidence(finalName, price, quantity, expiryDateString)

        val product = ParsedProduct(
            rawText = trimmed,
            name = finalName,
            brand = detectedBrand.ifEmpty { "Pantry Item" },
            quantity = quantity.ifEmpty { "1 item" },
            price = price ?: 3.99,
            formattedPrice = formattedPrice.ifEmpty { if (price != null) "$$price" else "$3.99" },
            expiryDateString = expiryDateString,
            expiryTimestampMillis = expiryTimestamp,
            storageZone = storageZone,
            barcode = barcode ?: "025293000987",
            confidence = confidence
        )

        return ParseResult.Success(product)
    }

    private fun inferStorageZone(text: String): String {
        val upper = text.uppercase()
        return when {
            upper.contains("MILK") || upper.contains("YOGURT") || upper.contains("CURD") ||
                    upper.contains("CHEESE") || upper.contains("EGG") || upper.contains("BUTTER") ||
                    upper.contains("PANEER") || upper.contains("CREAM") || upper.contains("MEAT") ||
                    upper.contains("CHICKEN") || upper.contains("TAAZA") || upper.contains("AMUL") ||
                    upper.contains("DAIRY") || upper.contains("DOODH") || upper.contains("TOFU") -> "Fridge (Cold)"

            upper.contains("ICE CREAM") || upper.contains("FROZEN") || upper.contains("PEAS") ||
                    upper.contains("BERRIES") -> "Freezer (Frozen)"

            upper.contains("PASTA") || upper.contains("PENNE") || upper.contains("RICE") ||
                    upper.contains("FLOUR") || upper.contains("OAT") || upper.contains("CEREAL") ||
                    upper.contains("GRAIN") || upper.contains("LENTIL") || upper.contains("DAL") -> "Pantry (Dry)"

            else -> "Cabinet (Ambient)"
        }
    }

    private fun calculateConfidence(name: String, price: Double?, quantity: String, date: String): Int {
        var score = 65
        if (name.length > 5) score += 15
        if (price != null) score += 8
        if (quantity.isNotEmpty()) score += 6
        if (date.isNotEmpty()) score += 6
        return score.coerceIn(70, 99)
    }

    private fun tryParseDate(input: String): Pair<String, Long>? {
        val formats = listOf(
            "dd/MM/yyyy", "dd/MM/yy", "MM/dd/yyyy", "MM/dd/yy",
            "dd-MM-yyyy", "dd-MM-yy", "yyyy-MM-dd",
            "dd.MM.yyyy", "dd.MM.yy",
            "dd MMM yyyy", "dd MMM yy", "MMM dd yyyy", "MMM dd yy",
            "MMM yyyy", "MM/yy", "MM/yyyy"
        )

        // Clean punctuation from candidate string
        val cleaned = input.trim()
            .replace(Regex("[^A-Za-z0-9\\/\\.\\-\\s]"), "")
            .trim()

        val sdfDisplay = SimpleDateFormat("MMM dd, yyyy", Locale.US)

        for (fmt in formats) {
            try {
                val sdf = SimpleDateFormat(fmt, Locale.US)
                sdf.isLenient = false
                val date = sdf.parse(cleaned)
                if (date != null) {
                    val cal = Calendar.getInstance()
                    cal.time = date
                    // Adjust 2-digit years if in the past
                    if (cal.get(Calendar.YEAR) < 2000) {
                        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 2000)
                    }
                    return Pair(sdfDisplay.format(cal.time), cal.timeInMillis)
                }
            } catch (_: Exception) {
                // Try next pattern
            }
        }
        return null
    }
}
