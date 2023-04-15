package implementations.database

import dataTypes.QueryTypes
import interfaces.DAOConnection
import utils.resultsToList
import java.sql.Connection
import java.sql.DriverManager

/**
 * класс взаимодействия с БД
 */
class DAO : DAOConnection {
    /**
     * подключение к БД
     */
    private var conn: Connection? = null
    override fun openConnection(dbName: String): Boolean {
        return try {
            conn = DriverManager.getConnection("jdbc:sqlite:$dbName")
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun closeConnection(): Boolean {
        return try {
            conn?.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun queryExecute(query: String, type: QueryTypes): MutableList<Map<String, *>>? {
        if (type == QueryTypes.SELECT) {
            val response = conn?.createStatement()?.executeQuery(query)
            if (response != null) {
                return resultsToList(response)
            }
        }
        if (type == QueryTypes.INSERT) {
            conn?.createStatement()?.executeUpdate(query)
        }
        return null
    }
}
