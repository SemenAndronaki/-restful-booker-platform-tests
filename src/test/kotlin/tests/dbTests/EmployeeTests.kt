package tests.dbTests

import data.Employee
import data.JobTitle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.*
import utils.EmployeeDBUtils
import java.io.IOException
import java.sql.Date
import java.sql.SQLException


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeTests {

    private lateinit var employeeUtils: EmployeeDBUtils

    private var employeesToDelete: MutableList<Employee> = mutableListOf()

    private val defaultEmployee = Employee(
        jobTitle = JobTitle.QA,
        vacationStart = Date.valueOf("2023-05-01"),
        vacationEnd = Date.valueOf("2023-05-05")
    )

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

    @Test
    fun checkCantCreateEmployeesWithSameId() {
        val employee1 = Employee(id = 1)
        val employee2 = Employee(id = 1)
        createEmployee(employee1)
        createEmployee(employee2)
        val employees = employeeUtils.getAllEmployees()
        assertThat(employees[0].id).isNotEqualTo(employees[1].id)
    }

    @ParameterizedTest
    @MethodSource("arguments")
    fun checkVacationDataConflicts(employee1: Employee, employee2: Employee, expectedConflict: Boolean) {
        createEmployee(employee1)
        val actualConflict = employeeUtils.getVacationDateConflict(employee2)
        assertThat(expectedConflict).isEqualTo(actualConflict)
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/employeeTest.csv"], numLinesToSkip = 1)
    fun checkVacationDataConflicts_CSVSource(
        jobTitle: String,
        vacationStart: String,
        vacationEnd: String,
        conflict: Boolean
    ) {
        createEmployee(defaultEmployee)
        val employee2 = Employee(
            jobTitle = JobTitle.valueOf(jobTitle),
            vacationStart = Date.valueOf(vacationStart),
            vacationEnd = Date.valueOf(vacationEnd)
        )
        val actualConflict = employeeUtils.getVacationDateConflict(employee2)
        assertThat(conflict).isEqualTo(actualConflict)
    }

    private fun createEmployee(employee: Employee) {
        employeesToDelete.add(employeeUtils.createEmployee(employee))
    }
}