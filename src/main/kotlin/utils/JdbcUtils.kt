package utils

import java.io.File
import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

const val jdbcUrl = "jdbc:h2:~/test"

open class JdbcUtils {

    var connection: Connection? = null

    @Throws(SQLException::class, IOException::class)
    fun executeScript(script: String) {
        val query = connection?.prepareStatement(script)
        query?.execute()
    }

    @Throws(SQLException::class, IOException::class)
    fun createDBConnection(): Connection? {
        if (connection == null) {
            try {
                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection(jdbcUrl, "", "");
                println("Подключение успешно выполнено")
                return connection
            } catch (e: Exception) {
                e.printStackTrace()
                println("не удалось подключиться к банку")
            }
        }
        return null
    }

    @Throws(IOException::class)
    fun readFile(filename: String): String {
        return File("${System.getProperty("user.dir")}/src/main/resources/$filename").readText().trimIndent()
    }

}