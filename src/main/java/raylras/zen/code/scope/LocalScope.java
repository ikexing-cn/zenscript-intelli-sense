package raylras.zen.code.scope;

import raylras.zen.code.symbol.Symbol;
import raylras.zen.code.tree.TreeNode;

import java.util.*;

public class LocalScope extends Scope {

    public Map<String, List<Symbol>> symbolTable;
    public LocalScope parent;

    public LocalScope(LocalScope parent, TreeNode owner) {
        super(owner);
        this.parent = parent;
        this.symbolTable = new HashMap<>();
    }

    @Override
    public void add(Symbol symbol) {
        symbolTable.computeIfAbsent(symbol.name, name -> new ArrayList<>()).add(symbol);
    }

    @Override
    public void remove(Symbol symbol) {
        symbolTable.computeIfAbsent(symbol.name, name -> new ArrayList<>()).remove(symbol);
    }

    @Override
    public List<Symbol> getSymbols(boolean recursive) {
        // TODO
        return Collections.emptyList();
    }

    @Override
    public List<Symbol> getSymbolsByName(String name, boolean recursive) {
        List<Symbol> symbols = new ArrayList<>(symbolTable.getOrDefault(name, Collections.emptyList()));
        if (recursive && parent != null) {
            symbols.addAll(parent.getSymbolsByName(name, recursive));
        }
        return symbols;
    }

}
