package code.Sudoku;

interface HistoryEnabler {
    /* HistoryNode object accessible to all classes that implement this interface */
    HistoryNode history = new HistoryNode();

    /* Common methods that deal with HistoryNode objects */

    /**
     * Moves a HistoryNode pointer forward after a move or re-do is made
     */
    void movePointerForward();


    /**
     * Moves a HistoryNode pointer backward after an un-do is made
     */
    void movePointerBackward();

    /**
     * Resets a HistoryNode pointer
     */
    void resetPointer();
}
