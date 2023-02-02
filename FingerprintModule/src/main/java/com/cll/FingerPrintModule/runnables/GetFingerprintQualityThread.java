package com.cll.FingerPrintModule.runnables;

import ai.tech5.sdk.abis.T5AirSnap.SgmRectImage;
import ai.tech5.sdk.abis.T5AirSnap.T5AirSnap;

public class GetFingerprintQualityThread implements Runnable
{
    private T5AirSnap m_cellSdk = null;
    SgmRectImage m_rect    = null;
    byte         m_quality = 0;

    public byte getFingerprintQuality()
    {
        return m_quality;
    }

    @Override
    public void run()
    {
        try
        {
            if (m_rect != null)
            {
                Byte quality = new Byte((byte)0);

                m_cellSdk.getFingerprintQuality(m_rect.image, m_rect.width, m_rect.height,
                        quality);
                m_quality = quality;
            }
        }
        catch(Exception ex)
        {
            //Log.e(APP_TAG," From thread error is " + ex.getMessage());
        }
    }

    public GetFingerprintQualityThread(T5AirSnap m_cellSdk,SgmRectImage rect)
    {
        this.m_cellSdk =m_cellSdk;
        m_rect = rect;
    }
}

