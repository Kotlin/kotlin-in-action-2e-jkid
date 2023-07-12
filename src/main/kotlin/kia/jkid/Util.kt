package kia.jkid

import kotlin.reflect.KType
import kotlin.reflect.typeOf

fun <T> Iterable<T>.joinToStringBuilder(
    stringBuilder: StringBuilder,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    callback: ((T) -> Unit)? = null
): StringBuilder {
    return joinTo(stringBuilder, separator, prefix, postfix, limit, truncated) {
        if (callback == null) return@joinTo it.toString()
        callback(it)
        ""
    }
}

fun KType.isPrimitiveOrString(): Boolean {
    val types = setOf(
        typeOf<Byte>(),
        typeOf<Short>(),
        typeOf<Int>(),
        typeOf<Long>(),
        typeOf<Float>(),
        typeOf<Double>(),
        typeOf<Boolean>(),
        typeOf<String>(),
        typeOf<String?>(),
    )
    return this in types
}