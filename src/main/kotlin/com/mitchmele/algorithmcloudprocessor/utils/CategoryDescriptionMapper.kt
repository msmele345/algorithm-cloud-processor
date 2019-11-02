package com.mitchmele.algorithmcloudprocessor.utils

import com.mitchmele.algorithmcloudprocessor.store.Tag
import org.springframework.stereotype.Service


@Service
class CategoryDescriptionMapper {

    companion object {
        val currentCategories = mapOf(
            "Strings" to listOf("String Manipulation", "String Formatting", "Algorithms"),
            "Arrays" to listOf("Collections", "Data Structures", "Algorithms")
        )

    }
    fun mapDescription(name: String): List<Tag> {
        return currentCategories[name]?.map { description ->
            Tag(description)
        } ?: emptyList()
    }
}