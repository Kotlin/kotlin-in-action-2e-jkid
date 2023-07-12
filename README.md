# Kotlin in Action 2e: JKid Implementation

> **Note**
> This project accompanies Chapter 12, "Introspecting Kotlin Code" of the [Kotlin in Action](https://kotl.in/in-action)
> book. It is designed as an educational resource to illustrate concepts of working with annotations and reflections in
> Kotlin. For production applications that require JSON serialization and deserialization, take a look
> at [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization).

JKid is a case study of how to build a simple JSON serialization/deserialization library for Kotlin data classes. To
serialize or deserialize an object use the `serialize` and `deserialize` functions.

"Declaring and Applying Annotations" of chapter 12 describes annotations in Kotlin, and also shows how to use the
library and how to tune it with annotations.
You can find examples for this section in the folder `test/kotlin/examples`.
Five examples correspond to five subsections accordingly.
The file `kotlin/Annotations.kt` contains the declarations of discussed annotations.

The section "Reflection: Introspecting Kotlin Objects at Runtime" of the book describes the implementation of serializer
and deserializer.
The files `main/kotlin/serialization/Serializer.kt` and `main/kotlin/deserialization/Deserializer.kt` contain the source
code.

### Exercises

We highly encourage you to do the following exercises after reading the text. Solving these exercises will help you to
understand the concepts better and lets you practice right away.

#### `DateFormat` support

This first exercise can be started after reading the description of the serializer, specifically, after finishing the
section "Customizing Serialization with Annotations."

Support the annotation `DateFormat`, which allows you to annotate a date property with `@DateFormat("dd-MM-yyyy")`,
specifying the date format as an argument.
A unit test example that also illustrates its usage is in the file `test/kotlin/exercise/DateFormatAnnotation.kt`.
Remove `@Ignore` from the test `DateFormatTest` and make the test pass.
The declaration of the annotation is in the file `main/kotlin/exercise/DateFormat.kt`.
The solution to this exercise can be found in the branch `solution-date-format`.

#### Support for maps as property values

The second exercise is intended to be started after reading the whole chapter.

Currently, JKid only supports objects and collections as property values.
Add support for the serialization and deserialization of maps as property values.
You can find an unit test that serves as an example in `test/kotlin/exercise/Map.kt`.
Remove `@Ignore` from the test `MapTest` and make it pass.

To support deserialization of maps, create a class `MapSeed` similar to `ObjectSeed` and collection seeds.
The function `createSeedForType` should now return an instance of `MapSeed` if a map is expected.
The example solution can be found in the branch `solution-map`.
