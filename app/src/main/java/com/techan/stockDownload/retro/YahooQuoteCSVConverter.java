package com.techan.stockDownload.retro;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techan.custom.Util;
import com.techan.stockDownload.StockData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

public class YahooQuoteCSVConverter  implements Converter {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructType(type);
            Map<String, StockData> mapType = new HashMap<>();
            if(javaType.getRawClass().isInstance(mapType)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
                Map<String, StockData> stockDataList = new HashMap<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] rowData = line.split(",");

                    if(rowData.length < 13) {
                        throw new RuntimeException("Unexpected data from yahoo.");
                    }

                    String symbol = rowData[0].replace("\"", "");
                    StockData stockData = new StockData(symbol);

                    stockData.price = Util.parseDouble(rowData[1]);
                    stockData.daysLow = Util.parseDouble(rowData[2]);
                    stockData.daysHigh = Util.parseDouble(rowData[3]);
                    stockData.pe = Util.parseDouble(rowData[4]);
                    stockData.peg = Util.parseDouble(rowData[5]);
                    stockData.div = Util.parseDouble(rowData[6]);
                    stockData.moveAvg50 = Util.parseDouble(rowData[7]);
                    stockData.moveAvg200 = Util.parseDouble(rowData[8]);
                    stockData.tradingVol = Util.parseDouble(rowData[9]);
                    stockData.avgTradingVol = Util.parseDouble(rowData[10]);
                    stockData.change = Util.parseDouble(rowData[11]);

                    int nameLength = rowData.length - 12;
                    StringBuilder nameBuilder = new StringBuilder();
                    nameBuilder.append("");
                    for(int i = 0; i < nameLength; ++i) {
                        nameBuilder.append(rowData[12+i]);
                    }
                    stockData.name = nameBuilder.toString();

                    stockDataList.put(symbol, stockData);
                }

                return stockDataList;
            } else {
                throw new InvalidParameterException("Don't support type");
            }
        } catch(Exception e) {
            throw new ConversionException("Failed to convert input stream");
        }
    }

    @Override
    public TypedOutput toBody(Object object) {
        throw new RuntimeException("Not implemented");
    }
}
