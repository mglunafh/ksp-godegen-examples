package org.burufi.codegen.csvbuilder.app

import org.burufi.codegen.csvbuilder.app.SomeStudentCsvConverter.toCsvString
import org.burufi.codegen.csvbuilder.app.StudentCsvConverter.toCsvString
import org.burufi.codegen.csvbuilder.app.StudentCsvConverter.writeCsv
import org.burufi.codegen.csvbuilder.processor.CsvBuilder
import java.io.File

fun main(args: Array<String>) {

    val alex = Student(1, "Alex", "Smith")
    val jean = Student(2, "Jean", "Stocks")
    val dorothy = Student(3, "Dorothy", "Hooks")

    val students = listOf(alex, jean, dorothy)
    students.forEach {
        println(it.toCsvString())
    }

    val file = File("students.csv")
    students.writeCsv(file.outputStream(), printHeader = true)

    val someAlex = SomeStudent(10, "Alex", "Smith")
    val someDorothy = SomeStudent(20, "Dorothy", "Hooks")
    val someStudents = listOf(someAlex, someDorothy)
    someStudents.forEach {
        println(it.toCsvString())
    }
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
