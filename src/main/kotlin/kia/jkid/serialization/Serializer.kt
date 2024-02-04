package kia.jkid.serialization

import kia.jkid.CustomSerializer
import kia.jkid.JsonExclude
import kia.jkid.JsonName
import kia.jkid.ValueSerializer
import kia.jkid.joinToStringBuilder
import kia.jkid.serializerForType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

fun serialize(obj: Any): String = buildString { serializeObject(obj) }

/* the first implementation discussed in the book */
private fun StringBuilder.serializeObjectWithoutAnnotation(obj: Any) {
    val kClass = obj::class as KClass<Any>
    val properties = kClass.memberProperties

    properties.joinToStringBuilder(this, prefix = "{", postfix = "}") { prop ->
        serializeString(prop.name)
        append(": ")
        serializePropertyValue(prop.get(obj))
    }
}

private fun StringBuilder.serializeObject(obj: Any) {
    (obj::class as KClass<Any>)
        .memberProperties
        .filter { it.findAnnotation<JsonExclude>() == null }
        .joinToStringBuilder(this, prefix = "{", postfix = "}") {
            serializeProperty(it, obj)
        }
}

private fun StringBuilder.serializeProperty(
    prop: KProperty1<Any, *>, obj: Any
) {
    val jsonNameAnn = prop.findAnnotation<JsonName>()
    val propName = jsonNameAnn?.name ?: prop.name
    serializeString(propName)
    append(": ")

    val value = prop.get(obj)
    val propertySerializer = prop.getCustomSerializer() ?: serializerForType(prop.returnType, prop)
    val jsonValue = propertySerializer?.toJsonValue(value) ?: value
    serializePropertyValue(jsonValue)
}

fun KProperty<*>.getCustomSerializer(): ValueSerializer<Any?>? {
    val customSerializerAnn = findAnnotation<CustomSerializer>() ?: return null
    val serializerClass = customSerializerAnn.serializerClass

    val valueSerializer = serializerClass.objectInstance ?: serializerClass.createClassInstance()
    @Suppress("UNCHECKED_CAST")
    return valueSerializer as ValueSerializer<Any?>
}

// Create an instance of a class implementing [ValueSerializer]. The class to be instantiated is expected to be a
// class with a constructor accepting [KAnnotatedElement] or a class with no-param constructor.
private fun KClass<*>.createClassInstance() = constructors.firstOrNull()?.let {
    check(it.parameters.size <= 1) { "Only ValueSerializer implementation accepting 0 or 1 constructor parameter are supported" }

    val constructorParam = it.parameters.firstOrNull()?.to(this)?.let { entry -> mapOf(entry) }

    if (constructorParam != null) {
        it.callBy(constructorParam)
    }
    else null
} ?: createInstance()

private fun StringBuilder.serializePropertyValue(value: Any?) {
    when (value) {
        null -> append("null")
        is String -> serializeString(value)
        is Number, is Boolean -> append(value.toString())
        is List<*> -> serializeList(value)
        else -> serializeObject(value)
    }
}

private fun StringBuilder.serializeList(data: List<Any?>) {
    data.joinToStringBuilder(this, prefix = "[", postfix = "]") {
        serializePropertyValue(it)
    }
}

private fun StringBuilder.serializeString(s: String) {
    append('\"')
    s.forEach { append(it.escape()) }
    append('\"')
}

private fun Char.escape(): Any =
    when (this) {
        '\\' -> "\\\\"
        '\"' -> "\\\""
        '\b' -> "\\b"
        '\u000C' -> "\\f"
        '\n' -> "\\n"
        '\r' -> "\\r"
        '\t' -> "\\t"
        else -> this
    }
