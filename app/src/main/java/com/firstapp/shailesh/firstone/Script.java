package com.firstapp.shailesh.firstone;

/**
 * Created by _SHAILESH on 1/9/2016.
 */
public class Script {
    private String ScriptName;
    private String Price;
    private String DayHigh;
    private String DayLow;

    public Script(String scriptName,String price,String dayHigh,String dayLow){
        ScriptName = scriptName;
        Price = price;
        DayHigh = dayHigh;
        DayLow = dayLow;
    }

    public String getScriptName(){
        return  ScriptName;
    }

    public String getPrice(){
        double scriptPrice = Double.parseDouble(Price);
        double roundScriptPrice = Math.round(scriptPrice * 100.0)/100.0;
        return Double.toString(roundScriptPrice);
    }

    public String getDayHigh(){
        double scriptPrice = Double.parseDouble(DayHigh);
        double roundScriptPrice = Math.round(scriptPrice * 100.0)/100.0;
        return Double.toString(roundScriptPrice);
    }
    public String getDayLow(){
        double scriptPrice = Double.parseDouble(DayLow);
        double roundScriptPrice = Math.round(scriptPrice * 100.0)/100.0;
        return Double.toString(roundScriptPrice);
    }
}
