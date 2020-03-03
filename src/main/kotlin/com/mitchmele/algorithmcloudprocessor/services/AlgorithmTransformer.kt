package com.mitchmele.algorithmcloudprocessor.services

import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModel
import com.mitchmele.algorithmcloudprocessor.store.BaseAlgorithm
import com.mitchmele.algorithmcloudprocessor.store.Category
import com.mitchmele.algorithmcloudprocessor.store.Tag
import com.mitchmele.algorithmcloudprocessor.utils.CategoryDescriptionMapper
import org.springframework.integration.transformer.GenericTransformer
import org.springframework.stereotype.Service
import java.util.*


@Service
class AlgorithmTransformer(
    val mapper: CategoryDescriptionMapper
) : GenericTransformer<BaseAlgorithm, AlgorithmDomainModel> {

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