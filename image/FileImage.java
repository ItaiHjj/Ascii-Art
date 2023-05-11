package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * A package-private class of the package image.
 *
 * @author Dan Nirel
 */
class FileImage implements Image {

    private static final Color DEFAULT_COLOR = Color.WHITE;

    private final Color[][] pixelArray;

    /**
     * Constructor of FileImage
     *
     * @param filename Name of File
     * @throws IOException Exception to throw
     */
    FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));
        int origWidth = im.getWidth(), origHeight = im.getHeight();

        int newHeight = ceilPowerOfTwo(origHeight);
        int newWidth = ceilPowerOfTwo(origWidth);
        this.pixelArray = new Color[newHeight][newWidth];

        float frameX = (newWidth - origWidth) / 2.0f;
        float frameY = (newHeight - origHeight) / 2.0f;

        for (int y = 0; y < pixelArray.length; y++) {
            for (int x = 0; x < pixelArray[y].length; x++) {
                //if this pixel is not in the margins, take value from image
                if (x >= (int) Math.floor(frameX) && x < pixelArray[y].length - Math.ceil(frameX) &&
                        y >= (int) Math.floor(frameY) && y < pixelArray.length - Math.ceil(frameY)) {
                    pixelArray[y][x] = new Color(im.getRGB(
                            x - (int) Math.floor(frameX),
                            y - (int) Math.floor(frameY)));
                } else {
                    pixelArray[y][x] = DEFAULT_COLOR;
                }
            }
        }
    }

    /**
     * @return image pixels width
     */
    public int getWidth() {
        return this.pixelArray[0].length;
    }

    /**
     * @return image pixels height
     */
    @Override
    public int getHeight() {
        return this.pixelArray.length;
    }

    /**
     * @param x coordinate (Represent Width)
     * @param y coordinate (Represent Height)
     * @return the pixel (x,y) of img
     */
    @Override
    public Color getPixel(int x, int y) {
        return this.pixelArray[y][x];
    }


    // Find the greater and closes power of 2 to x
    private int ceilPowerOfTwo(int num) {
        int power = 1;
        while (power < num) power = power << 1;
        return power;
    }
}
