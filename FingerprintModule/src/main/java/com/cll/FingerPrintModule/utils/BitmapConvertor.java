package com.cll.FingerPrintModule.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class BitmapConvertor {

    private  byte[] Color_palette = new byte[1024]; //a palette containing 256 colors
    private   byte[] BMP_File_Header = new byte[14];
    private  byte[] DIB_header = new byte[40];
    private   byte[] Bitmap_Data = null;

    public byte[] CreateBitmapFile(byte[] gray_image, int width, int height)
    {
        byte[] bmp_array = null;

        try
        {
            create_parts(gray_image,width,height);
            bmp_array = new byte[BMP_File_Header.length + DIB_header.length + Color_palette.length+Bitmap_Data.length];
            int offset = 0;
            System.arraycopy(BMP_File_Header,0,bmp_array,offset,BMP_File_Header.length);
            offset+= BMP_File_Header.length;
            System.arraycopy(DIB_header,0,bmp_array,offset,DIB_header.length);
            offset+= DIB_header.length;
            System.arraycopy(Color_palette,0,bmp_array,offset,Color_palette.length);
            offset+= Color_palette.length;
            System.arraycopy(Bitmap_Data,0,bmp_array,offset,Bitmap_Data.length);
        }
        catch(Exception ex)
        {

        }
        return bmp_array;
    }


    //returns a byte array of a gray scale bitmap image
    public    byte[] CreateGrayBitmapArray(Bitmap Image) {
        try {
            create_parts(Image);
            //Create the array
            byte[] bitmap_array = new byte[BMP_File_Header.length + DIB_header.length
                    + Color_palette.length + Bitmap_Data.length];
            Copy_to_Index(bitmap_array, BMP_File_Header, 0);
            Copy_to_Index(bitmap_array, DIB_header, BMP_File_Header.length);
            Copy_to_Index(bitmap_array, Color_palette, BMP_File_Header.length + DIB_header.length);
            Copy_to_Index(bitmap_array, Bitmap_Data, BMP_File_Header.length + DIB_header.length + Color_palette.length);

            return bitmap_array;
        } catch (Exception e) {
            return null; //return a null single byte array if fails
        }
    }

    //returns a byte array of a gray scale bitmap image
    public    Bitmap CreateGrayBitmapArray( byte[] Image, int width, int height) {
        Bitmap bmp = null;
        try {
            create_parts(Image,width,height);
            //Create the array
            byte[] bitmap_array = new byte[BMP_File_Header.length + DIB_header.length
                    + Color_palette.length + Bitmap_Data.length];
            Copy_to_Index(bitmap_array, BMP_File_Header, 0);
            Copy_to_Index(bitmap_array, DIB_header, BMP_File_Header.length);
            Copy_to_Index(bitmap_array, Color_palette, BMP_File_Header.length + DIB_header.length);
            Copy_to_Index(bitmap_array, Bitmap_Data, BMP_File_Header.length + DIB_header.length + Color_palette.length);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inMutable = true;
            bmp = BitmapFactory.decodeByteArray(bitmap_array,0,bitmap_array.length,opt);

        } catch (Exception e) {
            //return a null single byte array if fails
        }
        return bmp;
    }

    //creates byte array of 256 color grayscale palette
    private   byte[] create_palette() {
        byte[] color_palette = new byte[1024];
        for (int i = 0; i < 256; i++) {
            color_palette[i * 4 + 0] = (byte) (i); //bule
            color_palette[i * 4 + 1] = (byte) (i); //green
            color_palette[i * 4 + 2] = (byte) (i); //red
            color_palette[i * 4 + 3] = (byte) 0; //padding
        }
        return color_palette;
    }


    //adds data of Source array to Destination array at the Index
    private   boolean Copy_to_Index(byte[] destination, byte[] source, int index) {
        boolean res = false;
        try {
            System.arraycopy(source,0,destination,index,source.length);
            res = true;
        } catch (Exception e) {
        }
        return res;
    }


    //create different part of a bitmap file
    private    void create_parts(Bitmap img) {
        //Create Bitmap Data
        Bitmap_Data = ConvertToGrayscale(img);
        //Create Bitmap File Header (populate BMP_File_Header array)
        Copy_to_Index(BMP_File_Header, new byte[]{(byte) 'B', (byte) 'M'}, 0); //magic number
        Copy_to_Index(BMP_File_Header, writeInt(BMP_File_Header.length
                + DIB_header.length + Color_palette.length + Bitmap_Data.length), 2); //file size
        Copy_to_Index(BMP_File_Header, new byte[]{(byte) 'M', (byte) 'C', (byte) 'A', (byte) 'T'}, 6); //reserved for application generating the bitmap file (not imprtant)
        Copy_to_Index(BMP_File_Header, writeInt(BMP_File_Header.length
                + DIB_header.length + Color_palette.length), 10); //bitmap raw data offset
        //Create DIB Header (populate DIB_header array)
        Copy_to_Index(DIB_header, writeInt(DIB_header.length), 0); //DIB header length
        Copy_to_Index(DIB_header, writeInt(((Bitmap) img).getWidth()), 4); //image width
        Copy_to_Index(DIB_header, writeInt(((Bitmap) img).getHeight()), 8); //image height
        Copy_to_Index(DIB_header, new byte[]{(byte) 1, (byte) 0}, 12); //color planes. N.B. Must be set to 1
        Copy_to_Index(DIB_header, new byte[]{(byte) 8, (byte) 0}, 14); //bits per pixel
        Copy_to_Index(DIB_header, writeInt(0), 16); //compression method N.B. BI_RGB = 0
        Copy_to_Index(DIB_header, writeInt(Bitmap_Data.length), 20); //lenght of raw bitmap data
        Copy_to_Index(DIB_header, writeInt(500*10000/254), 24); //horizontal resolution N.B. not important
        Copy_to_Index(DIB_header, writeInt(500*10000/254), 28); //vertical resolution N.B. not important
        Copy_to_Index(DIB_header, writeInt(256), 32); //number of colors in the palette
        Copy_to_Index(DIB_header, writeInt(0), 36); //number of important colors used N.B. 0 = all colors are imprtant
        //Create Color palett
        Color_palette = create_palette();
    }

    private    void create_parts(byte[] img, int width, int height) {
        //Create Bitmap Data
        Bitmap_Data = new byte[img.length];
        System.arraycopy(img,0, Bitmap_Data,0,img.length);
        //Create Bitmap File Header (populate BMP_File_Header array)
        Copy_to_Index(BMP_File_Header, new byte[]{(byte) 'B', (byte) 'M'}, 0); //magic number
        Copy_to_Index(BMP_File_Header, writeInt(BMP_File_Header.length
                + DIB_header.length + Color_palette.length + Bitmap_Data.length), 2); //file size
        Copy_to_Index(BMP_File_Header, new byte[]{(byte) 'M', (byte) 'C', (byte) 'A', (byte) 'T'}, 6); //reserved for application generating the bitmap file (not imprtant)
        Copy_to_Index(BMP_File_Header, writeInt(BMP_File_Header.length
                + DIB_header.length + Color_palette.length), 10); //bitmap raw data offset
        //Create DIB Header (populate DIB_header array)
        Copy_to_Index(DIB_header, writeInt(DIB_header.length), 0); //DIB header length
        Copy_to_Index(DIB_header, writeInt( width), 4); //image width
        Copy_to_Index(DIB_header, writeInt(-height), 8); //image height
        Copy_to_Index(DIB_header, new byte[]{(byte) 1, (byte) 0}, 12); //color planes. N.B. Must be set to 1
        Copy_to_Index(DIB_header, new byte[]{(byte) 8, (byte) 0}, 14); //bits per pixel
        Copy_to_Index(DIB_header, writeInt(0), 16); //compression method N.B. BI_RGB = 0
        Copy_to_Index(DIB_header, writeInt(Bitmap_Data.length), 20); //lenght of raw bitmap data
        Copy_to_Index(DIB_header, writeInt(500*10000/254), 24); //horizontal resolution N.B. not important
        Copy_to_Index(DIB_header, writeInt(500*10000/254), 28); //vertical resolution N.B. not important
        Copy_to_Index(DIB_header, writeInt(256), 32); //number of colors in the palette
        Copy_to_Index(DIB_header, writeInt(0), 36); //number of important colors used N.B. 0 = all colors are imprtant
        //Create Color palett
        Color_palette = create_palette();
    }



    //convert the color pixels of Source image into a grayscale bitmap (raw data)
    private    byte[] ConvertToGrayscale(Bitmap Source) {
        Bitmap source = (Bitmap) Source;
        int padding = (source.getWidth() % 4) != 0 ? 4 - (source.getWidth() % 4) : 0; //determine padding needed for bitmap file
        byte[] bytes = new byte[source.getWidth() * source.getHeight() + padding * source.getHeight()]; //create array to contain bitmap data with paddin
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int pixel = source.getPixel(x, y);
                int g = (int) (0.3 * Color.red(pixel) + 0.59 * Color.green(pixel) + 0.11 * Color.blue(pixel)); //grayscale shade corresponding to rgb
                bytes[(source.getHeight() - 1 - y) * source.getWidth() + (source.getHeight() - 1 - y) * padding + x] = (byte) g;
            }
            //add the padding
            for (int i = 0; i < padding; i++) {
                bytes[(source.getHeight() - y) * source.getWidth() + (source.getHeight() - 1 - y) * padding + i] = (byte) 0;
            }
        }
        return bytes;
    }


    /**
     * Write integer to little-endian
     *
     * @param value
     * @return
     */
    private  byte[] writeInt(int value) {
        byte[] b = new byte[4];

        b[0] = (byte) (value & 0x000000FF);
        b[1] = (byte) ((value & 0x0000FF00) >> 8);
        b[2] = (byte) ((value & 0x00FF0000) >> 16);
        b[3] = (byte) ((value & 0xFF000000) >> 24);

        return b;
    }

}
