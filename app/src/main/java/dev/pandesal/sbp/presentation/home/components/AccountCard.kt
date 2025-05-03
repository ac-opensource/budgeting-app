package dev.pandesal.sbp.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.extensions.format
import dev.pandesal.sbp.presentation.model.AccountSummaryUiModel

@Composable
fun AccountCard(account: AccountSummaryUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(account.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    when {
                        account.isSpendingWallet && account.isFundingWallet -> "Spending + Savings"
                        account.isSpendingWallet -> "Spending Wallet"
                        account.isFundingWallet -> "Savings"
                        else -> "Other"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text("₱${account.balance.format()}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}