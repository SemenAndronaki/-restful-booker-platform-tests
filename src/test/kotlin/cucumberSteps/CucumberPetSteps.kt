package cucumberSteps

import data.Pet
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import utils.PetUtils

class CucumberPetSteps {

    private lateinit var petUtils: PetUtils
    private lateinit var pet: Pet

    @Given("Я создаю животное для теста")
    fun createPet() {
        petUtils = PetUtils()
        pet = Pet()
        petUtils.createPet(pet)
    }

    @When("Я удаляю животное")
    fun deletePet() {
        petUtils.deletePet(pet.id)
    }

    @Then("Животное удалено")
    fun checkPetIsDeleted() {
        petUtils.checkPetIsDeleted(pet.id)
    }
}