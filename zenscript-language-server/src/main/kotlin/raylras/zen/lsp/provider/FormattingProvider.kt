package raylras.zen.lsp.provider

import org.eclipse.lsp4j.DocumentFormattingParams
import org.eclipse.lsp4j.TextEdit
import raylras.zen.model.CompilationUnit
import raylras.zen.model.Listener
import raylras.zen.model.parser.ZenScriptParser
import raylras.zen.model.parser.ZenScriptParser.ImportDeclarationContext
import raylras.zen.model.parser.ZenScriptParser.QualifiedNameContext
import raylras.zen.util.textRange
import raylras.zen.util.toLspRange

object FormattingProvider {
    fun formatting(unit: CompilationUnit, params: DocumentFormattingParams): List<TextEdit> {
        val listener = FormattingListener()
        unit.accept(listener)
        return listOf(TextEdit(unit.parseTree.textRange.toLspRange(), listener.getResult()))
    }

}

enum class Operation {
    IMPORT,
    CLASS
}

class FormattingListener() : Listener() {
    private val indent = 2

    private lateinit var currentOperation: Operation
    private val imports: MutableList<StringBuilder> = mutableListOf()
    private val classes: MutableList<StringBuilder> = mutableListOf()

    fun getResult(): String {
        val result = StringBuilder()
        val _imports = imports.joinToString("\n") { it.toString() }
        val _classes = classes.joinToString("\n") { it.toString() }

        if (_imports.isNotEmpty()) {
            result.append(_imports)
        }
        if (_classes.isNotEmpty()) {
            result.append(_classes)
        }
        return result.toString()
    }

    override fun enterImportDeclaration(ctx: ImportDeclarationContext) {
        this.currentOperation = Operation.IMPORT
        this.imports.add(StringBuilder("import "))
    }

    override fun enterQualifiedName(ctx: QualifiedNameContext) {
        when (this.currentOperation) {
            Operation.IMPORT ->
                this.imports[this.imports.size - 1].append(ctx.simpleName().joinToString(".") { it.text })

            Operation.CLASS ->
                this.classes[this.classes.size - 1].append(ctx.simpleName().joinToString(".") { it.text })
        }

    }

    override fun exitImportDeclaration(ctx: ImportDeclarationContext) {
        this.imports[this.imports.size - 1].append(";")
    }

    override fun enterClassDeclaration(ctx: ZenScriptParser.ClassDeclarationContext) {
        this.currentOperation = Operation.CLASS
        this.classes.add(StringBuilder("""zenClass ${ctx.simpleClassName().text}"""))
    }

    override fun enterClassBody(ctx: ZenScriptParser.ClassBodyContext?) {
        this.classes[this.classes.size - 1].append(" {")
    }

    override fun exitClassDeclaration(ctx: ZenScriptParser.ClassDeclarationContext) {
        this.classes[this.classes.size - 1].append("}")
    }

}
