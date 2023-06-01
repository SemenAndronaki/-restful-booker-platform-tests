package tests.dbTests

import data.Employee
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import utils.EmployeeDBUtils
import java.io.IOException
import java.sql.Date
import java.sql.SQLException
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeTests {

    private lateinit var employeeUtils: EmployeeDBUtils

    private var dbOpen = false

    @BeforeAll
    @Throws(SQLException::class, IOException::class)
    fun createEmployeeDb() {
        if (!dbOpen) {
            employeeUtils = EmployeeDBUtils()
            dbOpen = true
        }
    }

    @BeforeEach
    fun before() {
        employeeUtils
            .resetDB()
    }

    @Test
    fun checkNoVacationDateConflictSameJodDifferentVacationDate() {
        val employee1 = Employee(
            firstName = "Ivan", lastName = "Ivanov", jobTitle = "Qa",
            vacationStart = Date.valueOf("2023-05-01"),
            vacationEnd = Date.valueOf("2023-05-05")
        )
        val employee2 = Employee(
            firstName = "Petr", lastName = "Petrov", jobTitle = "Qa",
            vacationStart = Date.valueOf("2023-04-01"),
            vacationEnd = Date.valueOf("2023-04-05")
        )
        employeeUtils.createEmployee(employee1)
        employeeUtils.getLastEmployeeId()

        val conflict = employeeUtils.checkVacationDateConflict(employee2)
        assertFalse(conflict, "No conflict because of different dates")
    }

    @Test
    fun checkVacationDateConflict() {
        val employee1 = Employee(
            firstName = "Ivan", lastName = "Ivanov", jobTitle = "Qa",
            vacationStart = Date.valueOf("2023-05-01"),
            vacationEnd = Date.valueOf("2023-05-05")
        )
        val employee2 = Employee(
            firstName = "Petr", lastName = "Petrov", jobTitle = "Qa",
            vacationStart = Date.valueOf("2023-05-01"),
            vacationEnd = Date.valueOf("2023-05-05")
        )
        employeeUtils.createEmployee(employee1)

        val conflict = employeeUtils.checkVacationDateConflict(employee2)
        assertTrue(conflict, "No conflict because of different dates")
    }

    @Test
    fun checkNoVacationDateConflictDifferentJobTitle() {
        val employee1 = Employee(
            firstName = "Ivan", lastName = "Ivanov", jobTitle = "Qa",
            vacationStart = Date.valueOf("2023-05-01"),
            vacationEnd = Date.valueOf("2023-05-05")
        )
        val employee2 = Employee(
            firstName = "Petr", lastName = "Petrov", jobTitle = "java developer",
            vacationStart = Date.valueOf("2023-05-01"),
            vacationEnd = Date.valueOf("2023-05-05")
        )
        employeeUtils.createEmployee(employee1)

        val conflict = employeeUtils.checkVacationDateConflict(employee2)
        assertFalse(conflict, "No conflict because of different dates")
    }
}