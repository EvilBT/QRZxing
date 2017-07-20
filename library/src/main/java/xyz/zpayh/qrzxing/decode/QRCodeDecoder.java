package xyz.zpayh.qrzxing.decode;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.Arrays;

/**
 * 创建人： zp
 * 创建时间：2017/7/20
 */

public class QRCodeDecoder {

    public static Result decode(@NonNull Bitmap src){

        final int width = src.getWidth();
        final int height = src.getHeight();
        int pixels[] = new int[width*height];
        src.getPixels(pixels,0,width,0,0,width,height);
        RGBLuminanceSource source = new RGBLuminanceSource(width,height,pixels);

        //Map<DecodeHintType,Object> hints = new EnumMap<>(DecodeHintType.class);

        try {
            Result result = new QRCodeReader().decode(new BinaryBitmap(new HybridBinarizer(source)));
            return result;
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Result decode(@NonNull byte[] yuv, int width, int height){
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(yuv,width,height,0,0,width,height,false);
        try {
            Result result = new QRCodeReader().decode(new BinaryBitmap(new HybridBinarizer(source)));
            return result;
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static byte[] YUVS = null;

    public static byte[] getYUV420sp(@NonNull Bitmap src){

        final int w = src.getWidth();
        final int h = src.getHeight();

        float scaled = 1.0f;
        if (w < h && w > 256){
            scaled = 256.0f / w;
        } else if (h < w && h > 256){
            scaled = 256.0f / h;
        }

        Matrix matrix = new Matrix();
        matrix.setScale(scaled,scaled);

        src = Bitmap.createBitmap(src,0,0,w,h,matrix,false);

        final int width = src.getWidth();
        final int height = src.getHeight();

        int argb[] = new int[width*height];

        src.getPixels(argb, 0, width, 0, 0, width, height);

        int requiredWidth = width % 2 == 0 ? width : width + 1;
        int requiredHeight = height % 2 == 0 ? height : height + 1;

        int byteLength = requiredWidth * requiredHeight * 3 / 2;

        if (YUVS == null || YUVS.length < byteLength){
            YUVS = new byte[byteLength];
        } else {
            Arrays.fill(YUVS, (byte) 0);
        }

        transCoding(YUVS, argb, width, height);

        return YUVS;
    }

    /**
     * RGB 转码 YUV420sp
     * @param yuv420sp inputWidth * inputHeight * 3 / 2
     * @param argb inputWidth * inputHeight
     * @param width image Width
     * @param height image Height
     */
    private static void transCoding(byte[] yuv420sp, int[] argb, int width, int height){
        final int frameSize = width * height;

        int Y, U, V;
        int yIndex = 0;
        int uvIndex = frameSize;

        int R, G, B;
        int rgbIndex = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                R = getRed(argb[rgbIndex]);
                G = getGreen(argb[rgbIndex]);
                B = getBlue(argb[rgbIndex]);
                rgbIndex++;

                Y = ((66  * R + 129 * G +  25 * B + 128) >> 8) + 16;
                U = ((-38 * R -  74 * G + 122 * B + 128) >> 8) + 128;
                V = ((112 * R -  94 * G -  18 * B + 128) >> 8) + 128;

                Y = limited(Y);
                U = limited(U);
                V = limited(V);

                yuv420sp[yIndex++] = (byte) Y;
                if ((y%2==0) && (x%2==0)){
                    yuv420sp[uvIndex++] = (byte) V;
                    yuv420sp[uvIndex++] = (byte) U;
                }
            }
        }
    }

    private static int getRed(int color){
        return (color & 0xFF0000) >> 8;
    }

    private static int getGreen(int color){
        return (color & 0xFF00) >> 8;
    }

    private static int getBlue(int color){
        return color & 0xFF;
    }

    private static int limited(int color){
        return Math.max(0,Math.min(color, 255));
    }
}
