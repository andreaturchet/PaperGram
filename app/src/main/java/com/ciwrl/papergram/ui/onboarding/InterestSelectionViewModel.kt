package com.ciwrl.papergram.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ciwrl.papergram.data.Datasource
import com.ciwrl.papergram.data.UserPreferences
import com.ciwrl.papergram.ui.adapter.UiMainCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Manages UI state and business logic for the [InterestSelectionFragment] during the
 * initial onboarding process.
 *
 * This ViewModel is responsible for:
 * - Loading the complete list of available categories from [Datasource].
 * - Initializing all categories with a default unselected state for the first-time user.
 * - Tracking user selections as they interact with the UI via [toggleCategorySelection].
 * - Exposing the list of categories and their current selection state via a StateFlow.
 * - Persisting the user's final list of interests to [UserPreferences] upon completion
 * of this onboarding step.
 *
 * @param application The application instance, required for accessing the context.
 * @see CategoriesViewModel which handles category management *after* the onboarding is complete.
 */

class InterestSelectionViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiCategories = MutableStateFlow<List<UiMainCategory>>(emptyList())
    val uiCategories = _uiCategories.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        _uiCategories.value = Datasource.getMainCategories().map { mainCategory ->
            UiMainCategory(
                mainCategory = mainCategory,
                isSelected = false
            )
        }
    }

    fun toggleCategorySelection(toggledCategory: UiMainCategory) {
        _uiCategories.update { currentList ->
            currentList.map { uiCategory ->
                if (uiCategory.mainCategory.name == toggledCategory.mainCategory.name) {
                    uiCategory.copy(isSelected = !uiCategory.isSelected)
                } else {
                    uiCategory
                }
            }
        }
    }

    fun saveSelectedCategories() {
        val selectedSubCategoryCodes = _uiCategories.value
            .filter { it.isSelected }
            .flatMap { it.mainCategory.subCategories }
            .map { it.code }
            .toSet()

        if (selectedSubCategoryCodes.isNotEmpty()) {
            UserPreferences.saveCategories(getApplication(), selectedSubCategoryCodes)
        }
    }
}