package code;

import java.util.Random;
import java.util.concurrent.*;

/**
 * An abstraction of the Sudoku puzzle.
 */
class  Board implements Cloneable{

    /**
     * Size of this board (number of columns/rows).
     */
    private int size;
    private boolean wasSolved;
    private int[][] board;
    private boolean[][] valid;
    private boolean[][] mutable;

    /**
     * Create a new board of the given size.
     *
     * @param size This will be the size of the board.
     */
    Board(int size) {
        this.size = size;
        this.board = new int[size][size];
        this.valid = new boolean[size][size];
        this.mutable = new boolean[size][size];
    }

    private Board(int size, int[][] board, boolean[][] valid, boolean[][] mutable) {
        this.size = size;
        this.board = arrayClone(board);
        this.valid = arrayClone(valid);
        this.mutable = arrayClone(mutable);
        this.wasSolved = false;
    }

    /**
     * This method clones the current board.
     *
     * @return Returns a copy of a board object.
     */
    Board cloneBoard(){
        return new Board(size,this.board,this.valid,this.mutable);

    }

    /**
     * Implementation of clone class.
     * */
    protected Board clone() throws CloneNotSupportedException {
        return (Board) super.clone();

    }

    /**
     * Resolution to array clone.
     * */
    private int[][] arrayClone(int[][] original) {
        int [][] clone = new int[original.length][original.length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, clone[i], 0, original.length);
        }
        return clone;
    }

    /**
     * Resolution to array clone.
     * */
    private boolean[][] arrayClone(boolean[][] original) {
        boolean [][] clone = new boolean[original.length][original.length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, clone[i], 0, original.length);
        }
        return clone;
    }

    /**
     * Return the size of this board.
     *
     * @return Returns the size of the board
     */
    int size() {
        return size;
    }

    /**
     * Method that creates a solvable board by backtracking.
     * */
    void generateBoard() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        fillBoard();
        try {
            final Future f = service.submit(() -> {
                Board init = this.cloneBoard();
                while (!init.solveSudoku());
            });
            f.get(100, TimeUnit.MILLISECONDS);
        } catch (final TimeoutException e) {
            reset(this.size);
            generateBoard();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            service.shutdown();
        }
    }

    /**
     * This method generates a board preset with a given difficulty.
     */
    private void fillBoard() {
        Random rand = new Random();
        for (int placed = (size == 4) ? 4 : 26; placed > 0; placed--){
            int n = rand.nextInt(size);
            int i = rand.nextInt(size);
            int j = rand.nextInt(size);
            if (ruleChecker(i, j, n)) {
                board[i][j] = n;
                valid[i][j] = true;
                mutable[i][j] = true;
            }
        }
    }

    /**
     * Backtracking method that determines if a configuration is solvable.
     * */
    boolean isSolvable() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            final Future<Boolean> f = service.submit(() -> {
                Board init = this.cloneBoard();
                return init.solveSudoku();
            });
            f.get(100, TimeUnit.MILLISECONDS);
        } catch (final TimeoutException e) {
            return false;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            service.shutdown();
        }
        return true;
    }

    /**
     * This is a back-tracking method to fill a partially generated board.
     *
     * @return Determines if the board can be solvable or not.
     */
    boolean solveSudoku() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == 0 && isMutable(row,col)) {
                    for (int number = 1; number <= size; number++) {
                        if (ruleChecker(row, col, number)) {
                            board[row][col] = number;
                            valid[row][col] = true;
                            if (solveSudoku()) {
                                return true;
                            } else {
                                board[row][col] = 0;
                                valid[row][col] = false;
                            }
                        }
                    }
                return false;
                }
            }
        }
        return true;
    }

    /**
     * This method receives a coordinate in the matrix and checks if it is allowed.
     * To check if the insertion is allowed, it relies on checkVertical, checkHorizontal,
     * checkRange, and checkSubGrid.
     *
     * @param row This is the row to be checked.
     * @param col This is the column to be checked.
     * @param num This is the number to be compared against the matrix.
     * @return Returns whether the insertion was allowed or not.
     */
    boolean ruleChecker(int row, int col, int num) {
        return (checkHorizontal(row, num) && checkVertical(col, num)
                && checkSubGrid(row, col, num) && checkRange(num) && isMutable(row,col));
    }

    /**
     * This deletes the element at position row col by setting it back to 0.
     *
     * @param row This is the row at which the number would be inserted deleted.
     * @param col This is the column at which the number would be deleted.
     */
    void deleteElement(int row, int col) {
        board[row][col] = 0;
        valid[row][col] = false;
    }

    /**
     * This  method retrieves the element at position row col.
     *
     * @param row This is the row in the matrix.
     * @param col This is the column in the matrix.
     * @return Returns the element at the index.
     */
    int getElement(int row, int col) {
        return board[row][col];
    }

    /**
     * This stores num into the position row col and determines if the insertion is valid.
     *
     * @param row This is the row at which the number is inserted.
     * @param col This is the column at which the number is inserted.
     * @param num This is the number inserted into the matrix.
     */
    void setElement(int row, int col, int num) {
        valid[row][col] = ruleChecker(row, col, num);
        board[row][col] = num;
    }

    /**
     * This performs the horizontal rule check of sudoku.
     *
     * @param row This is the row which will be checked.
     * @param num This is the number to be compared to the rest of the row.
     * @return Returns if the number follows the rule.
     */
    private boolean checkHorizontal(int row, int num) {
        for (int i = 0; i < size; i++) {
            if (board[row][i] == num) {
                return false;
            }
        }
        return true;
    }

    /**
     * This performs the vertical rule check of sudoku.
     *
     * @param col This is the column which will be checked.
     * @param num This is the number to be compared to the rest of the column.
     * @return Returns if the number follows the rule.
     */
    private boolean checkVertical(int col, int num) {
        for (int i = 0; i < size; i++) {
            if (board[i][col] == num) {
                return false;
            }
        }
        return true;
    }

    /**
     * This performs the sub-grid rule check of sudoku.
     *
     * @param row This is the row which will be checked.
     * @param col This is the column which will be checked.
     * @param num This is the number to be compared to the rest of the sub-grid.
     * @return Returns if the number follows the rule.
     */
    private boolean checkSubGrid(int row, int col, int num) {
        /*the starting position is determined by modding the
         * row/col num by the sqrt of the size*/
        int rowS = (int) Math.sqrt(size) * (int) Math.floor(Math.abs(row / Math.sqrt(size)));
        int colS = (int) Math.sqrt(size) * (int) Math.floor(Math.abs(col / Math.sqrt(size)));
        int rowE = (int) (rowS + (Math.sqrt(size)));
        int colE = (int) (colS + (Math.sqrt(size)));
        for (int i = rowS; i < rowE; i++) {
            for (int j = colS; j < colE; j++) {
                if (board[i][j] == num) {
                    return false; //if a matching number is found
                }
            }
        }
        return true;
    }

    /**
     * This checks if the input number is in range
     *
     * @param num This is the number to be checked.
     * @return Returns if the number follows the rule.
     */
    private boolean checkRange(int num) {
        return num <= size && num > 0;
    }

    /**
     * This returns whether the value was a valid insertion.
     *
     * @param row This is the row to be checked.
     * @param col This is the col to be checked.
     * @return Returns the value stored at the index.
     */
    boolean isValid(int row, int col) {
        return valid[row][col];
    }

    /**
     * This checks if there are any 0's left in the matrix.
     *
     * @return Returns if there are no 0's left in the matrix.
     */
    boolean isSolved() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!valid[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method returns whether an element in the board is mutable.
     *
     * @return Boolean determining if it can be mutable.
     */
    boolean isMutable(int row, int col) {
        return !mutable[row][col];
    }

    /**
     * This method rests the values in the matrix back to 0;
     */
    void reset(int size) {
        this.size = size;
        this.board = new int[size][size];
        this.valid = new boolean[size][size];
        this.mutable = new boolean[size][size];

    }

    /**
     * wasSolved flag modifier.
     * */
    void setWasSolved(){
        this.wasSolved = true;
    }

    /**
     * wasSolver vale returner.
     *
     * @return The current value of wasSolved.
     * */
    boolean getWasSolved(){
        return this.wasSolved;
    }
}