package com.poisondemo.mycurrencies;

public class BeanRecord {
    private String forCode;
    private String homCode;
    private String forAmount;
    private String homAmount;
    private String time;
    public BeanRecord(){};
    public BeanRecord(String forCode,String forAmount,String homCode,String homAmount,String time)
    {
        this.forCode = forCode;
        this.homCode = homCode;
        this.forAmount = forAmount;
        this.homAmount = homAmount;
        this.time = time;
    }

    public String getForAmount(){
        return forAmount;
    }

    public String getHomAmount(){
        return homAmount;
    }

    public String getForCode(){
        return forCode;
    }

    public String getHomCode(){
        return homCode;
    }

    public String getTime(){
        return time;
    }

    public void setForAmount(String forAmount){
        this.forAmount = forAmount;
    }

    public void setHomAmount(String homAmount){
        this.homAmount = homAmount;
    }

    public void setForCode(String forCode){
        this.forCode = forCode;
    }

    public void setHomCode(String homCode){
        this.homCode = homCode;
    }

    public void setTime(String time){
        this.time = time;
    }
}

