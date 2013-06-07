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
    private static JSONObject curJSONObject;

    private static JSONObject getRoot(Context ctx) throws JSONException {
        if(curJSONObject == null) {
            String s = PersistenceManager.read(ctx);
            if(s.equals("")) {
                curJSONObject = new JSONObject();
            } else {
                curJSONObject = new JSONObject(s);
            }
        }

        return curJSONObject;
    }

    public static boolean addSymbol(Context ctx, String symbol) {
        try {
            JSONObject jsonObject = getRoot(ctx);

            // Add symbol.
            jsonObject.put(symbol, new JSONObject());

            // Update file.
            return  PersistenceManager.write(ctx, jsonObject.toString());
        } catch(JSONException e) {
            Log.e(Constants.LOG_TAG, "Failed to add symbol to JSON object.");
            return false;
        }
    }

    public static boolean removeSymbol(Context ctx, String symbol) {
        try {
            JSONObject jsonObject = getRoot(ctx);

            // Add symbol.
            jsonObject.remove(symbol);

            // Update file.
            return  PersistenceManager.write(ctx, jsonObject.toString());
        } catch(JSONException e) {
            Log.e(Constants.LOG_TAG, "Failed to add symbol to JSON object.");
            return false;
        }
    }

    public static boolean addSymbolData(Context ctx, SymbolProfile profile) {
        JSONObject symbolJson;
        try {
            symbolJson = getRoot(ctx).getJSONObject(profile.symbol);
        } catch(JSONException e) {
            Log.e(Constants.LOG_TAG, "Failed to get json object");
            return false;
        }

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

        if(!PersistenceManager.write(ctx, curJSONObject.toString())) {
            status = false;
        }

        return status;
    }

    public static List<SymbolProfile> getSymbols(Context ctx) {
        List<SymbolProfile> profiles = new ArrayList<SymbolProfile>();
        try {
            JSONObject jsonObject = getRoot(ctx);

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

    private static SymbolProfile getSymbolProfile(String symbol, JSONObject symbolJson) throws JSONException {
        SymbolProfile symbolProfile = new SymbolProfile(symbol);

        Iterator subIter = symbolJson.keys();
        while(subIter.hasNext()) {
            String key = (String)subIter.next();
            try {
                Field f = SymbolProfile.class.getField(SymbolProfile.annotationFieldNameMap.get(key));
                Object val = symbolJson.get(key);
                f.set(symbolProfile, val);
            } catch(NoSuchFieldException e) {
                // Bad. Should never happen.
                throw new RuntimeException("JSON corrupted.");
            } catch(IllegalAccessException e) {
                // Bad. Should never happen.
                throw new RuntimeException("JSON corrupted.");
            }
        }

        return symbolProfile;
    }
}
