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
import java.math.BigDecimal

@Composable
fun TransactionItem(tx: Transaction) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = tx.name,
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

            Text(
                text = (if (tx.amount > BigDecimal.ZERO) "+₱" else "-₱") + "%,.2f".format(tx.amount.abs()),
                color = if (tx.amount > BigDecimal.ZERO) Color(0xFF2E7D32) else Color(0xFFC62828),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

