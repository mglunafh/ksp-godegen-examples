package org.burufi.codegen.debuglog.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

class DebugLogProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private val signatures: MutableList<MethodSignature> = mutableListOf()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val found = resolver.getSymbolsWithAnnotation(DebugLog::class.qualifiedName!!)
            .filterIsInstance(KSFunctionDeclaration::class.java)
            .map(::extractInfo)
            .forEach { signatures.add(it) }

        println(found)
        return emptyList()
    }

    override fun finish() {
        super.finish()
        generateReport(signatures)
    }

    private fun extractInfo(funDecl: KSFunctionDeclaration): MethodSignature {
        val signature = MethodSignature(
            name = funDecl.simpleName.asString(),
            parameters = funDecl.parameters.map { it.name?.asString() ?: "_" },
            returnType = funDecl.returnType?.resolve()?.toString() ?: "Unit",
        )
        println(signature)
        return signature
    }

    private fun generateReport(signatures: List<MethodSignature>) {
        val file = codeGenerator.createNewFile(
            dependencies = Dependencies(false),
            packageName = "",
            fileName = "debuglog-report",
            extensionName = "txt",
        )

        file.bufferedWriter(Charsets.UTF_8).use { writer ->
            writer.appendLine("Method signatures")
            signatures.forEach { writer.appendLine(it.toString()) }
        }
    }

    data class MethodSignature(
        val name: String,
        val parameters: List<String>,
        val returnType: String
    )
}
