package dev.pandesal.sbp.domain.service

import android.content.Context
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.pandesal.sbp.domain.model.ReceiptData
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.suspendCancellableCoroutine

@Singleton
class ReceiptOcrService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun parseReceipt(image: InputImage): ReceiptData =
        suspendCancellableCoroutine { cont ->
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val text = visionText.text
                    val amountRegex = Regex("(\\d+[.,]\\d{2})")
                    val amount = amountRegex.find(text)?.value?.replace(",", "")?.toBigDecimalOrNull()
                    val dateRegex = Regex("(\\d{4}-\\d{2}-\\d{2})")
                    val date = dateRegex.find(text)?.value?.let { LocalDate.parse(it) }
                    val merchant = text.lines().firstOrNull()
                    cont.resume(ReceiptData(merchant, amount, date)) {}
                }
                .addOnFailureListener { cont.resume(ReceiptData()) {} }
        }
}
