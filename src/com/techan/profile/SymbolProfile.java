package com.techan.profile;

import com.techan.custom.Util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SymbolProfile {
    public final String symbol;

    ///////////////////////////////////////////////////////////
    @SymbolProfileMember(memberName = ProfileManager.BUY_PRICE)
    public Double buyPrice = null;

    @SymbolProfileMember(memberName = ProfileManager.STOCK_COUNT)
    public Integer stockCount = null;


    ///////////////////////////////////////////////////////////
    @SymbolProfileMember(memberName = ProfileManager.STOP_LOSS_PERCENT)
    public Integer stopLossPercent = null;

    @SymbolProfileMember(memberName = ProfileManager.STOP_LOSS_BUY_DATE)
    public String stopLossBuyDate = null;

    @SymbolProfileMember(memberName = ProfileManager.STOP_LOSS_TRAILING)
    public Boolean stopLossTrailing = null;


    ///////////////////////////////////////////////////////////
    @SymbolProfileMember(memberName = ProfileManager.TARGET_PRICE)
    public Double targetPrice = null;

    @SymbolProfileMember(memberName = ProfileManager.TARGET_PRICE_COMPARATOR)
    public Boolean lessThanEqual = null;

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

    public void setStopLossInfo(Integer stopLossPercent, boolean trailing) {
        this.stopLossPercent = stopLossPercent;
        this.stopLossBuyDate = Util.getDateStrForDb(Calendar.getInstance());
        this.stopLossTrailing = trailing;
    }

    public void clearStopLossInfo() {
        stopLossPercent = null;
        stopLossBuyDate = null;
        stopLossTrailing = null;
    }
}
