package com.techan.profile;

import java.util.HashMap;
import java.util.Map;

public class SymbolProfileMap {
    Map<String, SymbolProfile> symbolProfiles = new HashMap<>();

    public Map<String, SymbolProfile> getSymbolProfiles() {
        return symbolProfiles;
    }

    public void setSymbolProfiles(Map<String, SymbolProfile> symbolProfiles) {
        this.symbolProfiles = symbolProfiles;
    }

    public void addSymbol(String symbol) {
        symbolProfiles.put(symbol, new SymbolProfile(symbol));
    }

    public void removeSymbol(String symbol) {
        symbolProfiles.remove(symbol);
    }

    public void updateSymbol(SymbolProfile symbolProfile) {
        symbolProfiles.put(symbolProfile.symbol, symbolProfile);
    }
}
