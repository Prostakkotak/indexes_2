import implementations.StatisticAnswerChooser
import implementations.database.DAO
import interfaces.AnswerChooser
import interfaces.DAOConnection

/**
 * НЕ НУЖНО ПОДКЛЮЧАТЬСЯ К БД В МЕЙНЕ, ЭТО ЗДЕСЬ ДЛЯ ПРИМЕРА
 */
fun main() {
    val dao: DAOConnection = DAO()
    dao.openConnection("wordsdb.db")
    val answerChooser: AnswerChooser = StatisticAnswerChooser()
    println(answerChooser.getAnswer("как быстро дойти до библиотеки"))
}
