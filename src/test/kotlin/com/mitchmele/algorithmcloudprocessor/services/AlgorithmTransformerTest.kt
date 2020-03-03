package com.mitchmele.algorithmcloudprocessor.services

import assertk.Assert
import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isEqualToIgnoringGivenProperties
import assertk.assertions.isSuccess
import assertk.assertions.support.expected
import assertk.assertions.support.show
import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModel
import com.mitchmele.algorithmcloudprocessor.store.BaseAlgorithm
import com.mitchmele.algorithmcloudprocessor.store.Category
import com.mitchmele.algorithmcloudprocessor.store.Tag
import com.mitchmele.algorithmcloudprocessor.utils.CategoryDescriptionMapper
import com.mitchmele.algorithmcloudprocessor.utils.UnitTest
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import java.util.*
import kotlin.reflect.KProperty1
import org.junit.experimental.categories.Category as TestCategory

@TestCategory(UnitTest::class)
class AlgorithmTransformerTest {

    val mockMapper: CategoryDescriptionMapper = mock {
        on { mapDescription(any()) } doReturn listOf(
            Tag("String Manipulation"),
            Tag("String Formatting"),
            Tag("Algorithms")
        )
    }

    val subject = AlgorithmTransformer(mockMapper)

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

        assertAll {
            assertThat(actual.category).isEqualTo(expectedDomainModel.category)
            assertThat(actual.name).isEqualTo(expectedDomainModel.name)
            assertThat(actual.codeSnippet).isEqualTo(expectedDomainModel.codeSnippet)
        }
    }
}

fun Assert<AlgorithmDomainModel>.hasName(expected: String) = given {
    if (this.name == expected) return
    expected("name${show(expected)} but was name: ${show(it.name)}")
}