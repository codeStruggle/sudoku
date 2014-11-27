package chenyue.sudoku;

import java.util.ArrayList;
import java.util.List;

public class SudokuSolution {

    private final List<int[][]> Solutions;

    private final int NORMS;

    SudokuSolution(int NORMS) {
        Solutions = new ArrayList<>();
        this.NORMS = NORMS;
    }

    //add solution
    public void addSolution(int[][] solution) {
        final int[][] sudokuSolution = new int[NORMS][NORMS];
        copySudoku(solution, sudokuSolution, NORMS);
        Solutions.add(sudokuSolution);
    }

    //delete all solutions
    public void clearSolutions() {
        Solutions.clear();
    }

    //print all solutions
    public void printSolutions() {

        int[][] solution;

        for (int i = 0; i < Solutions.size(); i++) {

            solution = Solutions.get(i);

            System.out.println("Solution: " + (i + 1));

            for (int row = 0; row < NORMS; row++) {

                for (int col = 0; col < NORMS; col++) {
                    System.out.printf("%3d", solution[row][col]);
                }

                System.out.println();
            }
        }
    }

    //copy Sudoku
    public static void copySudoku(int[][] src, int[][] dest, int NORMS) {
        for (int row = 0; row < NORMS; row++) {
            System.arraycopy(src[row], 0, dest[row], 0, NORMS);
        }
    }

}
