package com.hcyacg.pixiv.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * Created: Nekoer
 * Desc: 支付宝当面付
 * Date: 2020/7/29 10:26
 */
@Component
public class AliPayUtils {

    /**
     * 生成包含字符串信息的二维码图片
     *
     * @param content      二维码携带信息
     */
    public byte[] createQrCode(String content) {
        try {
            //设置二维码纠错级别ＭＡＰ
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);  // 矫错级别
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            //创建比特矩阵(位矩阵)的QR码编码的字符串
            BitMatrix byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 900, 900, hintMap);
            // 使BufferedImage勾画QRCode  (matrixWidth 是行二维码像素点)
            int matrixWidth = byteMatrix.getWidth();
            BufferedImage image = new BufferedImage(matrixWidth - 200, matrixWidth - 200, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, matrixWidth, matrixWidth);
            // 使用比特矩阵画并保存图像
            graphics.setColor(Color.BLACK);
            for (int i = 0; i < matrixWidth; i++) {
                for (int j = 0; j < matrixWidth; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i - 100, j - 100, 1, 1);
                    }
                }
            }

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            ImageIO.write(image, "JPEG", outStream);
            return outStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
