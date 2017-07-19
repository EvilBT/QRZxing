package xyz.zpayh.qrzxing.encode;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;

/**
 * 文 件 名: QRCodeEncoder
 * 创 建 人: 陈志鹏
 * 创建日期: 2017/7/19 23:26
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public final class QRCodeEncoder {

    public static final int BLACK = Color.BLACK;
    public static final int WHITE = Color.WHITE;

    public static Bitmap encode(String content, int dimension) throws WriterException{
        EnumMap<EncodeHintType,Object> hints = new EnumMap<>(EncodeHintType.class);

        //添加默认编码方式UTF-8
        hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        //
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        hints.put(EncodeHintType.MARGIN,1);

        BitMatrix result;

        try {
            result = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE,dimension,dimension,hints);
        } catch (IllegalArgumentException e) {
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width*height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x,y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels,0, width, 0, 0, width, height);
        return bitmap;
    }
}
