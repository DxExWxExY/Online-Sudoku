package code.Sudoku;

import java.util.concurrent.*;

class Solver {

    private Board original;
    private Board clone;

    Solver(Board original) {
        this.original = original;
        this.clone = this.original.cloneBoard();
    }

    Board generateBoard() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        original.fillBoard();
        try {
            final Future f = service.submit(() -> {
                while (!clone.solveSudoku());
            });
            f.get(1000, TimeUnit.MILLISECONDS);
        } catch (final TimeoutException e) {
            original.reset(original.size());
            generateBoard();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            service.shutdown();
        }
        return original;
    }

    boolean isSolvable() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            final Future<Boolean> f = service.submit(() -> {
                while (!clone.solveSudoku());
                return true;
            });
            f.get(1000, TimeUnit.MILLISECONDS);
        } catch (final TimeoutException e) {
            return false;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            service.shutdown();
        }
        return false;
    }
}
