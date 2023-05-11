package ascii_art.img_to_char;

import image.Image;

import java.awt.*;
import java.util.HashMap;

/**
 * This class takes care on match character to replace the image pixels
 */
public class BrightnessImgCharMatcher {
    static final double RED_RATE = (float) 0.2126;
    static final float GREEN_RATE = (float) 0.7152;
    static final float BLUE_RATE = (float) 0.0722;
    static final int CHAR_RESOLUTION = 16;
    private static final char FIRST_CHAR = 32;
    private static final char LAST_CHAR = 126;

    private final HashMap<Character, Float> charsMapToUse = new HashMap<>();
    private final HashMap<Character, Float> allCharsBrightnessMap = new HashMap<>();

    private final Image img;
    private final String font;
    private final HashMap<Image, Float> subImagesMap = new HashMap<>();
    private int minPixels = Integer.MAX_VALUE;

    /**
     * Constructor
     *
     * @param img  The wanted image to ascii art
     * @param font The font type to use
     */
    public BrightnessImgCharMatcher(Image img, String font) {
        this.img = img;
        this.font = font;
        for (char ch = FIRST_CHAR; ch <= LAST_CHAR; ch++) {
            allCharsBrightnessMap.put
                    (ch, charBrightness(CharRenderer.getImg(ch, CHAR_RESOLUTION, this.font)));
        }
    }

    /**
     * Count number of true's from given CharRenderer.getImg('C', 16, "Ariel");
     * Divide the solution by 64 =: CHAR_RESOLUTION.
     *
     * @param numCharsInRow chars per row
     * @param charSet       The set of wanted chars to use
     * @return An image represented by chars
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet) {
        this.charsMapToUse.clear();
        for (char ch : charSet) {
            this.charsMapToUse.put(ch, this.allCharsBrightnessMap.get(ch));
        }
        charsMapLinearStretch();
        int pixels = this.img.getWidth() / numCharsInRow;
        char[][] asciiArt = new char[this.img.getHeight() / pixels][numCharsInRow];
        convertImageToAscii(numCharsInRow, pixels, asciiArt);
        return asciiArt;
    }

    // Converting images to ascii
    private void convertImageToAscii(int numCharsInRow, int pixels, char[][] asciiArt) {
        int x = 0, y = 0;
        for (Image subImage : img.squareSubImagesOfSize(pixels)) {

            if (this.minPixels < pixels && (!this.subImagesMap.containsKey(subImage))) {
                this.subImagesMap.put
                        (subImage, convertImageToAsciiHelper
                                (numCharsInRow * 2, pixels / 2, subImage));
            }

            if (!this.subImagesMap.containsKey(subImage)) {
                this.subImagesMap.put(subImage, calculateImageBrightnessOf(subImage));
            }

            asciiArt[y][x] = findClosestCharToBrightnessOf(this.subImagesMap.get(subImage));
            x++;
            if (numCharsInRow <= x) {
                x = 0;
                y++;
            }
        }
        if (pixels < this.minPixels) {
            this.minPixels = pixels;
        }
    }

    // Converting Images recursion helper function
    private float convertImageToAsciiHelper(int numCharsInRow, int pixels, Image img) {
        float mean = 0;
        for (Image subImage : img.squareSubImagesOfSize(pixels)) {
            if (this.minPixels < pixels) {
                mean += convertImageToAsciiHelper
                        (numCharsInRow * 2, pixels / 2, subImage);
            }
            if (this.subImagesMap.containsKey(subImage)) {
                mean += this.subImagesMap.get(subImage);
            }
        }
        return mean / 4;
    }

    // Do a linear stretch to the brightness of the chars we're using in the correct render
    private void charsMapLinearStretch() {
        float minBrightness = 1, maxBrightness = 0;

        for (char ch : this.charsMapToUse.keySet()) {
            float charBrightness = this.charsMapToUse.get(ch);
            if (maxBrightness <= charBrightness) {
                maxBrightness = charBrightness;
            }
            if (charBrightness <= minBrightness) {
                minBrightness = charBrightness;
            }
        }

        for (char ch : this.charsMapToUse.keySet()) {
            this.charsMapToUse.put
                    (ch, (this.charsMapToUse.get(ch) - minBrightness) / (maxBrightness - minBrightness));
        }
    }

    // Calculate character brightness
    private float charBrightness(boolean[][] charImg) {
        int countTrues = 0;
        for (boolean[] rowOfPixels : charImg) {
            for (boolean pixel : rowOfPixels) {
                if (pixel) countTrues++;
            }
        }
        return (float) countTrues / (float) (charImg.length * charImg[0].length);
    }

    // Get an Image brightness and return the char which his brightness closest
    private char findClosestCharToBrightnessOf(float imgBrightness) {
        char closestChar = ' ';
        float closestBrightness = 1;

        for (char ch : this.charsMapToUse.keySet()) {
            if (Math.abs(this.charsMapToUse.get(ch) - imgBrightness) <= closestBrightness) {
                closestChar = ch;
                closestBrightness = Math.abs(this.charsMapToUse.get(ch) - imgBrightness);
            }
        }
        return closestChar;
    }

    // Get an Image and return the image brightness
    private float calculateImageBrightnessOf(Image image) {
        float countGreyColor = 0;
        int countPixels = 0;
        for (Color color : image.pixels()) {
            countPixels++;
            countGreyColor += (color.getRed() * RED_RATE);
            countGreyColor += (color.getGreen() * GREEN_RATE);
            countGreyColor += (color.getBlue() * BLUE_RATE);
        }
        return countGreyColor / (countPixels * 255);
    }
}
