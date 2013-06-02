package com.techan.profile;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SymbolProfile {
    public static final String STOP_LOSS_PERCENT = "stopLossPercent";
    public static final String STOP_LOSS_PIVOT = "stopLossPivot";

    public final String symbol;

    @SymbolProfileMember(memberName = STOP_LOSS_PERCENT)
    public Integer stopLossPercent = null;

    @SymbolProfileMember(memberName = STOP_LOSS_PIVOT)
    public Double stopLossPivot = null;

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
