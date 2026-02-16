package org.burufi.codegen.csvbuilder.app

import java.io.OutputStream

object StudentCsvConverter {

    const val SEPARATOR = ","

    fun convertStudent(student: Student): String {
        return "${student.index}$SEPARATOR${student.name}$SEPARATOR${student.surname}"
    }

    fun Student.toCsvString(): String {
        return "${this.index}$SEPARATOR${this.name}$SEPARATOR${this.surname}"
    }

    fun Student.asBuilder() = buildString {
        append(this@asBuilder.index).append(SEPARATOR)
        append(this@asBuilder.name).append(SEPARATOR)
        append(surname)
    }

    fun List<Student>.writeCsv(outputStream: OutputStream, printHeader: Boolean = true) {
        outputStream.bufferedWriter(Charsets.UTF_8).use {
            if (printHeader) {
                val header = "index,name,surname"
                it.appendLine(header)
            }
            forEach { student ->
                it.appendLine(student.toCsvString())
            }
        }
    }
}
