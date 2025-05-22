package dev.pandesal.sbp.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.extensions.format
import dev.pandesal.sbp.extensions.currencySymbol
import dev.pandesal.sbp.presentation.model.AccountSummaryUiModel
import dev.pandesal.sbp.domain.model.AccountType
import dev.pandesal.sbp.extensions.toLargeValueCurrency
import java.util.Currency

@Composable
fun AccountCard(account: AccountSummaryUiModel, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().height(68.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(getAccountIcon(account.type), contentDescription = null)
            Spacer(Modifier.width(8.dp))
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
            Text(
                account.balance.toLargeValueCurrency(
                    Currency.getInstance(account.currency)
                ),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

private fun getAccountIcon(type: AccountType): ImageVector = when (type) {
    AccountType.CASH_WALLET -> Icons.Outlined.Wallet
    AccountType.MOBILE_DIGITAL_WALLET -> Icons.Outlined.AccountBalanceWallet
    AccountType.BANK_ACCOUNT -> Icons.Outlined.AccountBalance
    AccountType.CREDIT_CARD -> Icons.Outlined.CreditCard
    AccountType.LOAN_FOR_ASSET, AccountType.LOAN_FOR_SPENDING -> Icons.Outlined.AttachMoney
}
