package utils

import dataTypes.Methods

/**
 *  Класс для хранения данных, вводимых пользователем
 *  @param input входная строка
 *  @param method метод поиска данных
 */
class InputUnit(input: String, method: Methods = Methods.STATISTIC) {

    /**
     * массив слов
     */
    private var unit: List<String>? = null

    private var hashedInput: String? = null

    init {
        /**
         * обычное разделение по словам для статистического выбора ответа
         */
        unit = input.split(' ')
    }

    /**
     * получения массива слов
     */
    fun getData() = unit
}
