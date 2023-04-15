package interfaces

import utils.SortInput

/**
 * Интерфейс для класса, взаимодействующего с базой данных для статистического анализа
 */
interface StatisticDataRequest {
    /**
     * Сеттер объекта взаимодействия с БД
     * @param dao объект для взаимодействия с БД
     */
    fun setConnection(dao: DAOConnection)
    /**
     * возвращает отличающееся слово, и слово, которое оно заменило
     * @param sorter утилитарный класс для обработки запросов
     * @param input слово, которое нужно сравнить
     */
    fun findAnalogue(sorter: SortInput, input: String): List<String>?
    /**
     * записывает новые слова в базу
     * @param input слова, которые нужно записать в БД
     */
    fun setNewWord(input: List<String>): String?
}
