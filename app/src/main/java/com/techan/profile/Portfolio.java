package com.techan.profile;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Portfolio {
    private String name;
    private HashSet<String> symbols;

    public Portfolio(String name) {
        this.name = name;
    }

    public Portfolio() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getSymbols() {
        if(symbols == null) {
            return Collections.emptySet();
        }

        return symbols;
    }

    public void setSymbols(HashSet<String> symbols) {
        this.symbols = symbols;
    }

    public void addSymbol(String symbol) {
        if(symbols == null) {
            symbols = new HashSet<>();
        }

        symbols.add(symbol);
    }

    public void removeSymbol(String symbol) {
        if(symbols == null) {
            return;
        }

        symbols.remove(symbol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Portfolio portfolio = (Portfolio) o;

        if (!name.equals(portfolio.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
