package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class TextGraphicsConverterImpl implements TextGraphicsConverter {
    private static final double MIN_RATIO = 1.0;
    private static final double DEFAULT_MAX_RATIO = 0.0;
    private double maxRatio;
    private int maxWidth;
    private int maxHeight;
    private TextColorSchema schema;

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));

        // Проверка соотношения сторон
        if (maxRatio > DEFAULT_MAX_RATIO) {
            double widthToHeightRatio = (double) img.getWidth() / img.getHeight();
            double heightToWidthRatio = MIN_RATIO / widthToHeightRatio; // 1/ratio

            if (widthToHeightRatio > maxRatio || heightToWidthRatio > maxRatio) {
                throw new BadImageSizeException(Math.max(widthToHeightRatio, heightToWidthRatio), maxRatio);
            }
        }

        // Вычисление новых размеров с сохранением пропорций
        int newWidth = img.getWidth();
        int newHeight = img.getHeight();

        if (maxWidth > 0 && newWidth > maxWidth) {
            double scale = (double) maxWidth / newWidth;
            newWidth = maxWidth;
            newHeight = (int) (newHeight * scale);
        }

        if (maxHeight > 0 && newHeight > maxHeight) {
            double scale = (double) maxHeight / newHeight;
            newHeight = maxHeight;
            newWidth = (int) (newWidth * scale);
        }

        // Масштабирование изображения
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);

        // Конвертация в ч/б
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);

        // Используем переданную схему или схему по умолчанию
        TextColorSchema schema = this.schema != null ? this.schema : new TextColorSchemaImpl();

        // Конвертация пикселей в символы
        WritableRaster bwRaster = bwImg.getRaster();
        StringBuilder result = new StringBuilder();

        for (int heightIndex = 0; heightIndex < newHeight; heightIndex++) {
            for (int widthIndex = 0; widthIndex < newWidth; widthIndex++) {
                int color = bwRaster.getPixel(widthIndex, heightIndex, new int[3])[0];
                char symbol = schema.convert(color);
                result.append(symbol).append(symbol); // Удваиваем символ для лучшей читаемости
            }
            result.append("\n");
        }

        return result.toString();
    }

    @Override
    public void setMaxWidth(int width) {
        this.maxWidth = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}