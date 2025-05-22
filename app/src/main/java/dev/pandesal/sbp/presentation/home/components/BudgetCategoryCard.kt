package dev.pandesal.sbp.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.extensions.format
import dev.pandesal.sbp.extensions.currencySymbol
import dev.pandesal.sbp.presentation.home.HomeScreen
import dev.pandesal.sbp.presentation.home.HomeUiState
import dev.pandesal.sbp.presentation.model.BudgetCategoryUiModel
import dev.pandesal.sbp.presentation.model.NetWorthUiModel
import java.math.BigDecimal

@Composable
fun BudgetCategoryCard(budget: BudgetCategoryUiModel) {
    val remaining = budget.allocated - budget.spent
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(budget.name, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = {
                    (budget.spent / budget.allocated).coerceIn(BigDecimal.ZERO, BigDecimal.ONE).toFloat()
                },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.inverseSurface,

            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "${budget.currency.currencySymbol()}${remaining.format()} left",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

