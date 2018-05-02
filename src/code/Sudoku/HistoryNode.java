package code.Sudoku;

/** Class used to implement undo's and redo's.
 * */
public class HistoryNode {
    private HistoryNode next;
    private HistoryNode previous;
    private Board board;

    /** Constructor that creates a non-head node by cloning the current board, sets the previous node, and sets previous'
     * node "next" pointer to this node.
     * @param board The instance to be cloned.
     * */
    HistoryNode(Board board, HistoryNode prevState) {
        this.board = board.cloneBoard();
        this.next = null;
        this.previous = prevState;
        prevState.next = this;

    }

    /**
     * Constructor that creates a head node by cloning the current board (empty board).
     * @param board The initial node at the beginning of the game.
     */
    HistoryNode(Board board) {
        this.board = board.cloneBoard();
        this.next = null;
        this.previous = null;
    }

    /**
     * Return previous instance of a node.
     *
     * @return Previous instance.
     * */
    HistoryNode getPrevious() {
        return previous;
    }

    /**
     * Returns the next instance of a node.
     *
     * @return Next instance.
     * */
    HistoryNode getNext() {
        return next;
    }

    /**
     * Return the board in the current node.
     *
     * @return Current Board instance.
     * */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Sets the next pointer of an instance.
     * */
    void setNext(HistoryNode next) {
        this.next = next;
    }

    /**
     * Sets a Board instance in the current node.
     * */
    void setBoard(Board test) {
        this.board = test;
    }

    /**
     * Board Instance bypass
     * @see Board
     * */
    boolean isMutable(int sy, int sx) {
        return board.isMutable(sy, sx);
    }

    /**
     * Board Instance bypass
     * @see Board
     * */
    void deleteElement(int sy, int sx) {
        board.deleteElement(sy, sx);
    }

    /**
     * Board Instance bypass
     * @see Board
     * */
    void setElement(int sy, int sx, int number) {
        board.setElement(sy, sx, number);
    }

    /**
     * Board Instance bypass
     * @see Board
     * */
    boolean isValid(int sy, int sx) {
        return board.isValid(sy,sx);
    }

    /**
     * Board Instance bypass
     * @see Board
     * */
    boolean ruleChecker(int x, int y ,int num) {
        return board.ruleChecker(x,y,num);
    }

    /**
     * Board Instance bypass
     * @see Board
     * */
    void reset(int i) {
        board.reset(i);
    }

    /**
     * Board Instance bypass
     * @see Board
     * */
    public void generateBoard() {
        board.generateBoard();
    }

    /**
     * Board Instance bypass
     * @see Board
     * */
    int size() {
        return board.size();
    }

    void setWasSolved() {
        board.setWasSolved();
    }

    public String getData(int i, int j) {
        return board.getData(i, j);
    }

    public void setData(String data) {
        board.setData(data);
    }
}