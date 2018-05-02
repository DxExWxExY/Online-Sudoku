package code.Sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Objects;

/**
 * A dialog template for playing simple Sudoku games.
 * You need to write code for three callback methods:
 * newClicked(int), numberClicked(int) and boardClicked(int,int).
 *
 * @author Yoonsik Cheon
 */
@SuppressWarnings("serial")
public class SudokuDialog extends JFrame implements HistoryEnabler {

    /** Default dimension of the dialog. */
    private final static Dimension DEFAULT_SIZE = new Dimension(310, 450);
    private final static String IMAGE_DIR = "/image/";
    protected final static Color BACKGROUND = new Color(47,76,76);

    /** Message bar to display various messages. */
    private JPanel content = new JPanel();
    private JPanel toolbar = new JPanel();
    private JPanel numberButtons;

    /** Special panel to display a sudoku board */
    private final BoardPanel boardPanel = new BoardPanel(this::boardClicked);

    private HistoryNode hPointer;

    /**
     * Create a new dialog
     */
    public SudokuDialog() {
        this(Toolkit.getDefaultToolkit().getScreenSize());
    }

    /**
     * Create a new dialog of the given screen dimension
     */
    private SudokuDialog(Dimension dim) {
        super("Sudoku");
        setLocation(dim.width/2-155, dim.height/2-225);
        setSize(DEFAULT_SIZE);

        hPointer = history;

        configureMenu();
        configureUI();
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Callback to be invoked when a square of the history is clicked
     *
     * @param x 0-based row index of the clicked square
     * @param y 0-based column index of the clicked square
     */
    private void boardClicked(int x, int y) {
        boardPanel.sx = x;
        boardPanel.sy = y;
        boardPanel.highlightSqr = true;
        content.remove(numberButtons);
        numberButtons = makeNumberButtons();
        content.add(numberButtons);
        content.revalidate();
        boardPanel.repaint();
    }

    /**
     * Callback to be invoked when a number button is clicked
     *
     * @param number Clicked number (1-9), or 0 for "X"
     */
    private void numberClicked(int number) {
        if (hPointer.getBoard().isMutable(boardPanel.getSy(), boardPanel.getSx())) {
            history.newNode();
            movePointerForward();
            boardPanel.movePointerForward();

            if (number == 0) {
                hPointer.getBoard().deleteElement(boardPanel.getSy(), boardPanel.getSx());
            }
            else {
                hPointer.getBoard().setElement(boardPanel.getSy(), boardPanel.getSx(), number);
                boardPanel.isValidMove();
            }
        }
        else {
            boardPanel.setInvalid(true);
        }
        boardPanel.setHighlightSqr(false);
        boardPanel.repaint();
        solved();
    }

    /**
     * Configure the menu items
     */
    protected void configureMenu() {

        /* Declare menu items */
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem newGame = new JMenuItem("New Game",KeyEvent.VK_N);
        JMenuItem exit = new JMenuItem("Exit",KeyEvent.VK_Q);

        /*Menu Accelerators */
        menu.setMnemonic(KeyEvent.VK_B);
        newGame.setAccelerator(KeyStroke.getKeyStroke("alt A"));
        exit.setAccelerator(KeyStroke.getKeyStroke("alt E"));


        /*Menu Items Icons */
        newGame.setIcon(createImageIcon("new.png"));
        exit.setIcon(createImageIcon("exit.png"));

        /* Incorporate menu items into menu, and menu into menu bar */
        menu.add(newGame);
        menu.add(exit);
        mb.add(menu);
        setJMenuBar(mb);
        setLayout(null);
        setVisible(true);

        /*Menu Items Listeners */
        newGame.addActionListener(e -> {
            Object[] options = {"4x4", "9x9", "Exit"};
            int n = JOptionPane.showOptionDialog(null, "Select a Sudoku Size",
                    "New Game", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[2]);

            switch (n) {
                case JOptionPane.YES_OPTION:
                    history.newGame(4);
                    resetNumberButtons();
                    break;
                case JOptionPane.NO_OPTION:
                    history.newGame(9);
                    resetNumberButtons();
                    break;
                case JOptionPane.CANCEL_OPTION:
                    System.exit(0);
                    break;
            }

            resetPointer();
            boardPanel.reset();
            content.revalidate();
            repaint();
        });
        exit.addActionListener(e -> System.exit(0));
    }

    /**
     * Resets the number buttons when a new game begins.
     */
    private void resetNumberButtons() {
        content.remove(numberButtons);
        numberButtons = makeNumberButtons();
        content.add(numberButtons);
    }

    /**
     * Configure the UI
     */
    protected void configureUI() {
        setIconImage(Objects.requireNonNull(createImageIcon("sudoku.png")).getImage());
        setLayout(new BorderLayout());

        /* Create a buttons JPanel and add it to the main JPanel */
        JPanel buttons = makeControlPanel();
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 16, 0, 16));
        buttons.setBackground(BACKGROUND);
        add(buttons, BorderLayout.NORTH);

