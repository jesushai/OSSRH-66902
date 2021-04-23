package com.lemon.framework.util.io;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2019/9/9
 */
@SuppressWarnings("unused")
public class ImageUtils {

    public enum ImageThumbModeEnum {
        FixedHeightAndWidthZoom,
        FixedHeightZoom,
        FixedWidthZoom,
        FixedHeightAndWidthCrop
    }

    public enum ImageFormatEnum {
        jpg, gif, png, ico
    }

    /**
     * 自动根据图片的长宽，适应手机屏幕
     *
     * @param bytes  图片内容
     * @param format 格式
     * @return 调整后的图片内容
     * @throws IOException IOException
     */
    public static byte[] rotateImageAuto(byte[] bytes, ImageFormatEnum format) throws IOException {
        BufferedImage originalImage = bytesToImage(bytes);
        BufferedImage img = rotateImageAuto(originalImage);
        if (img == originalImage)
            return bytes;
        else
            return imageToBytes(img, format);
    }

    /**
     * 自动根据图片的长宽，适应手机屏幕
     *
     * @param originalImage 原始图片
     * @return 调整后的图片
     */
    public static BufferedImage rotateImageAuto(BufferedImage originalImage) {
        if (originalImage.getHeight() >= originalImage.getWidth()) {
            return originalImage;
        }

        int type = originalImage.getColorModel().getTransparency();
        int w = originalImage.getWidth();
        int h = originalImage.getHeight();
        BufferedImage img;
        Graphics2D graphics2d = (img = new BufferedImage(h, w, type)).createGraphics();
        graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.rotate(Math.toRadians(90), (float) w / 2 - (float) (w - h) / 2, (float) h / 2);
        graphics2d.drawImage(originalImage, 0, 0, null);
        graphics2d.dispose();
        return img;
    }

