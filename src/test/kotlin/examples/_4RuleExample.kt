package kia.jkid.examples.rule

import kia.jkid.serialization.serialize
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals

data class Person(val name: String, val age: Int)

class WriteJsonToFileTest {

    @Test fun testUsingTempFolder(@TempDir folder: Path) {
        val person = Person("Alice", 29)
        val json = """{"age": 29, "name": "Alice"}"""
        val jsonFile = (folder.resolve("person.json")).createFile()
        jsonFile.writeText(serialize(person))
        assertEquals(json, jsonFile.readText())
    }
}