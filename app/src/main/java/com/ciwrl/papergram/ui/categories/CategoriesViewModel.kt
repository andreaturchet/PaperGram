
package com.ciwrl.papergram.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ciwrl.papergram.data.Datasource
import com.ciwrl.papergram.data.UserPreferences
import com.ciwrl.papergram.ui.adapter.UiCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiCategories = MutableStateFlow<List<UiCategory>>(emptyList())
    val uiCategories = _uiCategories.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        val allCategories = Datasource.getCategories()
        val savedCodes = UserPreferences.getCategories(getApplication())
        _uiCategories.value = allCategories.map { category ->
            UiCategory(
                category = category,
                isSelected = savedCodes.contains(category.code)
            )
        }
    }

    fun toggleCategorySelection(toggledCategory: UiCategory) {
        _uiCategories.update { currentList ->
            currentList.map { uiCategory ->
                if (uiCategory.category.code == toggledCategory.category.code) {
                    uiCategory.copy(isSelected = !uiCategory.isSelected)
                } else {
                    uiCategory
                }
            }
        }
    }

    fun saveCategories() {
        val selectedCodes = _uiCategories.value
            .filter { it.isSelected }
            .map { it.category.code }
            .toSet()
        UserPreferences.saveCategories(getApplication(), selectedCodes)
    }

    fun getSelectedCodes(): Set<String> {
        return _uiCategories.value
            .filter { it.isSelected }
            .map { it.category.code }
            .toSet()
    }
}