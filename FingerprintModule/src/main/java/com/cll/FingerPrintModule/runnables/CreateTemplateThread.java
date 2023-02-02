package com.cll.FingerPrintModule.runnables;

import static ai.tech5.sdk.abis.T5AirSnap.StandardErrorCodes.SE_OK;

import ai.tech5.sdk.abis.T5AirSnap.MinexTemplateType;
import ai.tech5.sdk.abis.T5AirSnap.RawImage;
import ai.tech5.sdk.abis.T5AirSnap.T5AirSnap;

public class CreateTemplateThread implements Runnable {
    private T5AirSnap m_cellSdk = null;
    int m_fingersNumber = 1;
    RawImage[] m_rawImages = null;
    byte[] m_templateBuffer = null;
    byte m_nistQuality = 0;
    byte m_quality = 0;
    int m_minutiaesNumber = 0;

    public byte[] getTemplate() {
        return m_templateBuffer;
    }

    public byte getNistQuality() {
        return m_nistQuality;
    }

    public byte getQuality() {
        return m_quality;
    }

    public int getMinutiaesNumber() {
        return m_minutiaesNumber;
    }

    public int getFinger() {
        return (m_rawImages != null) ? m_rawImages[0].m_finger : 0;
    }

    @Override
    public void run() {
        try {
            if (m_rawImages != null) {
                Byte[] nistQuality = new Byte[m_fingersNumber];
                Byte[] quality = new Byte[m_fingersNumber];
                Integer[] minutiaesNumber = new Integer[m_fingersNumber];

                for (int i = 0; i < m_fingersNumber; i++) {
                    nistQuality[i] = new Byte((byte) 0);
                    quality[i] = new Byte((byte) 0);
                    minutiaesNumber[i] = new Integer(0);
                }

                Integer templateSize = new Integer(0);

                int resultCode = m_cellSdk.createTemplate(m_rawImages, MinexTemplateType.NIST_TEMPLATE, m_templateBuffer, nistQuality, quality, minutiaesNumber, templateSize);
                if (resultCode == SE_OK) {
                    //Log.i(APP_TAG, "Success creating template size = " + templateSize + " quality = " + quality + " for finger = " + m_raw.m_finger);
                    m_nistQuality = nistQuality[0];
                    m_quality = quality[0];
                    m_minutiaesNumber = minutiaesNumber[0];
                } else {
                    m_templateBuffer = null;
                    //Log.e(APP_TAG, "Error of template building = " + resultCode + "for finger = " + m_raw.m_finger);
                }
            }
        } catch (Exception ex) {
            //Log.e(APP_TAG," From thread error is " + ex.getMessage());
        }
    }

    public CreateTemplateThread(T5AirSnap m_cellSdk, RawImage rawImage) {
        try {
            this.m_cellSdk = m_cellSdk;

            m_rawImages = new RawImage[m_fingersNumber];
            m_rawImages[0] = rawImage;
            m_templateBuffer = m_cellSdk.allocateTemplate(m_fingersNumber);
        } catch (Exception ex) {
            //Log.e(APP_TAG, ex.getMessage() );
        }
    }
}
