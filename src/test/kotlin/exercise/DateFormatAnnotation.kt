package kia.jkid.exercise

import kia.jkid.deserialization.deserialize
import kia.jkid.serialization.serialize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

data class Person(
        val name: String,
        @DateFormat("dd-MM-yyyy") val birthDate: Date
)

@Ignore
class DateFormatTest {
    private val value = Person("Alice", SimpleDateFormat("dd-MM-yyyy").parse("13-02-1987"))
    private val json = """{"birthDate": "13-02-1987", "name": "Alice"}"""

    @Test fun testSerialization() {
        assertEquals(json, serialize(value))
    }

    @Test fun testDeserialization() {
        assertEquals(value, deserialize(json))
    }
}
