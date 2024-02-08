package kia.jkid.deserialization

import kia.jkid.isPrimitiveOrString
import kia.jkid.serializerForBasicType
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import java.io.Reader
import java.io.StringReader

inline fun <reified T: Any> deserialize(json: String): T {
    return deserialize(StringReader(json))
}

inline fun <reified T: Any> deserialize(json: Reader): T {
    return deserialize(json, T::class)
}

fun <T: Any> deserialize(json: Reader, targetClass: KClass<T>): T {
    val seed = ObjectSeed(targetClass, ClassInfoCache())
    Parser(json, seed).parse()
    return seed.spawn()
}

interface JsonObject {
    fun setSimpleProperty(propertyName: String, value: Any?)

    fun createObject(propertyName: String): JsonObject

    fun createArray(propertyName: String): JsonObject
}

interface Seed: JsonObject {
    val classInfoCache: ClassInfoCache

    fun spawn(): Any?

    fun createCompositeProperty(propertyName: String, isList: Boolean): JsonObject

    override fun createObject(propertyName: String) = createCompositeProperty(propertyName, false)

    override fun createArray(propertyName: String) = createCompositeProperty(propertyName, true)
}

private fun isSubtypeOfList(type: KType): Boolean {
    val listType: KType = List::class.starProjectedType
    return type.isSubtypeOf(listType)
}

private fun isSubtypeOfMap(type: KType): Boolean {
    val listType: KType = Map::class.starProjectedType
    return type.isSubtypeOf(listType)
}

private fun getParameterizedType(type: KType): KType {
    return type.arguments.single().type!!
}

fun Seed.createSeedForType(paramType: KType, isList: Boolean): Seed {
    val paramClass = paramType.classifier as KClass<out Any>
    if (isSubtypeOfList(paramType)) {
        println("It's a list!")
        if (!isList) throw JKidException("An array expected, not a composite object")

        val elementType = getParameterizedType(paramType)
        if (elementType.isPrimitiveOrString()) {
            return ValueListSeed(elementType, classInfoCache)
        }
        return ObjectListSeed(elementType, classInfoCache)
    }
    if (isList) throw JKidException("Object of the type $paramType expected, not an array")
    if (isSubtypeOfMap(paramType)) {
        println("It's a map!")
        val elementType = paramType.arguments[1].type!!
        if (elementType.isPrimitiveOrString()) {
            return ValueMapSeed(elementType, classInfoCache)
        }
        return ObjectMapSeed(elementType, classInfoCache)
    }
    return ObjectSeed(paramClass, classInfoCache)
}


class ObjectSeed<out T: Any>(
        targetClass: KClass<T>,
        override val classInfoCache: ClassInfoCache
) : Seed {

    private val classInfo: ClassInfo<T> = classInfoCache[targetClass]

    private val valueArguments = mutableMapOf<KParameter, Any?>()
    private val seedArguments = mutableMapOf<KParameter, Seed>()

    private val arguments: Map<KParameter, Any?>
        get() = valueArguments + seedArguments.mapValues { it.value.spawn() }

    override fun setSimpleProperty(propertyName: String, value: Any?) {
        val param = classInfo.getConstructorParameter(propertyName)
        valueArguments[param] = classInfo.deserializeConstructorArgument(param, value)
    }

    override fun createCompositeProperty(propertyName: String, isList: Boolean): Seed {
        val param = classInfo.getConstructorParameter(propertyName)
        val deserializeAs = classInfo.getDeserializeClass(propertyName)?.starProjectedType
        val seed = createSeedForType(
            deserializeAs ?: param.type, isList
        )
        return seed.apply { seedArguments[param] = this }
    }

    override fun spawn(): T = classInfo.createInstance(arguments)
}

class ObjectListSeed(
    val elementType: KType,
    override val classInfoCache: ClassInfoCache
) : Seed {
    private val elements = mutableListOf<Seed>()

    override fun setSimpleProperty(propertyName: String, value: Any?) {
        throw JKidException("Found primitive value in collection of object types")
    }

    override fun createCompositeProperty(propertyName: String, isList: Boolean) =
            createSeedForType(elementType, isList).apply { elements.add(this) }

    override fun spawn(): List<*> = elements.map { it.spawn() }
}

class ObjectMapSeed(
    private val elementType: KType,
    override val classInfoCache: ClassInfoCache
): Seed {
    private val internalMap = mutableMapOf<String, Seed>()

    override fun spawn(): Map<*,*> =
        internalMap.entries.associate { entry ->
            entry.key to entry.value.spawn()
        }

    override fun createCompositeProperty(propertyName: String, isList: Boolean) =
        createSeedForType(elementType, isList).apply {
            internalMap[propertyName] = this
        }

    override fun setSimpleProperty(propertyName: String, value: Any?) {
        throw JKidException("Found primitive value in map of object types")
    }

}

class ValueListSeed(
    elementType: KType,
    override val classInfoCache: ClassInfoCache
) : Seed {
    private val elements = mutableListOf<Any?>()
    private val serializerForType = serializerForBasicType(elementType)

    override fun setSimpleProperty(propertyName: String, value: Any?) {
        elements.add(serializerForType.fromJsonValue(value))
    }

    override fun createCompositeProperty(propertyName: String, isList: Boolean): Seed {
        throw JKidException("Found object value in collection of primitive types")
    }

    override fun spawn() = elements
}

class ValueMapSeed(
    elementType: KType,
    override val classInfoCache: ClassInfoCache
) : Seed {
    private val elements = mutableMapOf<String, Any?>()
    private val serializerForType = serializerForBasicType(elementType)

    override fun setSimpleProperty(propertyName: String, value: Any?) {
        elements[propertyName] = serializerForType.fromJsonValue(value)
    }

    override fun createCompositeProperty(propertyName: String, isList: Boolean): Seed {
        throw JKidException("Found object value in map of primitive types")
    }

    override fun spawn() = elements
}