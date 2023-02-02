package com.cll.FingerPrintModule.runnables;

import ai.tech5.sdk.abis.T5AirSnap.SgmRectImage;
import ai.tech5.sdk.abis.T5AirSnap.T5AirSnap;

public class GetNistQualityThread implements Runnable {
    private T5AirSnap m_cellSdk = null;
    SgmRectImage m_rect = null;
    byte m_nistQuality = 0;

    public byte getNistQuality() {
        return m_nistQuality;
    }

    @Override
    public void run() {
        try {
            if (m_rect != null) {
                Byte nistQuality = new Byte((byte) 0);

                m_cellSdk.getNistFingerImageQuality(m_rect.image, m_rect.width, m_rect.height,
                        nistQuality);
                m_nistQuality = nistQuality;
            }
        } catch (Exception ex) {
            //Log.e(APP_TAG," From thread error is " + ex.getMessage());
        }
    }

    public GetNistQualityThread(T5AirSnap m_cellSdk, SgmRectImage rect) {
        this.m_cellSdk = m_cellSdk;
        m_rect = rect;
    }
}
