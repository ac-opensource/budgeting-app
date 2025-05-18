package dev.pandesal.sbp

import dev.pandesal.sbp.domain.usecase.SettingsUseCase
import dev.pandesal.sbp.fakes.FakeSettingsRepository
import dev.pandesal.sbp.presentation.settings.SettingsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = FakeSettingsRepository()
    private val useCase = SettingsUseCase(repository)

    @Test
    fun setDarkModeUpdatesRepository() = runTest {
        val vm = SettingsViewModel(useCase)
        vm.setDarkMode(true)
        advanceUntilIdle()
        assertTrue(repository.darkModeSet == true)
    }
}
