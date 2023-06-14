package ru.yole.jkid.examples.simple

import kia.jkid.deserialization.deserialize
import kia.jkid.serialization.serialize
import kotlin.test.Test
import kotlin.test.assertEquals

data class Person(val name: String, val age: Int)

class PersonTest {
    @Test fun test() {
        val person = Person("Alice", 29)
        val json = """{"age": 29, "name": "Alice"}"""

        assertEquals(json, serialize(person))
        assertEquals(person, deserialize(json))
    }
}