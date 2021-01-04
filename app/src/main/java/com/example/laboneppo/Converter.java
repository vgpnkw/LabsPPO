package com.example.laboneppo;

public class Converter {
    static public String convert(String data, Measure input, Measure output){
        if(input.getCategory() == output.getCategory() && !data.equals("")){
            return String.valueOf(( output.getCoefficient()/input.getCoefficient() ) * Double.parseDouble(data));
        }
        return "";
    }
}
