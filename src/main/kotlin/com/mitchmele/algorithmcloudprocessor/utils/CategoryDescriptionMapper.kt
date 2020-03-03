package com.mitchmele.algorithmcloudprocessor.utils

import com.mitchmele.algorithmcloudprocessor.store.Tag
import org.springframework.stereotype.Service


@Service
class CategoryDescriptionMapper {
    fun mapDescription(name: String): List<Tag> {
        return currentCategories[name.toLowerCase()]
            ?.map { description ->
                Tag(description)
            } ?: emptyList()
    }

    companion object {
        val currentCategories = mapOf(
            "strings" to listOf("String Manipulation", "String Formatting", "Algorithms"),
            "arrays" to listOf("Collections", "Data Structures", "Algorithms"),
            "numbers" to listOf("Math", "Puzzles", "Sequences", "Indexes"),
            "oop" to listOf("Encapsulation", "Inheritance", "Polymorphism"),
            "functional programming" to listOf("Pure Functions", "First Class Functions", "Functional Arguments")
        )
    }
}