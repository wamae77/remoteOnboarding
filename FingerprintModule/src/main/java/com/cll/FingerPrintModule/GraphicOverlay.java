package com.cll.FingerPrintModule;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import ai.tech5.sdk.abis.T5AirSnap.SgmRectImage;

public class GraphicOverlay extends View
{
    private Paint m_borderPaint   = null;
    private Rect m_borderRect    = null;
    private Paint m_boundBoxPaint = null;

    private ArrayList<SgmRectImage> m_rectangles = new ArrayList<SgmRectImage>();

    public GraphicOverlay(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        m_borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        m_borderPaint.setStyle(Paint.Style.STROKE);

        m_boundBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        m_boundBoxPaint.setStyle(Paint.Style.STROKE);
        m_boundBoxPaint.setColor(getResources().getColor(R.color.green_700));
    }

    public void init(Rect borderRect)
    {
        int squareWidth = (borderRect.width() + borderRect.height()) / 2;

        m_borderPaint.setStrokeWidth(0.02f * squareWidth);
        m_boundBoxPaint.setStrokeWidth(0.01f * squareWidth);

        m_borderRect = borderRect;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (m_borderRect == null)
        {
            return;
        }

        for (SgmRectImage rectangle : m_rectangles)
        {
            float  x0 = rectangle.coords[0][0];
            float  y0 = rectangle.coords[0][1];
            double dx = rectangle.coords[1][0] - x0;
            double dy = rectangle.coords[1][1] - y0;

            float  angle    = (float)(Math.atan2(dy, dx) * 180.0 / Math.PI);
            float  distance = (float)Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

            canvas.rotate(angle, x0, y0);

            canvas.drawOval((x0 - distance), (y0 - 0.6f * distance),
                    (x0 + distance), (y0 + 0.6f * distance),
                    m_boundBoxPaint);

            canvas.rotate(-angle, x0, y0);
        }

        canvas.drawRect(m_borderRect, m_borderPaint);
    }

    public void drawBorderAndBoundBoxes(int color, ArrayList<SgmRectImage> rectangles)
    {
        m_borderPaint.setColor(color);

        m_rectangles.clear();
        m_rectangles.addAll(rectangles);

        invalidate();
    }
}

