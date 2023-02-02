/**
 * Copyright (c) 2012-2017, Smart Engines Ltd
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * <p>
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Smart Engines Ltd nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.smartengines;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Map;
import java.util.TreeMap;

import biz.smartengines.smartid.swig.Image;
import biz.smartengines.smartid.swig.ImageField;
import biz.smartengines.smartid.swig.RecognitionResult;
import biz.smartengines.smartid.swig.StringField;
import biz.smartengines.smartid.swig.StringVector;

/**
 * Convenience storage of recognition results
 */
public class MainResultStore {

    /**
     * MainResultStore singleton storage
     */
    public static final MainResultStore instance = new MainResultStore();
    private final Map<String, Field> fields = new TreeMap<>(); ///< returned fields (wrapping)
    private String document_type; ///< string representation of the returned document type
    private ScannedIDData scannedIDData;

    /**
     * Register new recognition result
     */
    public void setResult(RecognitionResult result) {
        fields.clear();

        document_type = result.GetDocumentType();

        StringVector field_names = result.GetStringFieldNames(); // get all string results
        StringVector image_names = result.GetImageFieldNames(); // get all image results

        for (int i = 0; i < field_names.size(); i++) {  // get text fields
            String fieldName = field_names.get(i);
            StringField stringField = result.GetStringField(fieldName);
            Field field = new Field(fieldName, stringField.GetUtf8Value(), stringField.IsAccepted());
            fields.put(fieldName, field);
        }

        for (int i = 0; i < image_names.size(); i++) {  // get image fields
            String fieldName = image_names.get(i);
            ImageField imageField = result.GetImageField(fieldName);
            Field field = new Field(
                    image_names.get(i), getBitmap(imageField.GetValue()), imageField.IsAccepted());
            fields.put(fieldName, field);
        }
    }

    public String[] getFieldNames() {
        int count = 0;
        String[] fieldNames = new String[fields.size() + 1];
        fieldNames[count++] = "document type";

        for (Field field : fields.values()) {
            fieldNames[count++] = field.name;
        }

        return fieldNames;
    }

    public boolean isFieldImage(String fieldName) {
        Field field = fields.get(fieldName);
        return field != null && field.is_image;
    }

    public boolean isFieldAccepted(String fieldName) {
        Field field = fields.get(fieldName);
        return field != null && field.is_accepted;
    }

    public String getDocumentType() {
        return document_type;
    }

    public String getStringValue(String fieldName) {
        Field field = fields.get(fieldName);
        return field != null ? field.value : "";
    }

    public Bitmap getImageValue(String fieldName) {
        Field field = fields.get(fieldName);
        return field != null ? field.bitmap : null;
    }

    private Bitmap getBitmap(Image image) {
        Bitmap bitmap;

        int nChannels = image.GetChannels(); ///< number of color channels per-pixel
        int sizeBytes = image.GetRequiredBufferLength();
        byte[] bytes = new byte[sizeBytes];
        image.CopyToBuffer(bytes);

        int sizePixels = image.GetHeight() * image.GetWidth();
        int[] pixels = new int[sizePixels];

        int r = 0, g = 0, b = 0;

        for (int y = 0; y < image.GetHeight(); y++) {
            for (int x = 0; x < image.GetWidth(); x++) {
                if (nChannels == 1) {
                    r = g = b = (bytes[x + y * image.GetStride()] & 0xFF);
                }

                if (nChannels == 3) {
                    b = bytes[3 * x + y * image.GetStride()] & 0xFF;
                    g = bytes[3 * x + y * image.GetStride() + 1] & 0xFF;
                    r = bytes[3 * x + y * image.GetStride() + 2] & 0xFF;
                }

                pixels[x + y * image.GetWidth()] = Color.rgb(b, g, r);
            }
        }

        bitmap = Bitmap.createBitmap(
                pixels, image.GetWidth(), image.GetHeight(), Bitmap.Config.ARGB_8888);

        return bitmap;
    }

    public ScannedIDData getScannedIDData() {
        return scannedIDData == null ? new ScannedIDData() : scannedIDData;
    }

    public void setScannedIDData(ScannedIDData scannedIDData) {
        this.scannedIDData = scannedIDData;
    }



    /**
     * Document field class for future usage in Android app
     */
    public class Field {

        String name; ///< field name
        boolean is_accepted; ///< boolean field acceptance flag
        boolean is_image; ///< whether it's an StringField
        String value; ///< StringField value
        Bitmap bitmap; ///< ImageField value

        /**
         * Constructor for StringField wrapping
         */
        Field(String name, String value, boolean is_accepted) {
            this.name = name;
            this.value = value;
            this.is_accepted = is_accepted;

            is_image = false;
            bitmap = null;
        }

        /**
         * Constructor for ImageField wrapping
         */
        Field(String name, Bitmap bitmap, boolean is_accepted) {
            this.name = name;
            this.bitmap = bitmap;
            this.is_accepted = is_accepted;

            is_image = true;
        }
    }

}
