package org.burufi.codegen.csvbuilder.app

import org.burufi.codegen.csvbuilder.app.SomeStudentCsvConverter.toCsvString
import org.burufi.codegen.csvbuilder.app.SomeStudentCsvConverter.writeCsv
import org.burufi.codegen.csvbuilder.processor.CsvBuilder
import java.io.File

fun main(args: Array<String>) {

    val someAlex = SomeStudent(10, "Alex", "Smith")
    val someDorothy = SomeStudent(20, "Dorothy", "Hooks")
    val dorothy = SomeStudent(3, "Dorothy", "Hooks")
    val someStudents = listOf(someAlex, someDorothy, dorothy)
    someStudents.forEach {
        println(it.toCsvString())
    }
    val file = File("students.csv")
    someStudents.writeCsv(file.outputStream(), printHeader = false)
}

data class Student(
    val index: Int,
    val name: String,
    val surname: String
)


@CsvBuilder
data class SomeStudent(
    val index: Int,
    val name: String,
    val surname: String
)

@CsvBuilder
data class SomeLecture(
    val index: Int,
    val name: String,
    val duration: Int
)
