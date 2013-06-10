package com.techan.profile;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SymbolProfile {
    public final String symbol;

    @SymbolProfileMember(memberName = ProfileManager.STOP_LOSS_PERCENT)
    public Integer stopLossPercent = null;

    @SymbolProfileMember(memberName = ProfileManager.BUY_PRICE)
    public Double buyPrice = null;

    @SymbolProfileMember(memberName = ProfileManager.STOCK_COUNT)
    public Integer stockCount = null;

    @SymbolProfileMember(memberName = ProfileManager.PE_TARGET)
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
