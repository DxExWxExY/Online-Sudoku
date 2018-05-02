package code.Sudoku;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;


/**
 * A special panel class to display a Sudoku board modeled by the
 * {@link Board} class. You need to write code for
 * the paint() method.
 *
 * @see Board
 * @author Yoonsik Cheon
 */
@SuppressWarnings("serial")
public class BoardPanel extends JPanel implements HistoryEnabler{


    private static Color boardColor = new Color(70, 70, 70);

    private HistoryNode hPointer;
    private int squareSize, hx, hy;
    private boolean hover, reset, invalid, win;
    int sx, sy;
    boolean highlightSqr;


    /**
     * Constructor given a mouse click listener
     * @param listener Determines whether the board panel was clicked
     */
    BoardPanel(ClickListener listener) {
        hPointer = history;
        addMouseMotionListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e
             * @since 1.6
             */
            @Override
            public void mouseMoved(MouseEvent e) {
                if (hover) {
                    int xy = locateSquare(e.getX(), e.getY());
                    hx = xy / 100;
                    hy = xy % 100;
                    repaint();
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int xy = locateSquare(e.getX(), e.getY());
                if (xy >= 0) {
                    listener.clicked(xy / 100, xy % 100);
                }
            }

            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
            }

            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
            }
        });
    }

    /**
     * Resets the HistoryNode pointer and sets a sound flag
     */
    public void reset() {
        reset = true;
        playSound();
        resetPointer();
    }

    /**
     * Setter for this.invalid
     * @param value value to set this.invalid to
     */
    public void setInvalid(boolean value) {
        invalid = value;
    }

    /**
     * Setter for this.highlightSqr
     * @param value value to set this.highlightSqr to
     */
    public void setHighlightSqr(boolean value) {highlightSqr = value; }

    /**
     * Setter for this.win
     * @value value to set this.win to
     */
    public void setWin(boolean value) {
        win = value;
    }
    /**
     * Getter for sx
     * @return this.sx
     */
    public int getSx() {
        return this.sx;
    }

    /**
     * Getter for sy
     * @return this.sy
     */
    public int getSy() { return this.sy; }

    /**
     * Draw the associated board.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        /* Determine the square size */
        Dimension dim = getSize();
        squareSize = Math.min(dim.width, dim.height) / hPointer.getBoard().getSize();

        /* Draw the background */
        g.setColor(boardColor);
        g.fillRect(0, 0, squareSize * hPointer.getBoard().getSize(), squareSize * hPointer.getBoard().getSize());
        setBackground(SudokuDialog.BACKGROUND);

        /* Draw changes in board */
        playSound();
        highlightInvalid(g);
        highlightHovered(g);
        highlightSelected(g);
        drawNumbers(g);
        insideLines(g);
        outsideBox(g);
    }

    /**
     * This method plays a sound depending on which variable was set to true..
     */
    void playSound() {
        try {
            if(invalid) {
                InputStream song1 = getClass().getResourceAsStream("/sound/error.wav");
                AudioStream audioStream = new AudioStream(song1);
                AudioPlayer.player.start(audioStream);
                invalid = false;
            }
            else if(reset) {
                InputStream song1 = getClass().getResourceAsStream("/sound/new.wav");
                AudioStream audioStream = new AudioStream(song1);
                AudioPlayer.player.start(audioStream);
                reset = false;
            }
            else if(win) {
                InputStream song1 = getClass().getResourceAsStream("/sound/win.wav");
                AudioStream audioStream = new AudioStream(song1);
                AudioPlayer.player.start(audioStream);
                win = false;
            }
        }
        catch(Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }

    void isValidMove() {
        invalid = !hPointer.isValid(sy, sx);
    }

    /**
     * Given a screen coordinate, return the indexes of the corresponding square
     * or -1 if there is no square.
     * The indexes are encoded and returned as x*100 + y,
     * where x and y are 0-based column/row indexes.
     */
    private int locateSquare(int x, int y) {
        if (x < 0 || x > hPointer.getBoard().getSize() * squareSize
                || y < 0 || y > hPointer.getBoard().getSize() * squareSize) {
            return -1;
        }
        int xx = x / squareSize;
        int yy = y / squareSize;
        return xx * 100 + yy;
    }

    /**
     * This method draws the numbers in the matrix, the color
     * depends whether it was a valid entry or not.
     *
     * @param g This method receives the Graphics class to draw the numbers.
     */
    private void drawNumbers(Graphics g) {
        for (int i = 0; i < hPointer.getBoard().getSize(); i++) {
            for (int j = 0; j < hPointer.getBoard().getSize(); j++) {
                //if the number in the matrix are not 0's
                if (hPointer.getBoard().getElement(i, j) != 0) {
                    //if valid
                    if (hPointer.getBoard().isValid(i, j)) {
                        g.setColor(Color.WHITE);
                        g.drawString(String.valueOf(hPointer.getBoard().getElement(i, j)), (j * squareSize) + (squareSize / 2 - 3), (i * squareSize) + (squareSize / 2 + 4));
                    }
                    //if not valid
                    else if (!hPointer.getBoard().isValid(i, j)) {
                        g.setColor(Color.BLACK);
                        g.drawString(String.valueOf(hPointer.getBoard().getElement(i, j)), (j * squareSize) + (squareSize / 2 - 3), (i * squareSize) + (squareSize / 2 + 4));
                    }
                }
            }
        }
    }

    /**
     * This method highlights a number background if the entry was invalid.
     *
     * @param g This method receives the Graphics class in order to draw the square.
     */
    private void highlightInvalid(Graphics g) {
        for (int i = 0; i < hPointer.getBoard().getSize(); i++) {
            for (int j = 0; j < hPointer.getBoard().getSize(); j++) {
                if (!hPointer.getBoard().isValid(i, j) && hPointer.getBoard().getElement(i, j) != 0) {
                    g.setColor(Color.WHITE);
                    g.fillRect(j * squareSize, i * squareSize, squareSize, squareSize);
                } else if (!hPointer.getBoard().isMutable(i, j)) {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(j * squareSize, i * squareSize, squareSize, squareSize);
                }
            }
        }
    }

    /**
     * This method draws the outside lines to define the sub-grid of the board
     *
     * @param g This method receives the Graphics class in order to draw the lines
     */
    private void outsideBox(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawLine(0, 0, squareSize * hPointer.getBoard().getSize(), 0);             //top line
        g.drawLine(0, 0, 0, squareSize * hPointer.getBoard().getSize());             //left line
        g.drawLine(0, squareSize * hPointer.getBoard().getSize(), squareSize * hPointer.getBoard().getSize(), squareSize * hPointer.getBoard().getSize()); //bottom line
        g.drawLine(squareSize * hPointer.getBoard().getSize(), 0, squareSize * hPointer.getBoard().getSize(), squareSize * hPointer.getBoard().getSize()); //right line
        /*this draw the grid in the rectangle*/
        for (int i = 0; i < 276; i++) {
            if ((i % (squareSize * Math.sqrt(hPointer.getBoard().getSize())) == 0)) {
                g.drawLine(i, 0, i, squareSize * hPointer.getBoard().getSize());
                g.drawLine(0, i, squareSize * hPointer.getBoard().getSize(), i); //bottom line
            }
        }
    }

    /**
     * This method draw the inside lines to define the total rows and columns of the board
     *
     * @param g method receives the Graphics class in order to draw the lines
     */
    private void insideLines(Graphics g) {
        g.setColor(Color.GRAY);
        for (int i = 0; i < 276; i = i + squareSize) {
            g.drawLine(i, 0, i, squareSize * hPointer.getBoard().getSize());
            g.drawLine(0, i, squareSize * hPointer.getBoard().getSize(), i); //bottom line

        }
    }

    /**
     * This method paints the pixels of the square selected in the board.
     *
     * @param g method receives the Graphics class in order to draw the actions
     */
    private void highlightSelected(Graphics g) {
        if (highlightSqr) {
            g.setColor(new Color(105,105,105));
            g.fillRect(sx * squareSize, sy * squareSize, squareSize, squareSize);
        }
    }

    /**
     * This method highlights the hovered cell in the board.
     *
     * @param g method receives the Graphics class in order to draw the actions.
     */
    private void highlightHovered(Graphics g) {
        if (hover) {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(hx * squareSize, hy * squareSize, squareSize, squareSize);
        }
    }

    /* HistoryEnabler contract methods */

    public void resetPointer() {
        hPointer = history;
    }

    public void movePointerBackward() {
        hPointer = hPointer.getPrevious();
    }

    public void movePointerForward() {
        hPointer = hPointer.getNext();
    }
}