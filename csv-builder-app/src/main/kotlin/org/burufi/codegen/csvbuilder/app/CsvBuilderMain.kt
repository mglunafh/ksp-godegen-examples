package org.burufi.codegen.csvbuilder.app

import org.burufi.codegen.csvbuilder.app.SomeLectureCsvConverter.writeCsv
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

    val calculusOne = SomeLecture(1, "Calculus 1", 120)
    val geometry = SomeLecture(2, "Geometry", 120)
    val discreteMath = SomeLecture(3, "Discrete math", 80)
    val concreteMath = SomeLecture(3, "Concrete math", 80)

    val lectures = listOf(calculusOne, geometry, discreteMath, concreteMath)
    val lectureFile = File("lectures.tsv")
    lectures.writeCsv(lectureFile.outputStream(), printHeader = true)
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

@CsvBuilder(separator = '\t')
data class SomeLecture(
    val index: Int,
    val name: String,
    val duration: Int
)
