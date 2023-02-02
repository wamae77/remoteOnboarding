package com.cll.FingerPrintModule.utils;

import java.util.ArrayList;

public class ResultScan {

    private static ResultScan instance;

    private ArrayList<FingerPrint> list;

    public ArrayList<FingerPrint> getList() {
        return list;
    }

    public void setList(ArrayList<FingerPrint> list) {
        this.list = list;
    }

    private ResultScan(){}

    public static ResultScan getInstance(){
        if(instance == null){
            instance = new ResultScan();
        }
        return instance;
    }
}