    /**
     * 根据指定尺寸缩放图片
     *
     * @param originalImage 原始图片
     * @param width         调整宽度
     * @param height        调整高度
     * @return 调整后的图片
     */
    public static BufferedImage zoom(BufferedImage originalImage, int width, int height) {
        BufferedImage newImage = new BufferedImage(width, height, originalImage.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return newImage;
    }

    public static BufferedImage zoom(BufferedImage originalImage, ImageThumbModeEnum mode, final int width, final int height) {
        int newWidth = width;
        int newHeight = height;

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        switch (mode) {
            case FixedHeightAndWidthZoom:
                break;
            case FixedWidthZoom:
                newHeight = originalHeight * width / originalWidth;
                break;
            case FixedHeightZoom:
                newWidth = originalWidth * height / originalHeight;
                break;
            case FixedHeightAndWidthCrop:
                int x1, x2, y1, y2;
                x1 = (originalWidth - width) / 2;
                x1 = Math.max(x1, 0);
                y1 = (originalHeight - height) / 2;
                y1 = Math.max(y1, 0);
                x2 = width + x1 - 1;
                x2 = x2 > originalWidth ? originalWidth - 1 : x2;
                y2 = height + y1 - 1;
                y2 = y2 > originalHeight ? originalHeight - 1 : y2;
                return crop(originalImage, x1, y1, x2, y2);
        }

        return zoom(originalImage, newWidth, newHeight);
    }

    /**
     * 缩略图
     *
     * @param originalImage 原图
     * @param newSize       新尺寸
     * @return 新缩略图
     */
    public static BufferedImage thumbnail(BufferedImage originalImage, final int newSize) {
        float originalWidth = originalImage.getWidth();
        float originalHeight = originalImage.getHeight();

        BufferedImage img;
        if (originalWidth > originalHeight) {
            float scaledHeight = (originalHeight / originalWidth) * newSize;

            img = new BufferedImage(newSize, (int) scaledHeight, originalImage.getType());
            Image scaledImage = originalImage.getScaledInstance(newSize, (int) scaledHeight, Image.SCALE_SMOOTH);
            img.createGraphics().drawImage(scaledImage, 0, 0, null);
        } else if (originalWidth < originalHeight) {
            float scaledWidth = (originalWidth / originalHeight) * newSize;

            img = new BufferedImage((int) scaledWidth, newSize, originalImage.getType());
            Image scaledImage = originalImage.getScaledInstance((int) scaledWidth, newSize, Image.SCALE_SMOOTH);
            img.createGraphics().drawImage(scaledImage, 0, 0, null);
        } else {
            img = new BufferedImage(newSize, newSize, originalImage.getType());
            Image scaledImage = originalImage.getScaledInstance(newSize, newSize, Image.SCALE_SMOOTH);
            img.createGraphics().drawImage(scaledImage, 0, 0, null);
        }
        return img;
    }

    /**
     * 缩略图
     *
     * @param originalImage 原图
     * @param newSize       新尺寸
     * @param direction     尺寸的方向：W宽/H高
     * @return 新缩略图
     */
    public static BufferedImage thumbnail(BufferedImage originalImage, final int newSize, final char direction) {
        float originalWidth = originalImage.getWidth();
        float originalHeight = originalImage.getHeight();

        BufferedImage img;
        if (direction == 'H') {
            float scaledWidth = (originalWidth / originalHeight) * (float) newSize;
            img = new BufferedImage((int) scaledWidth, newSize, originalImage.getType());
            Image scaledImage = originalImage.getScaledInstance((int) scaledWidth, newSize, Image.SCALE_SMOOTH);
            img.createGraphics().drawImage(scaledImage, 0, 0, null);
        } else {
            float scaledHeight = (originalHeight / originalWidth) * (float) newSize;
            img = new BufferedImage(newSize, (int) scaledHeight, originalImage.getType());
            Image scaledImage = originalImage.getScaledInstance(newSize, (int) scaledHeight, Image.SCALE_SMOOTH);
            img.createGraphics().drawImage(scaledImage, 0, 0, null);
        }
        return img;
    }

    /**
     * 裁剪
     *
     * @param originalImage 原图
     * @param startX        裁剪的起始位置X坐标
     * @param startY        裁剪的起始位置Y坐标
     * @param endX          裁剪的结束位置X坐标
     * @param endY          裁剪的结束位置Y坐标
     * @return 裁剪后的图片
     */
    public static BufferedImage crop(BufferedImage originalImage, int startX, int startY, int endX, int endY) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        if (startX <= -1) {
            startX = 0;
        }
        if (startY <= -1) {
            startY = 0;
        }
        if (endX <= -1) {
            endX = width - 1;
        }
        if (endY <= -1) {
            endY = height - 1;
        }
        BufferedImage result = new BufferedImage(endX, endY, originalImage.getType());
        for (int y = startY; y < endY + startY; y++) {
            for (int x = startX; x < endX + startX; x++) {
                int rgb = originalImage.getRGB(x, y);
                result.setRGB(x - startX, y - startY, rgb);
            }
        }
        return result;
    }

    /**
     * 转换BufferedImage 数据为byte数组
     *
     * @param image  Image对象
     * @param format image格式字符串.如"gif","png"
     * @return byte数组
     * @throws IOException IOException
     */
    public static byte[] imageToBytes(BufferedImage image, ImageFormatEnum format) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, format.toString(), out);
            return out.toByteArray();
        }
    }

    public static ImageFormatEnum getImageFormat(String extensionName) {
        if (extensionName == null)
            return ImageFormatEnum.jpg;

        if (extensionName.contains("ico")) {
            return ImageFormatEnum.ico;
        } else if (extensionName.contains("gif")) {
            return ImageFormatEnum.gif;
        } else if (extensionName.contains("png")) {
            return ImageFormatEnum.png;
        } else {
            return ImageFormatEnum.jpg;
        }
    }

    /**
     * 转换byte数组为Image
     *
     * @param bytes 图片内容
     * @return 图片
     * @throws IOException IOException
     */
    public static BufferedImage bytesToImage(byte[] bytes) throws IOException {
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            return ImageIO.read(in);
        }
    }
}
