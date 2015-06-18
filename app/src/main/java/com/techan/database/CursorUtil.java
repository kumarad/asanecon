package com.techan.database;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import com.techan.contentProvider.StockContentProvider;
import com.techan.profile.Portfolio;
import com.techan.profile.ProfileManager;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class CursorUtil {
    public static CursorLoader getCursorLoader(Context context, String portfolioName, String[] projection) {
        Set<String> symbols = getSymbols(context, portfolioName);
        StringBuilder inList = getInList(symbols);

        // CONTENT_URI = "content://com.techan.contentprovider/stocks"
        return new CursorLoader(context,
                StockContentProvider.CONTENT_URI,
                projection,
                "sym IN (" + inList.toString() + ")",
                symbols.toArray(new String[0]),
                null);

    }

    public static Cursor getCursor(Context context, String portfolioName, String[] projection) {
        Set<String> symbols = getSymbols(context, portfolioName);
        StringBuilder inList = getInList(symbols);

        return context.getContentResolver().query(StockContentProvider.CONTENT_URI,
                projection,
                "sym IN (" + inList.toString() + ")",
                symbols.toArray(new String[0]),
                null);
    }

    private static Set<String> getSymbols(Context context, String portfolioName) {
        Set<String> symbols = Collections.emptySet();
        Map<String, Portfolio> portfolios = ProfileManager.getPortfolios(context);
        for(Map.Entry<String, Portfolio> curPortfolioEntry : portfolios.entrySet()) {
            if(curPortfolioEntry.getKey().equals(portfolioName)) {
                symbols = curPortfolioEntry.getValue().getSymbols();
                break;
            }
        }

        return symbols;
    }

    private static StringBuilder getInList(Set<String> symbols) {
        int argcount = symbols.size(); // number of IN arguments
        StringBuilder inList = new StringBuilder(argcount * 2);
        for (int i = 0; i < argcount; i++) {
            if (i > 0) inList.append(",");
            inList.append("?");
        }

        return inList;
    }
}
