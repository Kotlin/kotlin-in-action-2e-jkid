package kia.jkid

import kia.jkid.deserialization.JKidException
import kia.jkid.exercise.DateFormat
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.typeOf
import java.text.SimpleDateFormat
import java.util.Date

fun serializerForBasicType(type: KType): ValueSerializer<out Any?> {
    assert(type.isPrimitiveOrString()) { "Expected primitive type or String: $type" }
    return serializerForType(type)!!
}

@Suppress("UNCHECKED_CAST")
fun serializerForType(type: KType, element: KAnnotatedElement? = null): ValueSerializer<Any?>? =
    when (type) {
        typeOf<Byte>() -> ByteSerializer
        typeOf<Short>() -> ShortSerializer
        typeOf<Int>() -> IntSerializer
        typeOf<Long>() -> LongSerializer
        typeOf<Float>() -> FloatSerializer
        typeOf<Double>() -> DoubleSerializer
        typeOf<Boolean>() -> BooleanSerializer
        typeOf<String>(),
        typeOf<String?>() -> StringSerializer
        typeOf<Date>() -> DateSerializer(element)
        else -> null
    } as ValueSerializer<Any?>?

private fun Any?.expectNumber(): Number {
    if (this !is Number) throw JKidException("Expected number, was: $this")
    return this
}

object ByteSerializer : ValueSerializer<Byte> {
    override fun fromJsonValue(jsonValue: Any?) = jsonValue.expectNumber().toByte()
    override fun toJsonValue(value: Byte) = value
}

object ShortSerializer : ValueSerializer<Short> {
    override fun fromJsonValue(jsonValue: Any?) = jsonValue.expectNumber().toShort()
    override fun toJsonValue(value: Short) = value
}

object IntSerializer : ValueSerializer<Int> {
    override fun fromJsonValue(jsonValue: Any?) = jsonValue.expectNumber().toInt()
    override fun toJsonValue(value: Int) = value
}

object LongSerializer : ValueSerializer<Long> {
    override fun fromJsonValue(jsonValue: Any?) = jsonValue.expectNumber().toLong()
    override fun toJsonValue(value: Long) = value
}

object FloatSerializer : ValueSerializer<Float> {
    override fun fromJsonValue(jsonValue: Any?) = jsonValue.expectNumber().toFloat()
    override fun toJsonValue(value: Float) = value
}

object DoubleSerializer : ValueSerializer<Double> {
    override fun fromJsonValue(jsonValue: Any?) = jsonValue.expectNumber().toDouble()
    override fun toJsonValue(value: Double) = value
}

object BooleanSerializer : ValueSerializer<Boolean> {
    override fun fromJsonValue(jsonValue: Any?): Boolean {
        if (jsonValue !is Boolean) throw JKidException("Expected boolean, was: $jsonValue")
        return jsonValue
    }

    override fun toJsonValue(value: Boolean) = value
}

object StringSerializer : ValueSerializer<String?> {
    override fun fromJsonValue(jsonValue: Any?): String? {
        if (jsonValue !is String?) throw JKidException("Expected string, was: $jsonValue")
        return jsonValue
    }

    override fun toJsonValue(value: String?) = value
}

class DateSerializer(private val annotatedElement: KAnnotatedElement? = null) : ValueSerializer<Date> {

    override fun fromJsonValue(jsonValue: Any?): Date {
        TODO("Not yet implemented")
    }

    override fun toJsonValue(value: Date): Any? {
        val dateFormat = annotatedElement?.findAnnotation<DateFormat>()?.format ?: "yyyy-MM-dd"
        return SimpleDateFormat(dateFormat).format(value)
    }
}