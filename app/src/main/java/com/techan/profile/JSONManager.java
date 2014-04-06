package com.techan.profile;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collection;

// Uses persistence manager to get file contents as a string and maps it to SymbolProfile
public class JSONManager {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PersistenceManager persistenceManager;
    private SymbolProfileMap symbolProfileMap;

    public JSONManager(Context ctx) {
        persistenceManager = new PersistenceManager(ctx);

        String s = persistenceManager.read();
        if(s.equals("")) {
            symbolProfileMap = new SymbolProfileMap();
        } else {
            try {
                symbolProfileMap = objectMapper.readValue(s, SymbolProfileMap.class);
            } catch(Exception e) {
                Log.e(Constants.LOG_TAG, "Failed to construct SymbolProfileList");
                throw new RuntimeException("Failed to construct SymbolProfileList");
            }
        }
    }

    public boolean addSymbol(String symbol) {
        try {
            // Add symbol.
            symbolProfileMap.addSymbol(symbol);

            // Update file.
            return  persistenceManager.write(objectMapper.writeValueAsString(symbolProfileMap));
        } catch(Exception e) {
            Log.e(Constants.LOG_TAG, "Failed to add symbol to JSON object.");
            return false;
        }
    }

    public boolean removeSymbol(String symbol) {
        // Remove symbol.
        symbolProfileMap.removeSymbol(symbol);

        // Update file.
        try {
            return persistenceManager.write(objectMapper.writeValueAsString(symbolProfileMap));
        } catch(Exception e) {
            Log.e(Constants.LOG_TAG, "Failed to remove symbol from profile file.");
            return false;
        }
    }

    public boolean addSymbolData(SymbolProfile profile) {
        symbolProfileMap.updateSymbol(profile);

        try {
            return persistenceManager.write(objectMapper.writeValueAsString(symbolProfileMap));
        } catch(Exception e) {
            Log.e(Constants.LOG_TAG, "Failed to update json object");
            return false;
        }
    }

    public Collection<SymbolProfile> getSymbols() {
        return symbolProfileMap.getSymbolProfiles().values();
    }

    public void forceDelete() {
        persistenceManager.forceDelete();
    }

    public boolean deleteProfile() {
        symbolProfileMap = new SymbolProfileMap();
        return persistenceManager.clear();
    }
}
