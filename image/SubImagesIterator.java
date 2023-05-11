package image;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This is iterator of sub images of given image
 */
class SubImagesIterator implements Iterable<Image> {

    private final Image img;
    private final int pixels;

    /**
     * SubImages constructor
     */
    SubImagesIterator(Image img, int pixels) {
        this.img = img;
        this.pixels = pixels;
    }

    @Override
    public Iterator<Image> iterator() {
        return new Iterator<>() {
            int x = 0, y = 0;

            @Override
            public boolean hasNext() {
                return y < img.getHeight();
            }

            @Override
            public Image next() {
                if (!hasNext()) throw new NoSuchElementException();
                Image subImage = new SubImages(img, pixels, x, y);
                x += pixels;
                if (x >= img.getWidth()) {
                    x = 0;
                    y += pixels;
                }
                return subImage;
            }
        };
    }
}