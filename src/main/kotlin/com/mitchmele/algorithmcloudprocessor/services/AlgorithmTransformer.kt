package com.mitchmele.algorithmcloudprocessor.services

import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModel
import com.mitchmele.algorithmcloudprocessor.store.BaseAlgorithm
import com.mitchmele.algorithmcloudprocessor.store.Category
import com.mitchmele.algorithmcloudprocessor.store.Tag
import com.mitchmele.algorithmcloudprocessor.utils.CategoryDescriptionMapper
import org.springframework.integration.transformer.GenericTransformer
import org.springframework.stereotype.Service


@Service
class AlgorithmTransformer : GenericTransformer<BaseAlgorithm, AlgorithmDomainModel> {

    val mapper = CategoryDescriptionMapper()

    override fun transform(source: BaseAlgorithm): AlgorithmDomainModel {
        return AlgorithmDomainModel(
            name = source.name,
            codeSnippet = source.codeSnippet,
            category = Category(
                categoryDescription = source.category,
                difficultyLevel = setDifficultyLevel(source.category),
                tags = mapper.mapDescription("Strings")
            )
        )
    }

    fun parseCategoryInfo(source: BaseAlgorithm): List<Tag> {
        val listOfCategoryNames = listOf("Strings", "Collections", "Numbers", "Sorting", "Processing", "Data formatting")

        val names = source.name.split("(?=[A-Z])")
        println(names)

        return emptyList()

    }
    fun setDifficultyLevel(str: String): Int {
        return when (str) {
            "EASY" -> 2
            "MEDIUM" -> 3
            "HARD" -> 4
            "EXTREME PROGRAMMING" -> 5
            else -> 1
        }
    }
}