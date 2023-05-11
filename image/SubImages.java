package image;

import java.awt.*;

/**
 * This class is representing a sub image of image
 */
class SubImages implements Image {

    private final Image img;
    private final int pixels;
    private final int startX;
    private final int startY;
    private int hashCache = -1;

    /**
     * SubImages constructor
     */
    public SubImages(Image img, int pixels, int startX, int startY) {
        if (startX < 0 || startY < 0 || pixels <= 0 ||
                startX + pixels > img.getWidth() || startY + pixels > img.getHeight())
            throw new IllegalArgumentException();

        this.pixels = pixels;
        if (img instanceof SubImages) {
            SubImages subImages = (SubImages) img;
            this.startX = startX + subImages.startX;
            this.startY = startY + subImages.startY;
            this.img = subImages.img;
        } else {
            this.startX = startX;
            this.startY = startY;
            this.img = img;
        }
    }

    @Override
    public Color getPixel(int x, int y) {
        if (x < 0 || y < 0 || x >= this.getWidth() || y >= this.getHeight())
            throw new IndexOutOfBoundsException();
        return this.img.getPixel(this.startX + x, this.startY + y);
    }

    @Override
    public int getWidth() {
        return this.pixels;
    }

    @Override
    public int getHeight() {
        return this.pixels;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubImages))
            return false;
        SubImages other = (SubImages) obj;

        return this.img.equals(other.img) &&
                this.startX == other.startX && this.startY == other.startY &&
                this.pixels == other.pixels;
    }

    @Override
    public int hashCode() {
        if (hashCache == -1) {
            hashCache = this.startX;
            hashCache += 2551 * this.startY;
            hashCache += 3001 * this.pixels;
            hashCache += 5381 * this.img.hashCode();
        }
        return hashCache;
    }
}
