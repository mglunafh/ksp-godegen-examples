package org.burufi.codegen.csvbuilder.processor

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class CsvBuilder(
    val separator: Char = ','
)
