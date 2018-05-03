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

    HistoryNode() {
        board = new Board(4);
        this.next = null;
        this.previous = null;
    }

    public void newGame(int size) {
        board = new Board(size);
        this.next = null;
        this.previous = null;
    }

    public void newNode() {
        HistoryNode temp = this;
        while(temp.getNext() != null) {
            temp = temp.getNext();
        }

        temp.setNext(new HistoryNode(this.getBoard().cloneBoard(), temp));
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
     * Board Instance bypass
     * @see Board
     * */
    boolean isValid(int sy, int sx) {
        return board.isValid(sy,sx);
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

    public boolean isMutable(int sy, int sx) {
        return board.isMutable(sy,sx);
    }

    public void deleteElement(int sy, int sx) {
        board.deleteElement(sy,sx);
    }

    public void setElement(int sy, int sx, int number) {
        board.setElement(sy,sx,number);
    }

    public int getSize() {
        return board.getSize();
    }

    public boolean ruleChecker(int sy, int sx, int i) {
        return board.ruleChecker(sy,sx,i);
    }
}