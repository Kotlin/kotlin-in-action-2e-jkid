package kia.jkid

import kia.jkid.deserialization.JKidException
import kotlin.reflect.KType
import kotlin.reflect.typeOf

fun serializerForBasicType(type: KType): ValueSerializer<out Any?> {
    assert(type.isPrimitiveOrString()) { "Expected primitive type or String: $type" }
    return serializerForType(type)!!
}

fun serializerForType(type: KType): ValueSerializer<out Any?>? =
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
            else -> null
        }

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