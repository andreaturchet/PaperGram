package com.ciwrl.papergram.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DisplayCategory(
    val name: String,
    val isTranslated: Boolean
) : Parcelable