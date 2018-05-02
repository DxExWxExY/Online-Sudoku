package code.Sudoku;

import java.util.concurrent.*;

class Solver {

    private Board original;
    private Board clone;

    Solver(Board original) {
        this.original = original;
    }

    void generateBoard() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        original.fillBoard();
        try {
            final Future f = service.submit(() -> {
                clone = this.original.cloneBoard();
                while (true) {
                    if (clone.solveSudoku()) break;
                }
            });
            f.get(1000, TimeUnit.MILLISECONDS);
        } catch (final TimeoutException e) {
            original.reset(original.getSize());
            generateBoard();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            service.shutdown();
        }
    }

    boolean isSolvable() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            final Future<Boolean> f = service.submit(() -> {
                this.clone = this.original.cloneBoard();
                return clone.solveSudoku();
            });
            f.get(1000, TimeUnit.MILLISECONDS);
        } catch (final TimeoutException e) {
            return false;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            service.shutdown();
        }
        return true;
    }

    boolean solveSudoku() {
        for (int row = 0; row < original.getSize(); row++) {
            for (int col = 0; col < original.getSize(); col++) {
                if (original.getElement(row, col) == 0 && original.isMutable(row, col)) {
                    for (int number = 1; number <= original.getSize(); number++) {
                        if (original.ruleChecker(row, col, number)) {
                            original.setElement(row, col, number);
                            if (solveSudoku()) {
                                return true;
                            } else {
                                original.deleteElement(row, col);
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }
}


