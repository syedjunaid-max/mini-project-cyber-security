package steg;

import java.awt.image.BufferedImage;

public class SteganographyUtil {

    public static BufferedImage hideMessage(BufferedImage image, String message) {
        BufferedImage stegoImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        int messageLength = message.length();
        int[] msgBits = toBits(message);
        int bitIndex = 0;

        outer:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);

                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // Only modify blue channel
                if (bitIndex < msgBits.length) {
                    b = (b & 0xFE) | msgBits[bitIndex++];
                }

                int newRGB = (r << 16) | (g << 8) | b;
                stegoImage.setRGB(x, y, newRGB);

                if (bitIndex >= msgBits.length) {
                    break outer;
                }
            }
        }

        return stegoImage;
    }

    public static String extractMessage(BufferedImage image, int maxLength) {
        int[] bits = new int[maxLength * 8];
        int bitIndex = 0;

        outer:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int b = rgb & 0xFF;
                bits[bitIndex++] = b & 1;
                if (bitIndex >= bits.length) break outer;
            }
        }

        return fromBits(bits);
    }

    private static int[] toBits(String message) {
        byte[] bytes = message.getBytes();
        int[] bits = new int[bytes.length * 8];

        for (int i = 0; i < bytes.length; i++) {
            for (int j = 7; j >= 0; j--) {
                bits[i * 8 + (7 - j)] = (bytes[i] >> j) & 1;
            }
        }
        return bits;
    }

    private static String fromBits(int[] bits) {
        byte[] bytes = new byte[bits.length / 8];

        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < 8; j++) {
                bytes[i] = (byte) ((bytes[i] << 1) | bits[i * 8 + j]);
            }
        }
        return new String(bytes).trim();
    }
} 
