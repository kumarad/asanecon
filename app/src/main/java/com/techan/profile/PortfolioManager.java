package com.techan.profile;

import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;

public class PortfolioManager {
    private static final String portfolioFileName = "techan_portfolios";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PersistenceManager persistenceManager;
    Set<String> portfolios;

    public PortfolioManager(Context ctx) {
        persistenceManager = new PersistenceManager(ctx, portfolioFileName);

        String s = persistenceManager.read();
        if(s.equals("")) {
            portfolios = new HashSet<>();
        } else {
            try {
                portfolios = objectMapper.readValue(s, new TypeReference<Set<String>>(){});
            } catch(Exception e) {
                throw new RuntimeException("Failed to construct portfolio list");
            }
        }
    }

    public boolean addPortfolio(String portfolio) {
        try {
            portfolios.add(portfolio);

            return persistenceManager.write(objectMapper.writeValueAsString(portfolios));
        } catch(Exception e) {
            return false;
        }
    }

    public boolean removePortfolio(String portfolio) {
        portfolios.remove(portfolio);

        try {
            return persistenceManager.write(objectMapper.writeValueAsString(portfolios));
        } catch(Exception e) {
            return false;
        }
    }

    public Set<String> getPortfolios() {
        return portfolios;
    }
}
