package utils

import com.google.gson.Gson
import data.Pet
import io.restassured.RestAssured.given
import io.restassured.response.Response
import org.assertj.core.api.Assertions.assertThat

class PetUtils {

    private val baseUrl = "https://petstore.swagger.io/v2/pet"

    fun createPet(pet: Pet): Pet {
        val response: Response = given()
            .header("Content-type", "application/json")
            .and()
            .body(Gson().toJson(pet))
            .`when`()
            .post(baseUrl)
            .then()
            .extract().response()
        return Gson().fromJson(response.body.print(), Pet::class.java)
    }

    fun putPet(pet: Pet): PetUtils {
        given()
            .header("Content-type", "application/json")
            .and()
            .body(Gson().toJson(pet))
            .`when`()
            .put(baseUrl)
            .then()
            .statusCode(200)
        return this
    }

    fun getPetById(id: Int): Pet {
        val response: Response = given()
            .`when`()
            .get("$baseUrl/$id")
            .then()
            .extract().response()
        return Gson().fromJson(response.body.print(), Pet::class.java)
    }

    fun checkPetIsDeleted(id: Int): PetUtils {
        val response: Response = given()
            .`when`()
            .get("$baseUrl/$id")
            .then()
            .extract().response()
        assertThat(response.jsonPath().getString("message")).isEqualTo("Pet not found")
        return this
    }

    fun deletePet(id: Int): PetUtils {
        given()
            .`when`()
            .delete("$baseUrl/$id")
            .then()
            .statusCode(200)
        return this
    }
}