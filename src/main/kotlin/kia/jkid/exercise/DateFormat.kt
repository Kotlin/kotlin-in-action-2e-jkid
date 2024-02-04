package kia.jkid.exercise

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
annotation class DateFormat(val format: String)
