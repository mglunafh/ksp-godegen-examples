package org.burufi.codegen.debuglog.app

import org.burufi.codegen.debuglog.processor.DebugLog

fun main(args: Array<String>) {
    println("Hello debug log!")
}

class Greeter {

    @DebugLog
    fun greet() {
        println("Hello World!")
    }

    @DebugLog
    fun greet(name: String) {
        println("Hello $name!")
    }
}
