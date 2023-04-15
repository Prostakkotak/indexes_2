package interfaces

import dataTypes.QueryTypes

interface DAOConnection {
    /**
     * открытие подключения к БД
     *
     * @param dbName название БД
     */
    fun openConnection(dbName: String): Boolean

    /**
     * закрытие подключения к БД
     */
    fun closeConnection(): Boolean

    /**
     * выполнение запроса к БД
     *
     * @param query текст запроса
     * @param type тип запроса
     */
    fun queryExecute(query: String, type: QueryTypes): MutableList<Map<String, *>>?
}
