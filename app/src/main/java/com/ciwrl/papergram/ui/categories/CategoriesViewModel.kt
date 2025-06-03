package com.ciwrl.papergram.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ciwrl.papergram.data.Datasource
import com.ciwrl.papergram.data.UserPreferences
import com.ciwrl.papergram.ui.adapter.UiMainCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiCategories = MutableStateFlow<List<UiMainCategory>>(emptyList())
    val uiCategories = _uiCategories.asStateFlow()

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

    fun saveCategories() {
        val selectedSubCategoryCodes = _uiCategories.value
            .filter { it.isSelected }
            .flatMap { it.mainCategory.subCategories }
            .map { it.code }
            .toSet()

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