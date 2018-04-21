package code;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Objects;
import javax.swing.*;

/**
 * A dialog template for playing simple Sudoku games.
 * You need to write code for three callback methods:
 * newClicked(int), numberClicked(int) and boardClicked(int,int).
 *
 * @author Yoonsik Cheon
 */
@SuppressWarnings("serial")
public class SudokuDialog extends JFrame {

    /** Default dimension of the dialog. */
    private final static Dimension DEFAULT_SIZE = new Dimension(310, 450);
    private final static String IMAGE_DIR = "/image/";
    final static Color BACKGROUND = new Color(47,76,76);

    /** Sudoku historyIterator. */
    private HistoryNode historyIterator;

    /** Special panel to display a Sudoku historyIterator. */
    private BoardPanel boardPanel;

    /** Message bar to display various messages. */
    private JLabel msgBar = new JLabel("");
    private JPanel content = new JPanel();
    private JPanel numberButtons;

    /**
     * Create a new dialog.
     */
    private SudokuDialog() {
        this(Toolkit.getDefaultToolkit().getScreenSize());
    }

    /**
     * Create a new dialog of the given screen dimension.
     */
    private SudokuDialog(Dimension dim) {
        super("Sudoku");
        setLocation(dim.width/2-155, dim.height/2-225);
        setSize(DEFAULT_SIZE);
        initHistory();
        boardPanel = new BoardPanel(historyIterator.getBoard(), this::boardClicked);
        configureMenu();
        configureUI();
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

    }

    /**
     * Callback to be invoked when a square of the historyIterator is clicked.
     *
     * @param x 0-based row index of the clicked square.
     * @param y 0-based column index of the clicked square.
     */
    private void boardClicked(int x, int y) {
//        System.out.println("boardClicked");
        boardPanel.sx = x;
        boardPanel.sy = y;
        boardPanel.highlightSqr = true;
        content.remove(numberButtons);
        numberButtons = makeNumberButtons();
        content.add(numberButtons);
        content.revalidate();
        boardPanel.repaint();
        showMessage(String.format("Board clicked: x = %d, y = %d", x, y));
    }

    /**
     * Callback to be invoked when a number button is clicked.
     *
     * @param number Clicked number (1-9), or 0 for "X".
     */
    private void numberClicked(int number) {
        if (historyIterator.isMutable(boardPanel.sy, boardPanel.sx)) {
            createHistory();
            if (number == 0) {
                historyIterator.deleteElement(boardPanel.sy, boardPanel.sx);
                showMessage("Number Deleted");
            }
            else {
                historyIterator.setElement(boardPanel.sy, boardPanel.sx, number);
                boardPanel.invalid = !historyIterator.isValid(boardPanel.sy, boardPanel.sx);
                showMessage(String.format("Inserted Number %d", number));
            }
            boardPanel.setBoard(historyIterator.getBoard());
        }
        else {
            boardPanel.invalid = true;
        }
        boardPanel.highlightSqr = false;
        boardPanel.repaint();
    }

    /**
     * Display the given string in the message bar.
     *
     * @param msg Message to be displayed.
     */
    private void showMessage(String msg) {
        msgBar.setText(msg);
    }

    /**
     * Configure the UI.
     */
    private void configureMenu() {
        JMenu menu = new JMenu("Menu");
        JMenuBar mb = new JMenuBar();
        JMenuItem newGame, exit;
        setJMenuBar(mb);
        /*Menu Items Declaration*/
        newGame = new JMenuItem("New Game",KeyEvent.VK_N);
        exit = new JMenuItem("Exit",KeyEvent.VK_Q);
        /*Menu Accelerators*/
        newGame.setAccelerator(KeyStroke.getKeyStroke("alt A"));
        exit.setAccelerator(KeyStroke.getKeyStroke("alt E"));
        /*Menu Items Icons*/
        newGame.setIcon(createImageIcon("new.png"));
        exit.setIcon(createImageIcon("exit.png"));

        menu.add(newGame);
        menu.add(exit);
        menu.setMnemonic(KeyEvent.VK_B);
        mb.add(menu);
        setJMenuBar(mb);
        setLayout(null);
        setVisible(true);
        /*Menu Items Listeners*/
        newGame.addActionListener(e -> {
            Object[] options = {"4x4", "9x9", "Exit"};
            int n = JOptionPane.showOptionDialog(null, "Select a Sudoku Size",
                    "New Game", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[2]);
            switch (n) {
                case JOptionPane.YES_OPTION:
                    historyIterator.reset(4);
                    content.remove(numberButtons);
                    numberButtons = makeNumberButtons();
                    content.add(numberButtons);
                    break;
                case JOptionPane.NO_OPTION:
                    historyIterator.reset(9);
                    content.remove(numberButtons);
                    numberButtons = makeNumberButtons();
                    content.add(numberButtons);
                    break;
                case JOptionPane.CANCEL_OPTION:
                    System.exit(0);
                    break;
            }
            boardPanel.reset = true;
            historyIterator.generateBoard();
            content.revalidate();
            repaint();
        });
        exit.addActionListener(e -> System.exit(0));
    }