        /* Create a BoardPanel JPanel and add it to the main JPanel */
        JPanel history = new JPanel();
        history.setBorder(BorderFactory.createEmptyBorder(10, 16, 0, 16));
        history.setLayout(new GridLayout(1, 1));
        history.add(boardPanel);
        history.setBackground(BACKGROUND);
        add(history, BorderLayout.CENTER);
    }

    /**
     * Method used to create the options buttons.
     *
     * @param name String of the file name.
     * @param command Key event to be associated with the button.
     * @return JButton consisting of a command
     * */
    protected JButton makeOptionButtons(String name, int command) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(35,35));
        button.setIcon(createImageIcon(name));
        button.setBackground(BACKGROUND);
        button.setBorder(null);
        button.setFocusable(false);
        button.setMnemonic(command);
        button.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             * @param e
             */
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.GRAY);
            }

            /**
             * {@inheritDoc}
             * @param e
             */
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BACKGROUND);
            }
        });
        return button;
    }

    /**
     * Create a JPanel consisting of redo, undo, solve, and isSolvable buttons
     * @return JPanel with tool options
     */
    protected JPanel makeToolBar() {

        /* Create tool bar JPanel and JButtons */
        JPanel toolBar = new JPanel();
        JButton undo = makeOptionButtons("undo.png", KeyEvent.VK_Z);
        JButton redo = makeOptionButtons("redo.png", KeyEvent.VK_Y);
        JButton solve = makeOptionButtons("solve.png", KeyEvent.VK_S);
        JButton can = makeOptionButtons("can.png", KeyEvent.VK_C);

        /* Add action listeners */
        undo.addActionListener(e -> undo());
        redo.addActionListener(e -> redo());
        solve.addActionListener(e -> solve());
        can.addActionListener(e-> isSolvable());

        /* Add buttons to tool bar */
        toolBar.add(undo);
        toolBar.add(redo);
        toolBar.add(solve);
        toolBar.add(can);
        toolBar.setBackground(BACKGROUND);

        return toolBar;
    }

    /**
     * Create the JPanel that includes a sub-JPanel with JButtons for options and
     * a sub-JPanel with the tool bar
     * @return JPanel with game options
     */
    private JPanel makeControlPanel() {
        numberButtons = makeNumberButtons();
        toolbar = makeToolBar();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.add(toolbar);
        content.add(numberButtons);
        content.setBackground(BACKGROUND);

        return content;
    }

    /**
     *  Method that iteratively makes all number JButtons and puts them in a JPanel
     * @return JPanel containing JButtons for possible numbers to place on the game board
     */
    protected JPanel makeNumberButtons() {
        JPanel numberButtons = new JPanel(new FlowLayout());
        int maxNumber = hPointer.getBoard().getSize() + 1;
        for (int i = 1; i <= maxNumber; i++) {
            int number = i % maxNumber;
            JButton button = new JButton(number == 0 ? "X" : String.valueOf(number));
            button.setFocusPainted(false);
            button.setMargin(new Insets(0, 2, 0, 2));
            button.addActionListener(e -> numberClicked(number));
            if (!hPointer.getBoard().ruleChecker(boardPanel.getSy(),boardPanel.getSx(),i) && number != 0) {
                button.setEnabled(false);
            }
            numberButtons.add(button);
        }
        numberButtons.setAlignmentX(CENTER_ALIGNMENT);
        numberButtons.setBackground(BACKGROUND);
        return numberButtons;
    }

    /**
     * Create an image icon from the given image file
     */
    protected ImageIcon createImageIcon(String name) {
        URL imageUrl = getClass().getResource(IMAGE_DIR + name);
        if (imageUrl != null) {
            return new ImageIcon(imageUrl);
        }
        return null;
    }

    /**
     * Goes back to previous game state, essentially "undoing" a move if possible
     */
    protected void undo() {
        if(hPointer.getPrevious() != null) {
            movePointerBackward();
            boardPanel.movePointerBackward();
            boardPanel.setHighlightSqr(false);
            boardPanel.repaint();
        }
    }

    /**
     * Goes forward to next game state, essentially "redoing" a move if possible
     */
    protected void redo() {
        if(hPointer.getNext() != null) {
            movePointerForward();
            boardPanel.movePointerForward();
            boardPanel.setHighlightSqr(false);
            boardPanel.repaint();
        }
    }

    /**
     * Method called when the solve button is pressed.
     * */
    protected void solve() {
        if (hPointer.getBoard().isSolvable()) {
            history.newNode();
            movePointerForward();
            boardPanel.movePointerForward();

            hPointer.setWasSolved();
            hPointer.getBoard().solveSudoku();
            boardPanel.repaint();
        }
        else {
            JOptionPane.showMessageDialog(null, "This board cannot be solved.", "Can It Be Solved?", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method called when the can solve button is called.
     * */
    protected void isSolvable() {
        if (!hPointer.getBoard().isSolved()) {
            if (hPointer.getBoard().isSolvable()) {
                JOptionPane.showMessageDialog(null, "This board CAN be solved.", "Can It Be Solved?", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "This board CANNOT be solved.", "Can It Be Solved?", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * This method checks if all the numbers in the matrix meet the game rules.
     * If so, prompts the user to start a new game or to quit.
     */
    private void solved() {
        if (hPointer.getBoard().isSolved() && !hPointer.getBoard().getWasSolved()) {
            boardPanel.setWin(true);
            boardPanel.playSound();
            Object[] options = {"New Game", "Exit"};
            int solved = JOptionPane.showOptionDialog(null, "You Won!",
                    "Congratulations", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[1]);
            if (solved == JOptionPane.YES_OPTION) {
                history.newGame(history.getBoard().getSize());
                resetPointer();
                boardPanel.reset();
            }
            else {
                System.exit(0);
            }
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
