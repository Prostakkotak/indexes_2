package utils

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.util.*

fun resultsToList(resultSet: ResultSet): MutableList<Map<String, *>> {
    val md: ResultSetMetaData = resultSet.metaData
    val columns = md.columnCount
    val results: MutableList<Map<String, *>> = ArrayList()
    while (resultSet.next()) {
        val row: MutableMap<String, Any> = HashMap()
        for (i in 1..columns) {
            row[md.getColumnLabel(i).uppercase(Locale.getDefault())] = resultSet.getObject(i)
        }
        results.add(row).toString()
    }
    return results
}