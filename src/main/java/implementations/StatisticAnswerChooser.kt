package implementations

import dataTypes.QueryTypes
import implementations.database.DAO
import interfaces.AnswerChooser
import utils.clearPuncts
import utils.InputUnit
import utils.QueryBuilder

/**
 * Класс, реулизующий логику выбора варианта ответа на зпрос пользователя с помощью статистического анализа
 * Строка, введенная пользователем сравнивается с сохраненными в базе данных вопросами. Затем, если совпадения найти
 * не удалось, выгружает из таблицы words все контекстные синонимы для каждого из слов запроса и создает массив
 * возможных запросов, в котором методом перебора заменяет все слова на их контекстные синонимы.
 * Каждая из этих строк сравнивается с сохраненной в таблице questions, и если удалось найти точное совпадение,
 * то возвращает на него ответ. Если совпадение пересекло пороговое значение, то возвращается ответ, а строка передается
 * в класс StatisticTeacher для обучения системы, а пользователю восвращается заранее подготовленная строка.
 */
class StatisticAnswerChooser : AnswerChooser {
    private val dao = DAO()

    private var thresholdValue: Double = 0.75
    private val defaultAnswer = "Ответа на данный вопрос нету"

    override fun getAnswer(question: String): String {
        var res =  defaultAnswer
        val conn = dao.openConnection("wordsdb.db")
        if (conn) {

            // Записываем все вопросы и ответы
            val qAndAQuery = QueryBuilder.Builder(QueryTypes.SELECT, "questions_and_answers").build().toString()
            val qAndAResponse = dao.queryExecute(qAndAQuery, QueryTypes.SELECT)

            // Чистим запрос от лишнего
            val inputWithoutMarks = this.cleanFromMarks(question)
            val inputWords = InputUnit(inputWithoutMarks).getData()!!.toMutableList()
            val cleanInputWords = this.cleanQuestion(inputWords)
            val cleanInputString = cleanInputWords.joinToString(" ")

            // Возвращаем если есть ответ
            if (qAndAResponse != null) {
                for (i in qAndAResponse.indices) {
                    val dbQuestion = qAndAResponse[i]["QUESTION"] as String
                    if (cleanInputString == dbQuestion.lowercase()) {
                        return qAndAResponse[i]["ANSWER"] as String
                    }
                }
            }

            // Обработка пар слово - контекстный синоним
            val contextSynonyms: ArrayList<ArrayList<String>> = arrayListOf()
            for (i in cleanInputWords.indices) {
                val word = cleanInputWords[i]
                val connectedWordQuery = QueryBuilder.Builder(QueryTypes.SELECT, "words")
                    .withSelect(listOf("connected_word"))
                    .withWhere(listOf("word"), listOf(word)).build().toString()
                val wordQuery = QueryBuilder.Builder(QueryTypes.SELECT, "words")
                    .withSelect(listOf("word"))
                    .withWhere(listOf("connected_word"), listOf(word)).build().toString()
                val connectedWordQueryResponse =
                    dao.queryExecute(connectedWordQuery, QueryTypes.SELECT)
                val wordToConnectedWords: ArrayList<String> = ArrayList()
                if (connectedWordQueryResponse != null) {
                    for (i in connectedWordQueryResponse.indices) {
                        wordToConnectedWords.add(connectedWordQueryResponse[i].get("CONNECTED_WORD") as String)
                    }
                }

                contextSynonyms.add(wordToConnectedWords)
                val wordQueryResponse = dao.queryExecute(wordQuery, QueryTypes.SELECT)
                val wordCompare: ArrayList<String> = ArrayList()
                if (wordQueryResponse != null) {
                    for (i in wordQueryResponse.indices) {
                        wordCompare.add(wordQueryResponse[i]["WORD"] as String)
                    }
                }

                for (words in wordCompare) {
                    contextSynonyms[i].add(words)
                }
            }

            // Создаём аналоги по запросу юзера
            val possibleUserQuestions: ArrayList<String> = arrayListOf()
            for (i in contextSynonyms.indices) {
                val synonyms = contextSynonyms[i]
                val mainWord = cleanInputWords[i]
                for (j in synonyms.indices) {
                    possibleUserQuestions.add(cleanInputString.replace(mainWord, synonyms[j]))
                }
            }

            // Поиск ответов на аналоги
            if (qAndAResponse != null) {
                val functionalWordsResponse = this.pickFunctionalWords()
                for (i in possibleUserQuestions.indices) {
                    for (j in qAndAResponse.indices) {
                        val possibleQuestionsWords = possibleUserQuestions[i].split(" ")
                        val questionString = qAndAResponse[j].get("QUESTION") as String
                        var questionWords = questionString.split(" ").toMutableList()
                        questionWords = this.cleanQuestion(questionWords);

                        // Если в запросе столько же слов, сколько и в аналоге, то рассчитываем соответсвие
                        if (possibleQuestionsWords.size == questionWords.size) {
                            var thresholdDifference = 100.0
                            val decrement = 100 / questionWords.size.toDouble()
                            for (k in questionWords.indices) {
                                if (questionWords[k] != possibleQuestionsWords[k]) {
                                    thresholdDifference -= decrement
                                }
                                if (thresholdDifference >= thresholdValue) {
                                    val teacher = StatisticTeacher()
                                    dao.closeConnection()
                                    teacher.study(possibleUserQuestions[i] + ";;;" + questionWords.joinToString(" "))
                                    return qAndAResponse[j]["ANSWER"] as String
                                }
                            }
                        }
                    }
                }
            }
            res =  defaultAnswer
        }
        return res
    }

    private fun cleanQuestion(questionWord: MutableList<String>): MutableList<String> {
        val functionalWordsResponse = this.pickFunctionalWords()
        if (functionalWordsResponse != null) {
            for (i in functionalWordsResponse.indices) {
                val funcWord = functionalWordsResponse[i]["FUNCTIONAL_WORD"] as String
                if (questionWord.contains(funcWord)) {
                    questionWord.remove(funcWord)
                }
            }
        }
        return questionWord
    }

    private fun cleanFromMarks(question: String): String {
        return clearPuncts(question.lowercase())
    }

    private fun pickFunctionalWords(): MutableList<Map<String, *>>? {
        val functionalWordsQuery = QueryBuilder.Builder(QueryTypes.SELECT, "functional_words").build().toString()
        return dao.queryExecute(functionalWordsQuery, QueryTypes.SELECT)
    }

    override fun setThresholdValue(value: Double) {
        thresholdValue = value
    }
}
