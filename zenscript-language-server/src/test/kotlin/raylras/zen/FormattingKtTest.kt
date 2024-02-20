package raylras.zen

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raylras.zen.lsp.provider.FormattingListener
import raylras.zen.model.lex
import raylras.zen.model.parse

class FormattingKtTest {
    private fun createParseTree(source: String): ParseTree {
        val tokenStream = lex(CharStreams.fromString(source))
        return parse(tokenStream)
    }

    private fun walk(source: String): String {
        val listener = FormattingListener()
        val parseTree = createParseTree(source)
        ParseTreeWalker.DEFAULT.walk(listener, parseTree)
        return listener.getResult();
    }

    @Test
    fun importDeclaration() {
        val source = """
            
            import  foo .    bar
            
            import foo.bar .  baz    ;
            
        """

        val expect = """
            import foo.bar;
            import foo.bar.baz;
        """.trimIndent()

        assertEquals(walk(source), expect)
    }

    @Test
    fun classDeclaration() {
        val source = """
            zenClass    A    {
            
            }
        """.trimIndent()

        val expect = """
            zenClass A {}
        """.trimIndent()

        assertEquals(walk(source), expect)
    }
}