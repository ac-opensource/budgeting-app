package dev.pandesal.sbp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.extensions.currencySymbol
import dev.pandesal.sbp.extensions.format
import java.math.BigDecimal

@Composable
fun TransactionItem(
    tx: Transaction,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = tx.toAccountName ?: tx.merchantName ?: tx.fromAccountName ?: tx.name,
                    style = MaterialTheme.typography.titleMedium
                )
                if (tx.category != null) {
                    Text(
                        text = tx.category.name,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            val symbol = tx.currency.currencySymbol()
            val (prefix, color) = when (tx.transactionType) {
                dev.pandesal.sbp.domain.model.TransactionType.INFLOW -> "+$symbol" to Color(0xFF2E7D32)
                dev.pandesal.sbp.domain.model.TransactionType.OUTFLOW -> "-$symbol" to Color(0xFFC62828)
                else -> "" to MaterialTheme.colorScheme.onSurface
            }

            Text(
                text = prefix + tx.amount.abs().format(),
                color = color,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

