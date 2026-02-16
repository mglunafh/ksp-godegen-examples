package org.burufi.codegen.csvbuilder.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

class CsvBuilderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private val separatorValName = "SEPARATOR"

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(CsvBuilder::class.qualifiedName!!)
            .filterIsInstance(KSClassDeclaration::class.java)
            .forEach {
                println(it)
                val packageName = it.packageName.asString()
                val csvBuilderType = createCsvBuilder(it)
                val builderName = csvBuilderType.name!!

                val fileSpec = FileSpec.builder(packageName, builderName)
                    .addType(csvBuilderType)
                    .build()

                val csvBuilderFile = codeGenerator.createNewFile(
                    Dependencies(false, *it.containingFile?.let { arrayOf(it) } ?: emptyArray()),
                    packageName,
                    builderName
                )
                csvBuilderFile.bufferedWriter(Charsets.UTF_8).use { writer ->
                    fileSpec.writeTo(writer)
                }
            }

        return emptyList()
    }

    private fun createCsvBuilder(clazz: KSClassDeclaration): TypeSpec {
        val className = clazz.simpleName.asString()

        val csvBuilderType = TypeSpec.objectBuilder("${className}CsvConverter")
            .addProperty(PropertySpec.builder(separatorValName, String::class, KModifier.CONST)
                .initializer("%S", ",")
                .build()
            )
            .addFunction(createExtensionFunction(clazz))
            .build()

        return csvBuilderType
    }

    private fun createExtensionFunction(clazz: KSClassDeclaration): FunSpec {
        val className = clazz.simpleName.asString()
        val packageName = clazz.packageName.asString()
        val poetClass = ClassName(packageName, className)

        val csvStringStatement = clazz.getAllProperties()
            .filter { it.hasBackingField }
            .joinToString(separator = $$"$$$separatorValName") { $$"${$${it.simpleName.asString()}}" }

        val extension = FunSpec.builder("toCsvString")
            .receiver(poetClass)
            .returns(String::class)
            .addStatement("return %P", csvStringStatement)
            .build()

        return extension
    }
}