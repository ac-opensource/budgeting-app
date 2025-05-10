package dev.pandesal.sbp.presentation.components

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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(tx.name)
            Text(tx.createdAt.toString(), style = MaterialTheme.typography.bodySmall)
        }
        Text(
            text = (if (tx.amount > BigDecimal.ZERO) "+$" else "-$") + "%,.2f".format(tx.amount.abs()),
            color = if (tx.amount > BigDecimal.ZERO) Color.Green else Color.Red
        )
    }
}
