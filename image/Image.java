package image;

import java.awt.*;
import java.io.IOException;

/**
 * Facade for the image module and an interface representing an image.
 *
 * @author Dan Nirel
 */
public interface Image {
    /**
     * Return the color pixel of given x,y coordinates
     *
     * @param x the width axis
     * @param y the height axis
     * @return the coordinates color
     */
    Color getPixel(int x, int y);

    /**
     * @return Image width
     */
    int getWidth();

    /**
     * @return Image height
     */
    int getHeight();

    /**
     * Open an image from file. Each dimensions of the returned image is guaranteed
     * to be a power of 2, but the dimensions may be different.
     *
     * @param filename a path to an image file on disk
     * @return an object implementing Image if the operation was successful,
     * null otherwise
     */
    static Image fromFile(String filename) {
        try {
            return new FileImage(filename);
        } catch (IOException ioe) {
            return null;
        }
    }

    /**
     * Allows iterating the pixels' colors by order (first row, second row and so on).
     *
     * @return an Iterable<Color> that can be traversed with a foreach loop
     */
    default Iterable<Color> pixels() {
        return new ImageIterableProperty<>(this, this::getPixel);
    }

    /**
     * Allose iterating on sub images of image at the given pixel
     *
     * @param pixels pixel of each sub-square image
     * @return Iterator of sub images
     */
    default Iterable<Image> squareSubImagesOfSize(int pixels) {
        return new SubImagesIterator(this, pixels);
    }
}

