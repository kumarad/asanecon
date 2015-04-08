package com.techan.profile;

import android.content.Context;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProfileManager {
    private static SymbolProfileManager symbolProfileManager = null;
    private static PortfolioManager portfolioManager = null;

    private static void initialize(Context ctx) {
        if(symbolProfileManager == null) {
            symbolProfileManager = new SymbolProfileManager(ctx);
        }

        if(portfolioManager == null) {
            portfolioManager = new PortfolioManager(ctx);
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

    public static boolean addPortfolio(Context ctx, String portfolio) {
        initialize(ctx);
        return portfolioManager.addPortfolio(portfolio);
    }

    public static Map<String, Portfolio> getPortfolios(Context ctx) {
        initialize(ctx);
        return portfolioManager.getPortfolios();
    }

    public static boolean removePortfolio(Context ctx, String portfolio) {
        initialize(ctx);
        return portfolioManager.removePortfolio(portfolio);
    }

    public static boolean addSymbolToPortfolio(Context ctx, String portfolioName, String symbol) {
        initialize(ctx);
        return portfolioManager.addSymbolToPortfolio(portfolioName, symbol);
    }

    public static boolean removeSymbolFromPortfolio(Context ctx, String portfolioName, String symbol) {
        initialize(ctx);
        return portfolioManager.removeSymbolFromPortfolio(portfolioName, symbol);
    }

    public static boolean deletePortfolio(Context ctx, String portfolioName, boolean deleteAllStocks) {
        initialize(ctx);
        return portfolioManager.deletePortfolio(portfolioName, deleteAllStocks);
    }

    public static boolean deleteProfile(Context ctx) {
        initialize(ctx);
        return symbolProfileManager.deleteProfile() && portfolioManager.deletePortfolios();
    }
}
