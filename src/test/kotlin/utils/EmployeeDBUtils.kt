package utils

import data.Employee
import data.JobTitle
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.Server
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

class EmployeeDBUtils {

    private var connection: Connection

    private val ADD_RECORD =
        "INSERT INTO employees (id, first_name, last_name, job_title, vacation_start, vacation_end) VALUES (?, ?, ?, ?, ?, ?);"

    private val GET_ALL_EMPLOYEES = "SELECT * FROM employees;"

    private val GET_ALL_EMPLOYEES_BY_JOB_TITLE = "SELECT * FROM employees WHERE job_title='%s';"

    private val DELETE_ALL_EMPLOYEES = "DELETE FROM employees;"

    private val DELETE_EMPLOYEE = "DELETE FROM employees WHERE id=%s;"

    @Throws(SQLException::class)
    constructor() {
        val dataSource = JdbcDataSource()
        val dbProperties = File("${System.getProperty("user.dir")}/src/test/resources/db.properties")
        val fis = FileInputStream(dbProperties)
        val prop = Properties()
        prop.load(fis)
        dataSource.url = prop.getProperty("dbUrl")
        dataSource.user = prop.getProperty("dbUser")
        dataSource.password = prop.getProperty("dbPassword")
        connection = dataSource.connection
//        connection.schema = prop.getProperty("schema")
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
        val results: ResultSet = connection.prepareStatement(GET_ALL_EMPLOYEES).executeQuery()
        while (results.next()) {
            employees.add(
                Employee(
                    id = results.getInt("id"),
                    firstName = results.getString("first_name"),
                    lastName = results.getString("last_name"),
                    jobTitle = JobTitle.valueOf(results.getString("job_title")),
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
                    jobTitle = JobTitle.valueOf(results.getString("job_title")),
                    vacationStart = results.getDate("vacation_start"),
                    vacationEnd = results.getDate("vacation_end")
                )
            )
        }
        return employees
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
        val result: ResultSet = connection.prepareStatement("SELECT MAX(id) FROM employees;").executeQuery()
        result.next()
        return result.getInt(1)
    }

    @Throws(SQLException::class)
    fun cleanEmployess(employeesToDelete: List<Employee>): EmployeeDBUtils {
        for (employee in employeesToDelete) {
            val query = DELETE_EMPLOYEE.replace("%s", employee.id.toString())
            val ps = connection.prepareStatement(query)
            ps.executeUpdate()
        }
        return this
    }

    @Throws(IOException::class)
    fun readFile(filename: String): String {
        return File("${System.getProperty("user.dir")}/src/test/resources/$filename").readText().trimIndent()
    }
}