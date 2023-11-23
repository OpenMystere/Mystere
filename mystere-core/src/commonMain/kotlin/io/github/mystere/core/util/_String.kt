package io.github.mystere.core.util

fun String.smartSub(start: Int, end: Int = length): String {
    return substring(start, if (end < 0) length + end else end)
}