package com.techan.profile;

import android.content.Context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProfileManager {
    private static Map<String, SymbolProfile> profiles;
    private static JSONManager jsonManager = null;

    private static void initialize(Context ctx) {
        if(jsonManager == null) {
            jsonManager = new JSONManager(ctx);

            profiles = new HashMap<String, SymbolProfile>();
            for(SymbolProfile profile : jsonManager.getSymbols()) {
                profiles.put(profile.symbol, profile);
            }
        }
    }

    public static boolean addSymbol(Context ctx, String symbol) {
        initialize(ctx);

        SymbolProfile profile = new SymbolProfile(symbol);
        profiles.put(symbol, profile);
        return jsonManager.addSymbol(symbol);
    }

    public static Collection<SymbolProfile> getSymbols(Context ctx) {
        initialize(ctx);

        return profiles.values();
    }

    public static boolean removeSymbol(Context ctx, String symbol) {
        initialize(ctx);

        profiles.remove(symbol);
        return jsonManager.removeSymbol(symbol);
    }

    public static boolean addSymbolData(SymbolProfile symbolProfile) {
        if(symbolProfile != profiles.get(symbolProfile.symbol)) {
            throw new RuntimeException("Should not have created new symbol profile object.");
        }

        return jsonManager.addSymbolData(symbolProfile);
    }

    public static SymbolProfile getSymbolData(String symbol) {
        return profiles.get(symbol);
    }

    public static void forceDelete() {
        jsonManager.forceDelete();
    }
}
