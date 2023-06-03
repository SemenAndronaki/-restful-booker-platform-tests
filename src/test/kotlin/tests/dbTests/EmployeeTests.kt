package tests.dbTests

import data.Employee
import data.JobTitle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import utils.EmployeeDBUtils
import java.io.IOException
import java.sql.Date
import java.sql.SQLException


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeTests {

    private lateinit var employeeUtils: EmployeeDBUtils

    private lateinit var employeesToDelete: List<Employee>

    @BeforeAll
    @Throws(SQLException::class, IOException::class)
    fun createEmployeeDb() {
        employeeUtils = EmployeeDBUtils()
    }

    @AfterEach
    fun after() {
        employeeUtils
            .cleanEmployess(employeesToDelete)
    }

    companion object {
        @JvmStatic
        fun arguments(): List<Arguments> {
            val employee1 = Employee(
                jobTitle = JobTitle.QA,
                vacationStart = Date.valueOf("2023-05-01"),
                vacationEnd = Date.valueOf("2023-05-05")
            )
            return listOf(
                Arguments.of(
                    employee1, employee1.copy(
                        vacationStart = Date.valueOf("2023-04-01"),
                        vacationEnd = Date.valueOf("2023-04-05")
                    ), false
                ),
                Arguments.of(
                    employee1, employee1.copy(
                        vacationStart = Date.valueOf("2023-05-01"),
                        vacationEnd = Date.valueOf("2023-05-05")
                    ), true
                ),
                Arguments.of(
                    employee1, employee1.copy(jobTitle = JobTitle.JAVA_DEVELOPER), false
                )
            )
        }
    }

    @ParameterizedTest
    @MethodSource("arguments")
    fun checkVacationDataConflicts(employee1: Employee, employee2: Employee, expectedConflict: Boolean) {
        employeeUtils.createEmployee(employee1)

        val actualConflict = employeeUtils.getVacationDateConflict(employee2)
        assertThat(expectedConflict).isEqualTo(actualConflict)
        employeesToDelete = listOf(employee1, employee2)
    }
}