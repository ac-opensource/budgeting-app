package dev.pandesal.sbp.domain.usecase

import com.google.mlkit.vision.common.InputImage
import dev.pandesal.sbp.domain.model.ReceiptData
import dev.pandesal.sbp.domain.service.ReceiptOcrService
import javax.inject.Inject

class ReceiptUseCase @Inject constructor(
    private val service: ReceiptOcrService
) {
    suspend fun parse(image: InputImage): ReceiptData = service.parseReceipt(image)
}
