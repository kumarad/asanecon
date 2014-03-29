package com.techan.profile;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JSONManager {
    private final PersistenceManager persistenceManager;
    private JSONObject jsonObject;

    public JSONManager(Context ctx) {
        persistenceManager = new PersistenceManager(ctx);

        String s = persistenceManager.read();
        if(s.equals("")) {
            jsonObject = new JSONObject();
        } else {
            try {
                jsonObject = new JSONObject(s);
            } catch(JSONException e) {
                Log.e(Constants.LOG_TAG, "Failed to construct JSONObject");
                throw new RuntimeException("Failed to construct JSONObject");
            }
        }
    }

    public boolean addSymbol(String symbol) {
        try {
            // Add symbol.
            jsonObject.put(symbol, new JSONObject());

            // Update file.
            return  persistenceManager.write(jsonObject.toString());
        } catch(JSONException e) {
            Log.e(Constants.LOG_TAG, "Failed to add symbol to JSON object.");
            return false;
        }
    }

    public boolean removeSymbol(String symbol) {
        // Add symbol.
        jsonObject.remove(symbol);

        // Update file.
        return  persistenceManager.write(jsonObject.toString());
    }

    public boolean addSymbolData(SymbolProfile profile) {
        JSONObject symbolJson = new JSONObject();

        boolean status = true;
        for(Field f : SymbolProfile.class.getFields()) {
            SymbolProfileMember annotation = f.getAnnotation(SymbolProfileMember.class);
            if(annotation != null) {
                try {
                    Object fieldValue = f.get(profile);
                    if(fieldValue != null) {
                        try {
                            symbolJson.put(annotation.memberName(), fieldValue);
                        } catch(JSONException e) {
                            Log.e(Constants.LOG_TAG, "Failed to add data.");
                            // keep going.
                            status = false;
                        }
                    }
                } catch(IllegalAccessException e) {
                    throw new RuntimeException("Should never have issues accessing fields.");
                }
            }
        }

        try {
            jsonObject.put(profile.symbol, symbolJson);
        } catch(JSONException e) {
            Log.e(Constants.LOG_TAG, "Failed to update json object");
            return false;
        }


        if(!persistenceManager.write(jsonObject.toString())) {
            status = false;
        }

        return status;
    }

    public List<SymbolProfile> getSymbols() {
        List<SymbolProfile> profiles = new ArrayList<SymbolProfile>();
        try {
            // Populate list.
            Iterator iter = jsonObject.keys();
            while(iter.hasNext()) {
                String symbol = (String)iter.next();
                JSONObject symbolJson = jsonObject.getJSONObject(symbol);
                if(symbolJson != null) {
                    SymbolProfile sp = getSymbolProfile(symbol, symbolJson);
                    profiles.add(sp);
                } else {
                    profiles.add(new SymbolProfile(symbol));
                }
            }

            return profiles;
        } catch(JSONException e) {
            Log.e(Constants.LOG_TAG, "Failed to add symbol to JSON object.");
            return null;
        }
    }

    private SymbolProfile getSymbolProfile(String symbol, JSONObject symbolJson) throws JSONException {
        SymbolProfile symbolProfile = new SymbolProfile(symbol);

        Iterator subIter = symbolJson.keys();
        while(subIter.hasNext()) {
            String key = (String)subIter.next();
            try {
                Field f = SymbolProfile.class.getField(SymbolProfile.annotationFieldNameMap.get(key));
                Object val;
                if(f.getType() == Double.class) {
                    val = symbolJson.getDouble(key);
                } else {
                    val = symbolJson.get(key);
                }
                f.set(symbolProfile, val);
            } catch(NoSuchFieldException e) {
                // Bad. Should never happen.
                throw new RuntimeException("JSON corrupted.");
            } catch(IllegalAccessException e) {
                // Bad. Should never happen.
                throw new RuntimeException("JSON corrupted.");
            } catch(IllegalArgumentException e) {
                throw new RuntimeException(key);
            }
        }

        return symbolProfile;
    }

    public void forceDelete() {
        persistenceManager.forceDelete();
    }

    public boolean deleteProfile() {
        jsonObject = new JSONObject();
        return persistenceManager.clear();
    }
}
