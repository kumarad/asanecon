package com.techan.profile;

import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PortfolioManager {
    private static final String portfolioFileName = "techan_portfolios";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PersistenceManager persistenceManager;
    private Map<String,Portfolio> portfolioMap;

    public PortfolioManager(Context ctx) {
        persistenceManager = new PersistenceManager(ctx, portfolioFileName);

        String s = persistenceManager.read();
        if(s.equals("")) {
            portfolioMap = new HashMap<>();
        } else {
            try {
                portfolioMap = objectMapper.readValue(s, new TypeReference<Map<String,Portfolio>>(){});
            } catch(Exception e) {
                throw new RuntimeException("Failed to construct portfolio list");
            }
        }
    }

    public boolean addPortfolio(String portfolioName) {
        try {
            portfolioMap.put(portfolioName, new Portfolio(portfolioName));

            return persistenceManager.write(objectMapper.writeValueAsString(portfolioMap));
        } catch(Exception e) {
            return false;
        }
    }

    public boolean removePortfolio(String portfolioName) {
        portfolioMap.remove(portfolioName);

        try {
            return persistenceManager.write(objectMapper.writeValueAsString(portfolioMap));
        } catch(Exception e) {
            return false;
        }
    }

    public Map<String, Portfolio> getPortfolios() {
        return portfolioMap;
    }

    public boolean addSymbolToPortfolio(String portfolioName, String symbol) {
        Portfolio portfolio = portfolioMap.get(portfolioName);
        if(portfolio != null) {
            portfolio.addSymbol(symbol);

            try {
                return persistenceManager.write(objectMapper.writeValueAsString(portfolioMap));
            } catch(Exception e) {
                return false;
            }
        }

        return false;
    }

    public boolean removeSymbolFromPortfolio(String portfolioName, String symbol) {
        Portfolio portfolio = portfolioMap.get(portfolioName);
        if(portfolio != null) {
            portfolio.removeSymbol(symbol);

            try {
                return persistenceManager.write(objectMapper.writeValueAsString(portfolioMap));
            } catch(Exception e) {
                return false;
            }
        }

        return false;
    }

    public boolean deletePortfolio(String portfolioName, boolean deleteAllStocks) {
        Portfolio portfolio = portfolioMap.get(portfolioName);
        if(portfolio != null) {
            if(deleteAllStocks) {
                // Delete the stocks in this portfolio across all other portfolios.
                for(String curSymbol : portfolio.getSymbols()) {
                    for(Map.Entry<String, Portfolio> curEntry : portfolioMap.entrySet()) {
                        curEntry.getValue().removeSymbol(curSymbol);
                    }
                }
            }

            portfolioMap.remove(portfolioName);

            try {
                return persistenceManager.write(objectMapper.writeValueAsString(portfolioMap));
            } catch(Exception e) {
                return false;
            }
        }

        return false;

    }

    public boolean deletePortfolios() {
        portfolioMap = new HashMap<>();
        return persistenceManager.clear();
    }
}
