package code.Sudoku;

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

    /** Sudoku history. */
    private HistoryNode history;

    /** Special panel to display a Sudoku history. */
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
        initHistory(4);
        boardPanel = new BoardPanel(history.getBoard(), this::boardClicked);
        configureMenu();
        configureUI();
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Callback to be invoked when a square of the history is clicked.
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
        if (history.isMutable(boardPanel.sy, boardPanel.sx)) {
            createHistory();
            if (number == 0) {
                history.deleteElement(boardPanel.sy, boardPanel.sx);
                showMessage("Number Deleted");
            }
            else {
                history.setElement(boardPanel.sy, boardPanel.sx, number);
                boardPanel.invalid = !history.isValid(boardPanel.sy, boardPanel.sx);
                showMessage(String.format("Inserted Number %d", number));
                solved();
            }
            boardPanel.setBoard(history.getBoard());
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
                    initHistory(4);
                    content.remove(numberButtons);
                    numberButtons = makeNumberButtons();
                    content.add(numberButtons);
                    break;
                case JOptionPane.NO_OPTION:
                    initHistory(9);
                    content.remove(numberButtons);
                    numberButtons = makeNumberButtons();
                    content.add(numberButtons);
                    break;
                case JOptionPane.CANCEL_OPTION:
                    System.exit(0);
                    break;
            }
            boardPanel.reset = true;
            boardPanel.setBoard(history.getBoard());
            history.generateBoard();
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
        JPanel history = new JPanel();
        history.setBorder(BorderFactory.createEmptyBorder(10, 16, 0, 16));
        history.setLayout(new GridLayout(1, 1));
        history.add(boardPanel);
        history.setBackground(BACKGROUND);
        add(history, BorderLayout.CENTER);
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
        int maxNumber = history.size() + 1;
        for (int i = 1; i <= maxNumber; i++) {
            int number = i % maxNumber;
            JButton button = new JButton(number == 0 ? "X" : String.valueOf(number));
            button.setFocusPainted(false);
            button.setMargin(new Insets(0, 2, 0, 2));
            button.addActionListener(e -> numberClicked(number));
            if (!history.ruleChecker(boardPanel.sy,boardPanel.sx,i) && number != 0) {
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
            history.setNext(new HistoryNode(history.getBoard().clone(), history));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        history = history.getNext();
    }

    /**
     * Method used to create a HistoryNode for undo and redo.
     * */
    private void initHistory(int i) {
        history = new HistoryNode(new Board(i));
        history.generateBoard();
    }

    /**
     * Goes back to previous game state, essentially "undoing" a move if possible
     */
    private void undo() {
        if(history.getPrevious() != null) {
            history = history.getPrevious();
            boardPanel.setBoard(history.getBoard());
            boardPanel.highlightSqr = false;
            boardPanel.repaint();
        }
    }

    /**
     * Goes forward to next game state, essentially "redoing" a move if possible
     */
    private void redo() {
        if(history.getNext() != null) {
            history = history.getNext();
            boardPanel.highlightSqr = false;
            boardPanel.setBoard(history.getBoard());
            boardPanel.repaint();
        }
    }

    /**
     * Method called when the solve button is pressed.
     * */
    private void solve() {
        if (history.getBoard().isSolvable()) {
            createHistory();
            history.setWasSolved();
            history.setBoard(history.getBoard());
            boardPanel.setBoard(history.getBoard());
            history.getBoard().solveSudoku();
            boardPanel.repaint();
        }
        else {
            JOptionPane.showMessageDialog(null, "This board cannot be solved.", "Can It Be Solved?", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method called when the can solve button is called.
     * */
    private void isSolvable() {
        if (!history.getBoard().isSolved()) {
            if (history.getBoard().isSolvable()) {
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
        if (history.getBoard().isSolved() && !history.getBoard().getWasSolved()) {
            boardPanel.win = true;
            boardPanel.playSound();
            Object[] options = {"New Game", "Exit"};
            int solved = JOptionPane.showOptionDialog(null, "You Won!",
                    "Congratulations", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[1]);
            if (solved == JOptionPane.YES_OPTION) {
                initHistory(history.getBoard().size());
                boardPanel = new BoardPanel(history.getBoard(), this::boardClicked);
                boardPanel.repaint();
            }
            else {
                System.exit(0);
            }
        }
    }
    public static void main(String[] args) {
        new SudokuDialog();
    }
}
