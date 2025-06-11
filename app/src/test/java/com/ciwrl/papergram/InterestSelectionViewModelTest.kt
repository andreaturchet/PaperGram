package com.ciwrl.papergram.ui.onboarding

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ciwrl.papergram.data.Datasource
import com.ciwrl.papergram.data.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class InterestSelectionViewModelTest {

    // Questa regola esegue ogni task in modo sincrono
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // "dispatcher" di test per controllare le coroutine
    private val testDispatcher = UnconfinedTestDispatcher()

    // "mock" (finti) che simulano le dipendenze reali
    private lateinit var mockApplication: Application
    private lateinit var viewModel: InterestSelectionViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockApplication = mock()

        viewModel = InterestSelectionViewModel(mockApplication)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleCategorySelection should update isSelected state correctly`() = runTest {
        val firstCategory = viewModel.uiCategories.value.first()
        assertFalse("Precondition failed: Category should not be selected initially.", firstCategory.isSelected)

        viewModel.toggleCategorySelection(firstCategory)

        val updatedCategory = viewModel.uiCategories.value.first()
        assertTrue("Category should be selected after the first toggle.", updatedCategory.isSelected)

        viewModel.toggleCategorySelection(updatedCategory)
        val finalCategoryState = viewModel.uiCategories.value.first()
        assertFalse("Category should be deselected after the second toggle.", finalCategoryState.isSelected)
    }
}