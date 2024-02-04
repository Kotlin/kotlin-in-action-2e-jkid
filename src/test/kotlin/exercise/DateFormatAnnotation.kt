package kia.jkid.exercise

import kia.jkid.deserialization.deserialize
import kia.jkid.serialization.serialize
import kotlin.test.Test
import kotlin.test.assertEquals
import java.text.SimpleDateFormat
import java.util.Date

data class Person(
    val name: String,
    //@CustomSerializer(DateSerializer::class)
    // due to the implementation approach (serialization uses member properties, deserialization uses primary constructor parameters)
    // the property currently has to be annotated both in relation the constructor and the class property
    @param:DateFormat("dd-MM-yyyy")
    @property:DateFormat("dd-MM-yyyy")
    val birthDate: Date
)

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
