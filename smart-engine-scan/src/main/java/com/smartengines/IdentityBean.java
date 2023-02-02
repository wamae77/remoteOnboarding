package com.smartengines;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class IdentityBean {
    private String title;
    private String key;
    private static final List<IdentityBean> identityTypeList = new ArrayList<>();

    public IdentityBean(String title, String key) {
        this.title = title;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }


    public static String identificationDocument(){

        IdentityBean bean = null;
        String idType =MainResultStore.instance.getDocumentType();
        if (idType == null){
            return "";
        }
        switch (idType) {
            case Constant.ID_KENYAN:
            case Constant.ID_UGANDA:
            case Constant.ID_TANZANIA:
            case Constant.ID_RWANDA:
            case Constant.ID_UAE:
            case Constant.ID_AADHAAR:
            case Constant.ID_PANCARD:
                bean = getIdentityTypeList().get(0);
                return bean.getTitle();
            case Constant.PASSPORT_KENYAN:
            case Constant.PASSPORT_UGANDA:
            case Constant.PASSPORT_TANZANIA:
            case Constant.PASSPORT_TANZANIA_OLD:
                    /*case Constant.PASSPORT_RWANDA:
                    case Constant.PASSPORT_UAE:*/
            case Constant.PASSPORT_INDIA:
                bean = getIdentityTypeList().get(2);
                return bean.getTitle();
        }
        return "";
    }
    private static List<IdentityBean> getIdentityTypeList() {
        if (identityTypeList.isEmpty()) {
            identityTypeList.add(new IdentityBean("National ID", "nationalId"));
            identityTypeList.add(new IdentityBean("Driving Licence", "dl"));
            identityTypeList.add(new IdentityBean("Passport", "passport"));
        }
        return identityTypeList;
    }
}

