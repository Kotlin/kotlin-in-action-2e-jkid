package kia.jkid.examples.annotationsTest

import kia.jkid.JsonExclude
import kia.jkid.JsonName
import kia.jkid.examples.jsonSerializerTest.testJsonSerializer
import kotlin.test.Test

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