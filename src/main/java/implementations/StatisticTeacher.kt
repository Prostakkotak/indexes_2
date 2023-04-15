package implementations

import dataTypes.QueryTypes
import implementations.database.DAO
import interfaces.Teacher
import utils.QueryBuilder

/**
 * Класс, реализующий интерфейс обучения для статистического анализа.
 * При получении строки, которую программа сразу не смогла распознать, разбивает ее на отдельные слова, и ищет в БД
 * строку, совпадающую с данной не менее чем на заданный в thresholdValue процент. Затем все отличающиеся слова
 * добавляются в таблицу words как контекстные синонимы, чтобы в последствии использовать их для выбора ответа.
 */
class StatisticTeacher : Teacher {
    override fun study(input: String): Boolean {
        val dao = DAO()
        var res = false
        val con = dao.openConnection("wordsdb.db")
        if (con) {
            val inputSplit = input.split(";;;")
            val inputQuestionWords = inputSplit[0].split(" ")
            val dbQuestionWords = inputSplit[1].split(" ")
            for (i in inputQuestionWords.indices) {
                if (inputQuestionWords[i] != dbQuestionWords[i]) {
                    val qAndAQuery = QueryBuilder.Builder(QueryTypes.SELECT, "words").withWhere(listOf("word"), listOf(inputQuestionWords[i])).build().toString()
                    val qAndAResponse = dao.queryExecute(qAndAQuery, QueryTypes.SELECT)
                    if (qAndAResponse != null && qAndAResponse.size < 1) {
                        val insertQuery = QueryBuilder.Builder(QueryTypes.INSERT, "words").withInsert("").withValues(listOf("'${inputQuestionWords[i]}'", "'${dbQuestionWords[i]}'")).build().toString()
                        dao.queryExecute(insertQuery, QueryTypes.INSERT)
                    }
                }
            }

            res = true
        }

        return res
    }
}
