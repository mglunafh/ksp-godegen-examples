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
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.OutputStream

class CsvBuilderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private val separatorValName = "SEPARATOR"

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(CsvBuilder::class.qualifiedName!!)
            .filterIsInstance(KSClassDeclaration::class.java)
            .forEach {
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
        val annotation = clazz.annotations.first { ann -> ann.shortName.asString() == CsvBuilder::class.simpleName }
        val separatorArg = annotation.arguments.first { arg -> arg.name?.asString() == "separator" }

        val className = clazz.simpleName.asString()

        val fnToCsvString = createFunctionToCsvString(clazz)
        val fnWriteCsv = createFunctionWriteCsv(clazz, fnToCsvString)

        val csvBuilderType = TypeSpec.objectBuilder("${className}CsvConverter")
            .addProperty(PropertySpec.builder(separatorValName, Char::class, KModifier.CONST)
                .initializer("'%L'",  separatorArg.value)
                .build()
            )
            .addFunction(fnToCsvString)
            .addFunction(fnWriteCsv)
            .build()

        return csvBuilderType
    }

    private fun createFunctionToCsvString(clazz: KSClassDeclaration): FunSpec {
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

    private fun createFunctionWriteCsv(clazz: KSClassDeclaration, fnToCsvString: FunSpec): FunSpec {
        val className = clazz.simpleName.asString()
        val packageName = clazz.packageName.asString()
        val poetClass = ClassName(packageName, className)
        val targetClass = ClassName("kotlin.collections", "List").parameterizedBy(poetClass)

        val csvHeaderStatement = clazz.getAllProperties()
            .filter { it.hasBackingField }
            .joinToString(separator = $$"${$${separatorValName}}") { it.simpleName.asString() }

        val extension = FunSpec.builder("writeCsv")
            .receiver(targetClass)
            .addParameter("outputStream", OutputStream::class)
            .addParameter(ParameterSpec.builder("printHeader", Boolean::class)
                .defaultValue("true")
                .build()
            )
            .returns(Unit::class)
            .beginControlFlow("outputStream.bufferedWriter(Charsets.UTF_8).use")
                .beginControlFlow("if (printHeader)")
                    .addStatement("val header = %P", csvHeaderStatement)
                    .addStatement("it.appendLine(header)")
                .endControlFlow()
                .beginControlFlow("forEach")
                    .addStatement("data -> it.appendLine(data.%N())", fnToCsvString)
                .endControlFlow()
            .endControlFlow()
            .build()

        return extension
    }
}