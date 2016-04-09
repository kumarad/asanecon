package com.techan.stockDownload.retro;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techan.custom.Util;
import com.techan.stockDownload.DownloadTrendAndStopLossInfo;
import com.techan.stockDownload.StockDayPriceInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.SortedMap;
import java.util.TreeMap;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

public class YahooHistoryCSVConverter implements Converter {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructType(type);
            if(javaType.getRawClass().isInstance((new TreeMap<String, Double>()))) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
                SortedMap<String, StockDayPriceInfo> priceMap = new TreeMap<>();
                String line = reader.readLine(); // First line defines columns
                while ((line = reader.readLine()) != null) {
                    String[] rowData = line.split(",");

                    Double closePrice = Util.parseDouble(rowData[DownloadTrendAndStopLossInfo.DAY_CLOSE_INDEX]);
                    Double dayHigh = Util.parseDouble(rowData[DownloadTrendAndStopLossInfo.DAY_HIGH_INDEX]);
                    Double dayLow = Util.parseDouble(rowData[DownloadTrendAndStopLossInfo.DAY_LOW_INDEX]);
                    String dateStr = rowData[DownloadTrendAndStopLossInfo.DATE_INDEX];
                    priceMap.put(dateStr, new StockDayPriceInfo(closePrice, dayHigh, dayLow));
                }
                return priceMap;
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
