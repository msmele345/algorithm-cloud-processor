package com.mitchmele.algorithmcloudprocessor.services

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSuccess
import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModel
import com.mitchmele.algorithmcloudprocessor.store.BaseAlgorithm
import com.mitchmele.algorithmcloudprocessor.store.Category
import com.mitchmele.algorithmcloudprocessor.store.Tag
import com.mitchmele.algorithmcloudprocessor.utils.UnitTest
import org.junit.Test

@org.junit.experimental.categories.Category(UnitTest::class)
class AlgorithmTransformerTest {


    val subject =  AlgorithmTransformer()


    @Test
    fun `transform - should map fields from the baseAlgorithm object correctly to the ADM`() {

        val inputBaseAlgorithm = BaseAlgorithm(
            name = "capitalizeString",
            codeSnippet = """
                fun capitalizeString(str: String): String = str.toUpperCase() 
            """.trimIndent(),
            category = "EASY",
            isSolved = true
        )

        val expectedDomainModel = AlgorithmDomainModel(
            name = "capitalizeString",
            codeSnippet = """
                fun capitalizeString(str: String): String = str.toUpperCase() 
            """.trimIndent(),
            category = Category(
                categoryDescription = "EASY",
                difficultyLevel = 2,
                tags = listOf(
                    Tag("String Manipulation"),
                    Tag("String Formatting"),
                    Tag("Algorithms")
                )
            )
        )

        val actual = subject.transform(inputBaseAlgorithm)

        assertThat { actual }
            .isSuccess()
            .isEqualTo(expectedDomainModel)
    }
}