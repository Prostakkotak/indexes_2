package utils

fun clearPuncts(str: String): String {
    return str.replace("\\p{Punct}".toRegex(), "")
}