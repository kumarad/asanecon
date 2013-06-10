package com.techan.profile;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SymbolProfile {
    public static final Integer GLOBAL_STOP_LOSS_PERCENT = 25;
    public static final Double GLOBAL_PE_TARGET = 10.0;

    public static final String STOP_LOSS_PERCENT = "stopLossPercent";
    public static final String BUY_PRICE = "BUY_PRICE";
    public static final String STOCK_COUNT = "STOCK_COUNT";
    public static final String PE_TARGET = "PE_TARGET";

    public final String symbol;

    @SymbolProfileMember(memberName = STOP_LOSS_PERCENT)
    public Integer stopLossPercent = null;

    @SymbolProfileMember(memberName = BUY_PRICE)
    public Double buyPrice = null;

    @SymbolProfileMember(memberName = STOCK_COUNT)
    public Integer stockCount = null;

    @SymbolProfileMember(memberName = PE_TARGET)
    public Double peTarget = null;

    public SymbolProfile(String symbol) {
        this.symbol = symbol;
    }

    public final static Map<String,String> annotationFieldNameMap = new HashMap<String, String>();
    static {
        for(Field f : SymbolProfile.class.getFields()) {
            Annotation a = f.getAnnotation(SymbolProfileMember.class);
            if( a != null) {
                String str = ((SymbolProfileMember)a).memberName();
                annotationFieldNameMap.put(str, f.getName());
            }
        }
    }
}
