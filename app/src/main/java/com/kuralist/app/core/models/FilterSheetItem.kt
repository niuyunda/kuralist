package com.kuralist.app.core.models

data class FilterSheetItem(
    val categoryName: String,
    val options: List<String>,
    val selectedOption: String?
)
