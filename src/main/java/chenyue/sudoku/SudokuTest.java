package chenyue.sudoku;

public class SudokuTest {

    public static void main(String[] args) {

        Sudoku sudoku = new Sudoku();

        sudoku.newSudokuGame(Sudoku.DifficultyLevel.HARD);

        if (sudoku.solveSudoku() == 0) {
            System.out.println("Keine Lösung gefunden!");
        } else {
            sudoku.printSudokuSolutions();
        }
        
    }
}
