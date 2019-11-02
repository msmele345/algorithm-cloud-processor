package com.mitchmele.algorithmcloudprocessor.utils

import com.mitchmele.algorithmcloudprocessor.store.Tag
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.experimental.categories.Category

@Category(UnitTest::class)
class CategoryDescriptionMapperTest {

    val subject = CategoryDescriptionMapper()


    @Test
    fun `mapDescription - should map a category name to a list of corresponding tags`() {
        val inputName = "Strings"

        val expected = listOf(
            Tag("String Manipulation"),
            Tag("String Formatting"),
            Tag("Algorithms")
        )

        subject.mapDescription(inputName).let { actual ->
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Test
    fun `mapDescription - should return an empty list if the name does not currently have a mapping`() {
        val inputName = "nothing"

        val expected = emptyList<Tag>()
        subject.mapDescription(inputName).let { actual ->
            assertThat(actual).isEqualTo(expected)
        }
    }
}