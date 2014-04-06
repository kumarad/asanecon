package com.techan.profile;

import android.content.Context;

import java.util.Collection;

public class ProfileManager {
    private static SymbolProfileManager symbolProfileManager = null;

    private static void initialize(Context ctx) {
        if(symbolProfileManager == null) {
            symbolProfileManager = new SymbolProfileManager(ctx);
        }
    }

    public static boolean addSymbol(Context ctx, String symbol) {
        initialize(ctx);
        return symbolProfileManager.addSymbol(symbol);
    }

    public static Collection<SymbolProfile> getSymbols(Context ctx) {
        initialize(ctx);
        return symbolProfileManager.getSymbols().values();
    }

    public static boolean removeSymbol(Context ctx, String symbol) {
        initialize(ctx);
        return symbolProfileManager.removeSymbol(symbol);
    }

    public static boolean addSymbolData(SymbolProfile symbolProfile) {
        if(symbolProfile != symbolProfileManager.getSymbol(symbolProfile.symbol)) {
            throw new RuntimeException("Should not have created new symbol profile object.");
        }

        return symbolProfileManager.addSymbolData(symbolProfile);
    }

    public static SymbolProfile getSymbolData(Context ctx, String symbol) {
        initialize(ctx);
        return symbolProfileManager.getSymbol(symbol);
    }

    public static void forceDelete(Context ctx) {
        initialize(ctx);
        symbolProfileManager.forceDelete();
    }

    public static boolean deleteProfile(Context ctx) {
        initialize(ctx);
        return symbolProfileManager.deleteProfile();
    }
}
