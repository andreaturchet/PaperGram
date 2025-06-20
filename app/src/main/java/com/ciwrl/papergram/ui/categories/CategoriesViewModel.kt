package com.ciwrl.papergram.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciwrl.papergram.R
import com.ciwrl.papergram.data.Datasource
import com.ciwrl.papergram.data.UserPreferences
import com.ciwrl.papergram.ui.adapter.UiMainCategory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Manages UI state and business logic for the [CategoriesFragment].
 *
 * This ViewModel is responsible for:
 * - Loading the complete list of main categories from [Datasource].
 * - Synchronizing the selection state of categories with the user's saved preferences
 * from [UserPreferences].
 * - Providing the list of categories and their selection state to the UI via a StateFlow.
 * - Handling user interactions, such as toggling a category's selection.
 * - Persisting the final selection of categories back to [UserPreferences].
 *
 * @param application The application instance, required for accessing the context.
 */

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiCategories = MutableStateFlow<List<UiMainCategory>>(emptyList())
    val uiCategories = _uiCategories.asStateFlow()

    private val _toastMessage = MutableSharedFlow<Int>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        val allMainCategories = Datasource.getMainCategories()
        val savedSubCategoryCodes = UserPreferences.getCategories(getApplication())

        _uiCategories.value = allMainCategories.map { mainCategory ->
            val allSubCategoriesAreSelected = mainCategory.subCategories.all { subCat ->
                savedSubCategoryCodes.contains(subCat.code)
            }
            UiMainCategory(
                mainCategory = mainCategory,
                isSelected = allSubCategoriesAreSelected
            )
        }
    }

    fun toggleCategorySelection(toggledCategory: UiMainCategory) {
        viewModelScope.launch {

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
    }

    fun saveCategories() {
        val selectedSubCategoryCodes = _uiCategories.value
            .filter { it.isSelected }
            .flatMap { it.mainCategory.subCategories }
            .map { it.code }
            .toSet()

        if (selectedSubCategoryCodes.isEmpty()) {
            viewModelScope.launch {
                _toastMessage.emit(R.string.select_at_least_one_category_to_save)
            }
            return
        }

        UserPreferences.saveCategories(getApplication(), selectedSubCategoryCodes)
    }

    fun getSelectedCodes(): Set<String> {
        return _uiCategories.value
            .filter { it.isSelected }
            .flatMap { it.mainCategory.subCategories }
            .map { it.code }
            .toSet()
    }
}