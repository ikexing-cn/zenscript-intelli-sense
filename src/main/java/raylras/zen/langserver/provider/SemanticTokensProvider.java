package raylras.zen.langserver.provider;

import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensParams;
import raylras.zen.code.CompilationContext;
import raylras.zen.code.CompilationUnit;
import raylras.zen.code.Listener;
import raylras.zen.util.Range;
import raylras.zen.util.Utils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SemanticTokensProvider extends Listener {

    public static SemanticTokens semanticTokensFull(CompilationContext context, SemanticTokensParams params) {
        Path documentPath = Utils.toPath(params.getTextDocument().getUri());
        CompilationUnit compilationUnit = context.getCompilationUnit(documentPath);
        SemanticTokensProvider provider = new SemanticTokensProvider(compilationUnit);
        return new SemanticTokens(provider.data);
    }

    private final CompilationUnit unit;
    private final List<Integer> data = new ArrayList<>();
    private int prevLine = Range.FIRST_LINE;
    private int prevColumn = Range.FIRST_COLUMN;

    private SemanticTokensProvider(CompilationUnit unit) {
        this.unit = unit;
    }

    private void push(Range range, int tokenType, int tokenModifiers) {
        if (range == null) return;
        int line = range.startLine - prevLine;
        int column = range.startLine == prevLine ? range.startColumn - prevColumn : range.startColumn;
        int length = range.endColumn - range.startColumn;
        prevLine = range.startLine;
        prevColumn = range.startColumn;
        data.add(line);
        data.add(column);
        data.add(length);
        data.add(tokenType);
        data.add(tokenModifiers);
    }

}
