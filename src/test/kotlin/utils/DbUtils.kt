package utils

import org.h2.jdbcx.JdbcDataSource
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

abstract class DbUtils {

    private lateinit var connection: Connection

    fun getConnection(): Connection {
        return connection
    }

    fun getAllRecordsByStringParameter(table: String, parameterName: String, parameterValue: String): ResultSet {
        val getAllRecordsByParameterQuery = "SELECT * FROM $table WHERE $parameterName='$parameterValue';"
        return connection.prepareStatement(getAllRecordsByParameterQuery).executeQuery()
    }

    fun getSingleValueFromTable(table: String, value: String): ResultSet {
        val getSingleRecordFromTable = "SELECT $value FROM $table;"
        return connection.prepareStatement(getSingleRecordFromTable).executeQuery()
    }

    fun getAllRecords(table: String): ResultSet {
        val getAllRecordsByParameterQuery = "SELECT * FROM $table';"
        return connection.prepareStatement(getAllRecordsByParameterQuery).executeQuery()
    }

    fun deleteRecordById(table: String, id: Int) {
        val deleteRecord = "DELETE FROM $table WHERE id=$id;"
        connection.prepareStatement(deleteRecord).executeUpdate()
    }

    fun prepareConnection(): Connection {
        if (this::connection.isInitialized) {
            return connection
        }
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
        return connection
    }

    @Throws(SQLException::class, IOException::class)
    fun initDB(initScript: String) {
        connection.prepareStatement(readFile(initScript)).execute()
    }

    @Throws(IOException::class)
    fun readFile(filename: String): String {
        return File("${System.getProperty("user.dir")}/src/test/resources/dbScripts/$filename").readText().trimIndent()
    }
}