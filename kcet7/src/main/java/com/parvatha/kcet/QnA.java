package com.parvatha.kcet;

import java.util.ArrayList;

public class QnA {

    String stringQ, stringA, stringQimg, stringAimg, stringAnsExplained;
    ArrayList<String> arrayListOptions = new ArrayList<>();

    public QnA(String stringQ, String stringA, ArrayList<String> arrayListOptions, String stringQimg, String stringAimg) {
        this.stringQ = stringQ;
        this.stringA = stringA;
        this.arrayListOptions = arrayListOptions;
        this.stringQimg = stringQimg;
        this.stringAimg = stringAimg;
    }

    public String getStringAnsExplained() {
        return stringAnsExplained;
    }

    public void setStringAnsExplained(String stringAnsExplained) {
        this.stringAnsExplained = stringAnsExplained;
    }

    public String getStringQimg() {
        return stringQimg;
    }

    public void setStringQimg(String stringQimg) {
        this.stringQimg = stringQimg;
    }

    public String getStringAimg() {
        return stringAimg;
    }

    public void setStringAimg(String stringAimg) {
        this.stringAimg = stringAimg;
    }

    public String getStringQ() {
        return stringQ;
    }

    public void setStringQ(String stringQ) {
        this.stringQ = stringQ;
    }

    public String getStringA() {
        return stringA;
    }

    public void setStringA(String stringA) {
        this.stringA = stringA;
    }

    public ArrayList<String> getArrayListOptions() {
        return arrayListOptions;
    }

    public void setArrayListOptions(ArrayList<String> arrayListOptions) {
        this.arrayListOptions = arrayListOptions;
    }
}
