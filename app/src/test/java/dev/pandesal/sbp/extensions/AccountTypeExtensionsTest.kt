package dev.pandesal.sbp.extensions

import dev.pandesal.sbp.domain.model.AccountType
import org.junit.Assert.assertEquals
import org.junit.Test

class AccountTypeExtensionsTest {
    @Test
    fun `label returns expected string for each account type`() {
        assertEquals("Cash wallet", AccountType.CASH_WALLET.label())
        assertEquals("Mobile digital wallet", AccountType.MOBILE_DIGITAL_WALLET.label())
        assertEquals("Bank account", AccountType.BANK_ACCOUNT.label())
        assertEquals("Credit card", AccountType.CREDIT_CARD.label())
        assertEquals("Loan for asset", AccountType.LOAN_FOR_ASSET.label())
        assertEquals("Loan for spending", AccountType.LOAN_FOR_SPENDING.label())
    }
}

