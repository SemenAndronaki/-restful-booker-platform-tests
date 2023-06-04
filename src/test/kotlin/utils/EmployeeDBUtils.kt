package utils

import data.Employee
import data.JobTitle
import org.h2.tools.Server
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

class EmployeeDBUtils : DbUtils {

    private val initScript = "initEmployeeDb.sql"
    private val tableName = "employees"

    private val ADD_RECORD =
        "INSERT INTO employees (id, first_name, last_name, job_title, vacation_start, vacation_end) VALUES (?, ?, ?, ?, ?, ?);"

    @Throws(SQLException::class)
    constructor() {
        prepareConnection()
        initDB(initScript)
        try {
            Server.createTcpServer("-tcpPort", "9090", "-tcpAllowOthers").start()
        } catch (e: NullPointerException) {
            println("DB server mode disabled")
        }
    }

    @Throws(SQLException::class)
    fun createEmployee(employee: Employee): EmployeeDBUtils {
        val connection = getConnection()
        val preparedStatement: PreparedStatement = connection.prepareStatement(ADD_RECORD)
        preparedStatement.setInt(1, getLastEmployeeId() + 1)
        preparedStatement.setString(2, employee.firstName)
        preparedStatement.setString(3, employee.lastName)
        preparedStatement.setString(4, employee.jobTitle.toString())
        preparedStatement.setDate(5, employee.vacationStart)
        preparedStatement.setDate(6, employee.vacationEnd)
        preparedStatement.executeUpdate()
        return this
    }

    @Throws(SQLException::class)
    fun getAllEmployees(): List<Employee> {
        var employees: MutableList<Employee> = arrayListOf()
        val results: ResultSet = getAllRecords(tableName)
        while (results.next()) {
            employees.add(convertResultToEmployee(results))
        }
        return employees
    }

    @Throws(SQLException::class)
    fun getAllEmployeesByJobTitle(jobTitle: String): List<Employee> {
        var employees: MutableList<Employee> = arrayListOf()
        val results: ResultSet = getAllRecordsByStringParameter(tableName, "job_title", jobTitle)
        while (results.next()) {
            employees.add(convertResultToEmployee(results))
        }
        return employees
    }

    private fun convertResultToEmployee(result: ResultSet): Employee {
        return Employee(
            id = result.getInt("id"),
            firstName = result.getString("first_name"),
            lastName = result.getString("last_name"),
            jobTitle = JobTitle.valueOf(result.getString("job_title")),
            vacationStart = result.getDate("vacation_start"),
            vacationEnd = result.getDate("vacation_end")
        )
    }

    @Throws(SQLException::class)
    fun getVacationDateConflict(employee: Employee): Boolean {
        val employees = getAllEmployeesByJobTitle(employee.jobTitle.toString())
        if (employees.isEmpty()) return false
        for (e in employees) {
            when {
                employee.vacationEnd!!.before(e.vacationStart) -> return false
                employee.vacationStart!!.after(e.vacationEnd) -> return false
            }
        }
        return true
    }

    @Throws(SQLException::class)
    fun getLastEmployeeId(): Int {
        val result: ResultSet = getSingleValueFromTable(tableName, "MAX(id)")
        result.next()
        return result.getInt(1)
    }

    @Throws(SQLException::class)
    fun cleanEmployess(employeesToDelete: List<Employee>): EmployeeDBUtils {
        for (employee in employeesToDelete) {
            deleteRecordById(tableName, employee.id)
        }
        return this
    }

}