package utils

/**
 * Сортировщик ввода
 * @param unit данные для обработки
 */
class SortInput(private val unit: InputUnit) {

    private var result: List<String>? = unit.getData()?.distinct()
    private var frequency = result?.size?.let { IntArray(it) { 1 } }

    init {
        sort()
    }

    /**
     * сортировка данных
     */
    private fun sort() {
        val data = unit.getData()

        if (data != null && result != null && frequency != null) {

            for ((count, i) in result!!.withIndex()) {
                if (i == data[count])
                    frequency!![count]++
            }
        }
    }

    /**
     * получить список уникальных слов
     */
    fun getUniq() = result
    /**
     * получить частоту уникальных слов
     */
    fun getFreq() = frequency
    /**
     * получить массив слов из исходной строки
     *
     * @param input входная строка
     */
    fun split(input: String) = input.split(" ")
}
