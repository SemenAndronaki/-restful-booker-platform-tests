package utils

import data.Employee
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.Server
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class EmployeeDBUtils {

    private var connection: Connection

    private var lastId = 0

    private val ADD_RECORD =
        "INSERT INTO employees (id, first_name, last_name, job_title, vacation_start, vacation_end) VALUES (?, ?, ?, ?, ?, ?);"

    private val GET_ALL_EMPLOYEES = "SELECT * FROM employees;"

    private val GET_ALL_EMPLOYEES_BY_JOB_TITLE = "SELECT * FROM employees WHERE job_title='%s';"

    private val DELETE_ALL_EMPLOYEES = "DELETE FROM employees;"

    @Throws(SQLException::class)
    constructor() {
        val dataSource = JdbcDataSource()
        dataSource.url = "jdbc:h2:mem:test;MODE=MySQL"
        dataSource.user = "user"
        dataSource.password = "password"
        connection = dataSource.connection
        initDB()
        try {
            Server.createTcpServer("-tcpPort", "9090", "-tcpAllowOthers").start()
        } catch (e: NullPointerException) {
            println("DB server mode disabled")
        }
    }

    @Throws(SQLException::class, IOException::class)
    fun initDB(): EmployeeDBUtils {
        connection.prepareStatement(readFile("initDb.sql")).execute()
        return this
    }

    @Throws(SQLException::class)
    fun createEmployee(employee: Employee): EmployeeDBUtils {
        val preparedStatement: PreparedStatement = connection.prepareStatement(ADD_RECORD)
        lastId++
        preparedStatement.setInt(1, lastId)
        preparedStatement.setString(2, employee.firstName)
        preparedStatement.setString(3, employee.lastName)
        preparedStatement.setString(4, employee.jobTitle)
        preparedStatement.setDate(5, employee.vacationStart)
        preparedStatement.setDate(6, employee.vacationEnd)
        preparedStatement.executeUpdate()
        return this
    }

    @Throws(SQLException::class)
    fun getAllEmployees(): List<Employee> {
        var employees: MutableList<Employee> = arrayListOf()
        val results: ResultSet = connection.prepareStatement(GET_ALL_EMPLOYEES).executeQuery()
        while (results.next()) {
            employees.add(
                Employee(
                    id = results.getInt("id"),
                    firstName = results.getString("first_name"),
                    lastName = results.getString("last_name"),
                    jobTitle = results.getString("job_title"),
                    vacationStart = results.getDate("vacation_start"),
                    vacationEnd = results.getDate("vacation_end")
                )
            )
        }
        return employees
    }

    @Throws(SQLException::class)
    fun getAllEmployeesByJobTitle(jobTitle: String): List<Employee> {
        var employees: MutableList<Employee> = arrayListOf()
        val query = GET_ALL_EMPLOYEES_BY_JOB_TITLE.replace("%s", jobTitle)
        val results: ResultSet = connection.prepareStatement(query).executeQuery()
        while (results.next()) {
            employees.add(
                Employee(
                    id = results.getInt("id"),
                    firstName = results.getString("first_name"),
                    lastName = results.getString("last_name"),
                    jobTitle = results.getString("job_title"),
                    vacationStart = results.getDate("vacation_start"),
                    vacationEnd = results.getDate("vacation_end")
                )
            )
        }
        return employees
    }

    @Throws(SQLException::class)
    fun checkVacationDateConflict(employee: Employee): Boolean {
        val employees = getAllEmployeesByJobTitle(employee.jobTitle)
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
        return connection.prepareStatement("SELECT MAX(id) FROM employees;").executeQuery().getInt("MAX(id)")
    }

    @Throws(SQLException::class)
    fun resetDB(): EmployeeDBUtils {
        val ps = connection.prepareStatement(DELETE_ALL_EMPLOYEES)
        ps.executeUpdate()
        lastId = 0
        return this
    }

    @Throws(IOException::class)
    fun readFile(filename: String): String {
        return File("${System.getProperty("user.dir")}/src/test/resources/$filename").readText().trimIndent()
    }
}