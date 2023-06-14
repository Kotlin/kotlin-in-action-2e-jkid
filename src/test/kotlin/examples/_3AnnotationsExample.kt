package ru.yole.jkid.examples.annotationsTest

import kotlin.test.Test
import kia.jkid.JsonExclude
import kia.jkid.JsonName
import ru.yole.jkid.examples.jsonSerializerTest.testJsonSerializer

data class Person(
    @JsonName(name = "first_name") val firstName: String,
    @JsonExclude val age: Int? = null
)

class AnnotationsTest {
    @Test fun test() = testJsonSerializer(
            value = Person("Alice"),
            json = """{"first_name": "Alice"}"""
    )
}