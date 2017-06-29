package com.cf.zxinglibrary.zxing.encoding;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;
/**
 * @author Ryan Tang
 *
 */
public final class EncodingHandler {
	private static final int BLACK = 0xff000000;
	
	public static Bitmap createQRCode(String str,int widthAndHeight) throws WriterException {
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);

		//========================================================================
//		//图像数据转换，使用了矩阵转换
//		BitMatrix bitMatrix = new QRCodeWriter().encode(str, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);
//		int[] pixels = new int[widthAndHeight * widthAndHeight];
//		//下面这里按照二维码的算法，逐个生成二维码的图片，
//		//两个for循环是图片横列扫描的结果
//		for (int y = 0; y < widthAndHeight; y++)
//		{
//			for (int x = 0; x < widthAndHeight; x++)
//			{
//				if (bitMatrix.get(x, y))
//				{
//					pixels[y * widthAndHeight + x] = 0xff000000;
//				}
//				else
//				{
//					pixels[y * widthAndHeight + x] = 0xffffffff;
//				}
//			}
//		}
		//=====================================================================

		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = BLACK;
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
}
