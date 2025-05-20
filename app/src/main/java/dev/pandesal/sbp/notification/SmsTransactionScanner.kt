package dev.pandesal.sbp.notification

import android.content.Context
import android.provider.Telephony
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.pandesal.sbp.domain.model.NotificationType
import javax.inject.Inject

class SmsTransactionScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scan() {
        val cursor = context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            arrayOf(Telephony.Sms.BODY),
            null,
            null,
            null
        ) ?: return

        cursor.use {
            val bodyIndex = it.getColumnIndexOrThrow(Telephony.Sms.BODY)
            while (it.moveToNext()) {
                val body = it.getString(bodyIndex) ?: continue
                if (isFinancialSms(body)) {
                    InAppNotificationCenter.postNotification(
                        message = body,
                        type = NotificationType.TRANSACTION_SUGGESTION,
                        canCreateTransaction = true
                    )
                }
            }
        }
    }

    private fun isFinancialSms(text: String): Boolean {
        val pattern = Regex(
            "(?i)(\\bpaid\\b|\\bpurchase\\b|\\bdeposit\\b|\\bspent\\b|\\bpayment\\b|\\bdebit\\b|" +
                "\\bcredited\\b|\\bbalance\\b|\\bwithdraw\\b|\\bsent\\b|\\btransfer\\b|\\bcash\\s?in\\b|" +
                "\\bcashout\\b|\\binstapay\\b|\\bpesonet\\b|\\bpay\\b|\\bcharge\\b|\\breceived\\b|₱|PHP)"
        )
        return pattern.containsMatchIn(text)
    }
}

