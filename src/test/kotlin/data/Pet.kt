package data

import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic

data class Pet(
    val id: Int = 700,
    val category: Category = Category(),
    val name: String = randomAlphabetic(10),
    val photoUrls: List<String> = listOf("string"),
    val tags: List<Tag> = emptyList(),
    val status: String = "available"
)

data class Category(
    val id: Int = 1,
    val name: String = "Hamster"
)

data class Tag(
    val id: Int = 1,
    val name: String = "cute"
)