package com.mitchmele.algorithmcloudprocessor.store

import com.mitchmele.algorithmcloudprocessor.result.ServiceErrors
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable

data class BaseAlgorithm(
    val name: String = "",
    val codeSnippet: String = "",
    val category: String = CategoryDescription.EASY.value,
    val isSolved: Boolean = false
)

@Document(collection = "algorithmDomainModels")
data class AlgorithmDomainModel(
    @Id
    val name: String = "",
    val codeSnippet: String = "",
    val category: Category = Category(categoryDescription = ""),
    val isSolved: Boolean = true
) : Serializable


data class Category(
    val categoryDescription: String = "",
    val difficultyLevel: Int = 1,
    val tags: List<Tag> = emptyList()
)

data class Tag(val label: String = "")

enum class CategoryDescription(val value: String) {
    HARD("HARD"),
    EASY("EASY"),
    MEDIUM("MEDIUM"),
    EXTREME_PROGRAMMING("EXTREME PROGRAMMING")
}


interface AlgorithmClient {
    fun saveAlgorithm(algorithm: AlgorithmDomainModel) : com.mitchmele.algorithmcloudprocessor.result.Result<Unit, ServiceErrors>
    fun deleteAlgorithmByName(algorithmName: String) : com.mitchmele.algorithmcloudprocessor.result.Result<Unit, ServiceErrors>
}