    /**
     * Configure the UI.
     */
    private void configureUI() {
        setIconImage(Objects.requireNonNull(createImageIcon("sudoku.png")).getImage());
        setLayout(new BorderLayout());
        JPanel buttons = makeControlPanel();
        // boarder: top, left, bottom, right
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 16, 0, 16));
        buttons.setBackground(BACKGROUND);
        add(buttons, BorderLayout.NORTH);
        JPanel historyIterator = new JPanel();
        historyIterator.setBorder(BorderFactory.createEmptyBorder(10, 16, 0, 16));
        historyIterator.setLayout(new GridLayout(1, 1));
        historyIterator.add(boardPanel);
        historyIterator.setBackground(BACKGROUND);
        add(historyIterator, BorderLayout.CENTER);
        msgBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 0));
        msgBar.setBackground(BACKGROUND);
        //add(msgBar, BorderLayout.SOUTH);
    }

    /**
     * Method used to create the options buttons.
     *
     * @param name String of the file name.
     * @param command Key event to be associated with the button.
     * */
    private JButton makeOptionButtons(String name, int command) {
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
     * Create a control panel consisting of new and number buttons.
     */
    private JPanel makeToolBar() {
        JPanel toolBar = new JPanel();
        JButton undo, redo, solve, can;
        undo = makeOptionButtons("undo.png", KeyEvent.VK_Z);
        redo = makeOptionButtons("redo.png", KeyEvent.VK_Y);
        solve = makeOptionButtons("solve.png", KeyEvent.VK_S);
        can = makeOptionButtons("can.png", KeyEvent.VK_C);
        undo.addActionListener(e -> undo());
        redo.addActionListener(e -> redo());
        solve.addActionListener(e -> solve());
        can.addActionListener(e-> isSolvable());
        toolBar.add(undo);
        toolBar.add(redo);
        toolBar.add(solve);
        toolBar.add(can);
        toolBar.setBackground(BACKGROUND);
        return toolBar;
    }

    /**
     * Create a control panel consisting of new and number buttons.
     */
    private JPanel makeControlPanel() {
        // buttons labeled 1, 2, ..., 9, and X.
        numberButtons = makeNumberButtons();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.add(makeToolBar());
        content.add(numberButtons);
        content.setBackground(BACKGROUND);
        return content;
    }

    /**
     * Method in charge of creating the number buttons.
     * */
    private JPanel makeNumberButtons() {
        JPanel numberButtons = new JPanel(new FlowLayout());
        int maxNumber = historyIterator.size() + 1;
        for (int i = 1; i <= maxNumber; i++) {
            int number = i % maxNumber;
            JButton button = new JButton(number == 0 ? "X" : String.valueOf(number));
            button.setFocusPainted(false);
            button.setMargin(new Insets(0, 2, 0, 2));
            button.addActionListener(e -> numberClicked(number));
            if (!historyIterator.ruleChecker(boardPanel.sy,boardPanel.sx,i) && number != 0) {
                button.setEnabled(false);
            }
            numberButtons.add(button);
        }
        numberButtons.setAlignmentX(CENTER_ALIGNMENT);
        numberButtons.setBackground(BACKGROUND);
        return numberButtons;
    }

    /**
     * Create an image icon from the given image file.
     */
    private ImageIcon createImageIcon(String name) {
        URL imageUrl = getClass().getResource(IMAGE_DIR + name);
        if (imageUrl != null) {
            return new ImageIcon(imageUrl);
        }
        return null;
    }

    /**
     * Creates history for undo and redo functions of Sudoku game
     *
     */
    private void createHistory() {
        try {
            historyIterator.setNext(new HistoryNode(historyIterator.getBoard().clone(), historyIterator));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        historyIterator = historyIterator.getNext();
    }

    /**
     * Method used to create a HistoryNode for undo and redo.
     * */
    private void initHistory() {
        historyIterator = new HistoryNode(new Board(4));
        historyIterator.generateBoard();

    }

    /**
     * Goes back to previous game state, essentially "undoing" a move if possible
     */
    private void undo() {
        if(historyIterator.getPrevious() != null) {
            historyIterator = historyIterator.getPrevious();
            boardPanel.setBoard(historyIterator.getBoard());
            boardPanel.highlightSqr = false;
            boardPanel.repaint();
        }
    }

    /**
     * Goes forward to next game state, essentially "redoing" a move if possible
     */
    private void redo() {
        if(historyIterator.getNext() != null) {
            historyIterator = historyIterator.getNext();
            boardPanel.highlightSqr = false;
            boardPanel.setBoard(historyIterator.getBoard());
            boardPanel.repaint();
        }
    }

    /**
     * Method called when the solve button is pressed.
     * */
    private void solve() {
        Board test = historyIterator.getBoard().cloneBoard();
        if (test.isSolvable()) {
            test.setWasSolved();
            createHistory();
            historyIterator.setBoard(test);
            boardPanel.setBoard(historyIterator.getBoard());
            test.solveSudoku();
            boardPanel.repaint();
        }
        else {
            JOptionPane.showMessageDialog(null, "This boars cannot be solved.", "Can It Be Solved?", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method called when the can solve button is called.
     * */
    private void isSolvable() {
        Board test = historyIterator.getBoard().cloneBoard();
        if (!test.isSolved()) {
            if (test.isSolvable()) {
                JOptionPane.showMessageDialog(null, "This board CAN be solved.", "Can It Be Solved?", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "This board CANNOT be solved.", "Can It Be Solved?", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new SudokuDialog();
    }
}
