package code;

public interface ClickListener {
    /**
     * Callback to notify clicking of a square.
     * @param x 0-based column index of the clicked square
     * @param y 0-based row index of the clicked square
     */
    void clicked(int x, int y);
}