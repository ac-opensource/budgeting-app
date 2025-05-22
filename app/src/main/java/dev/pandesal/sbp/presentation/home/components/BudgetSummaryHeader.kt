package dev.pandesal.sbp.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import dev.pandesal.sbp.extensions.format
import dev.pandesal.sbp.extensions.currencySymbol
import java.math.BigDecimal

@Composable
fun BudgetSummaryHeader(unassigned: BigDecimal, assigned: BigDecimal, currency: String = "PHP") {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val symbol = currency.currencySymbol()

        Column {
            Text("Unallocated", style = MaterialTheme.typography.labelMedium)
            Text(
                text = "$symbol${unassigned.format()}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        Column {
            Text("Allocated", style = MaterialTheme.typography.labelMedium)
            Text(
                text = "$symbol${assigned.format()}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}