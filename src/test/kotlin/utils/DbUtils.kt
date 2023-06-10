package utils

import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.Server
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.sql.*
import java.util.*

abstract class DbUtils<T> {

    private lateinit var connection: Connection
    private lateinit var schema: String

    constructor() {
        prepareConnection()
    }

    fun prepareConnection(): Connection {
        if (!this::connection.isInitialized) {
            val dataSource = JdbcDataSource()
            val dbProperties = File("${System.getProperty("user.dir")}/src/test/resources/db.properties")
            val fis = FileInputStream(dbProperties)
            val prop = Properties()
            prop.load(fis)
            dataSource.url = prop.getProperty("dbUrl")
            dataSource.user = prop.getProperty("dbUser")
            dataSource.password = prop.getProperty("dbPassword")
            schema = prop.getProperty("schema")
            connection = dataSource.connection
            connection.prepareStatement("CREATE SCHEMA $schema").execute()
            try {
                Server.createTcpServer("-tcpPort", "9090", "-tcpAllowOthers").start()
            } catch (e: NullPointerException) {
                println("DB server mode disabled")
            }
        }
        return connection
    }

    fun getConnection(): Connection {
        return connection
    }

    fun getAllRecordsByStringParameter(table: String, parameterName: String, parameterValue: String): ResultSet {
        val getAllRecordsByParameterQuery = "SELECT * FROM $schema.$table WHERE $parameterName='$parameterValue';"
        return connection.prepareStatement(getAllRecordsByParameterQuery).executeQuery()
    }

    fun getRecordByIntParameter(table: String, parameterName: String, parameterValue: Int): ResultSet {
        val getAllRecordsByParameterQuery = "SELECT * FROM $schema.$table WHERE $parameterName='$parameterValue';"
        return connection.prepareStatement(getAllRecordsByParameterQuery).executeQuery()
    }

    fun getSingleValueFromTable(table: String, value: String): ResultSet {
        val getSingleRecordFromTable = "SELECT $value FROM $schema.$table;"
        return connection.prepareStatement(getSingleRecordFromTable).executeQuery()
    }

    fun getAllRecords(table: String): ResultSet {
        val getAllRecordsByParameterQuery = "SELECT * FROM $schema.$table;"
        return connection.prepareStatement(getAllRecordsByParameterQuery).executeQuery()
    }

    fun deleteRecordById(table: String, id: Int) {
        val deleteRecord = "DELETE FROM $schema.$table WHERE id=$id;"
        connection.prepareStatement(deleteRecord).executeUpdate()
    }

    @Throws(SQLException::class, IOException::class)
    fun initDB(initScript: String, table: String) {
        connection.prepareStatement(readFile(initScript).replace("%s", "$schema.$table")).execute()
    }

    @Throws(IOException::class)
    fun readFile(filename: String): String {
        return File("${System.getProperty("user.dir")}/src/test/resources/dbScripts/$filename").readText().trimIndent()
    }

//    @Throws(SQLException::class)
//    fun createEmployeeBaseClass(tableName: String, data: Any): DbUtils {
//        val query = formAddRecordQuery(tableName)
//        val connection = getConnection()
//        val preparedStatement: PreparedStatement = connection.prepareStatement(query)
//        val props = data::class.java.declaredFields
//        val size = props.size
//        for (i in 1..size) {
//            preparedStatement.setObject(i, data.get(props[i]))
//        }
//        return this
//    }
//
//    private fun getColumns(table: String): MutableList<String> {
//        var namesList: MutableList<String> = mutableListOf()
//        val metadata: ResultSetMetaData = connection
//            .prepareStatement("SELECT * FROM $table;").executeQuery().metaData
//        for (i in 1..metadata.columnCount) {
//            namesList.add(metadata.getColumnName(i).toString())
//        }
//        return namesList
//    }
//
//    private fun formAddRecordQuery(table: String): String {
//        val fields = getColumns(table)
//        var fieldsToInsert = "("
//        var values = "("
//        fields.map { field ->
//            fieldsToInsert = "$fieldsToInsert $field,"
//            values = "$values ?,"
//        }
//        fieldsToInsert = fieldsToInsert.dropLast(1) + ")"
//        values = values.dropLast(1) + ")"
//
//        return "INSERT INTO $table $fieldsToInsert VALUES $values;"
//    }
}