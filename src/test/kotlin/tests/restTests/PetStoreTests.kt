package tests.restTests

import data.Category
import data.Pet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import utils.PetUtils

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PetStoreTests {

    private lateinit var petUtils: PetUtils
    lateinit var pet: Pet

    @BeforeAll
    fun beforeAll() {
        petUtils = PetUtils()
    }


    @BeforeEach
    fun beforeEach() {
        pet = Pet()
        petUtils.createPet(pet)
    }

    @Test
    fun checkUpdatePet() {
        val expectedPet = pet.copy(category = Category(id = 2, name = "Dog"), name = "Good Boy")
        petUtils
            .putPet(expectedPet)
        assertThat(petUtils.getPetById(pet.id)).isEqualTo(expectedPet)
    }

    @Test
    fun checkDeletePet() {
        val expectedPet = pet.copy(category = Category(id = 2, name = "Dog"), name = "Good Boy")
        petUtils
            .deletePet(expectedPet.id)
            .checkPetIsDeleted(expectedPet.id)
    }
}