package chenyue.sudoku;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class Sudoku {

    // define  Sudoku Matrix with 9 X 9
    private final int NORMS = 9; 

    // max to 10000 solutions
    private final int MAX_SOLUTIONS = 10000; 

    // Sodoku Matrix
    private final int[][] sudoku = new int[NORMS][NORMS];

    //swap
    private final int[][] sudoku_swap = new int[NORMS][NORMS]; 

    //solutions
    private final SudokuSolution solutions = new SudokuSolution(NORMS); 

    // candidate number
    private int[][][] ctbl; 

    //enumeration DifficultyLevel
    public enum DifficultyLevel {

        EASY(30), MEDIUM(50), HARD(70);

        private final int nRemove;

        DifficultyLevel(int nRemove) {
            this.nRemove = nRemove;
        }

        public int getDifficultyLevel() {
            return nRemove;
        }
    }

    public void newSudokuGame(DifficultyLevel level) {
        
        generateSudoku();

        int nRemove = level.getDifficultyLevel();
       
        Random random = new Random();

        for (int i = 0; i < nRemove; i++) {
            int x = random.nextInt(NORMS);
            int y = random.nextInt(NORMS);
            sudoku[x][y] = sudoku_swap[x][y] = 0;

        }

        System.out.println("New Sudoku game:");
        
        printSudoku(false);
        System.out.println("==================================");

    } 
    
     public void generateSudoku() {

        while (!generate()) {
        }
        SudokuSolution.copySudoku(sudoku, sudoku_swap, NORMS);
    } 


    public int solveSudoku() {

        solutions.clearSolutions();
        
        int count = 0;
        boolean unique;
        
        ctbl = new int[NORMS][NORMS][10];
        
        //init all candidate
        for (int col = 0; col < NORMS; col++) {
            
            for (int row = 0; row < NORMS; row++) {
                
                if (0 == sudoku[row][col]) {
                    getConstraint(col, row, ctbl[row][col], true);
                    count++;
                }
            }
        }

        while (true) {
            unique = false;
            for (int col = 0; col < NORMS; col++) {
                for (int row = 0; row < NORMS; row++) {
                    
                    //if just one condidate, set sudoku_swap[row][col] 
                    if (1 == constraintSize(ctbl[row][col])) {
                        sudoku_swap[row][col] = ctbl[row][col][0]; 
                        setCell(row, col, ctbl[row][col][0]);
                        count--;
                        unique = true;
                    }
                }
            }
            
            //just one Solution, return 
            if (0 == count) {
                solutions.addSolution(sudoku);
                break;
            }
            

            if (unique == false) {
                //use backtracking 
                return multipleSolutions(count);
            }

        }
        return 1;
    }

    //print Sudoku Solution
    public void printSudokuSolutions() {
        solutions.printSolutions();
    } 

    //generate
    private boolean generate() {

        int[] constraint = new int[10];
        resetSudoku();
        for (int col = 0; col < NORMS; col++) {
            for (int row = 0; row < NORMS; row++) {
                getConstraint(col, row, constraint, false);
                if (0 == constraint[0]) {
                    return false;
                }
                sudoku[row][col] = randomCandidate(constraint);
            }
        }
        return true;
    } 

    //backtracking solving Sudoku,count: how many number must be solving
    private int multipleSolutions(int count) {
         
        Deque<Integer> deque = new ArrayDeque<>();
        int results = 0; 
        int reach = 0; 
        int row = 0;
        int col = 0;
        int next = -1; 

        while (true) {

            next = (-1 == next) ? 0 : next;

            if (0 == sudoku[row][col]) {

                while (next >= constraintSize(ctbl[row][col])) {

                    if (deque.isEmpty() || results == MAX_SOLUTIONS) {
                        return results;
                    }
                    //backtracking
                    next = deque.pop() + 1;
                    do {
                        row--;
                        if (-1 == row) {
                            row = NORMS - 1;
                            col--;
                        }
                    } while (0 != sudoku_swap[row][col]);

                    sudoku[row][col] = 0;
                    //update all constraints
                    updateAllConstraints();
                    reach--;
                }

                setCell(row, col, ctbl[row][col][next]);
                reach++;
                deque.push(next);
                next = -1;
            }

            if (reach == count) {
                // get a sultion
                results++;
                solutions.addSolution(sudoku);
                
                //backtracking
                next = deque.pop() + 1;
                sudoku[row][col] = 0;
                reach--;
                //update all constraints
                updateAllConstraints();
                row--;
            }

            row++;

            if (NORMS == row) {
                row = 0;
                col++;

            }

        } 
    }

    // set sudoku[row][col] with val, update Constraints
    private void setCell(int row, int col, int val) {
        sudoku[row][col] = val;
        updateConstraints(row, col, val);
    }

    //get selection number for sudoku[row][col] and save in Constraints
    private void getConstraint(int col, int row, int[] constraint, 
            boolean allIter) {

        int[] cc = new int[10];
        int[] cr = new int[10];
        int[] cb = new int[10];

        // reset constraints
        for (int i = 1; i <= NORMS; i++) {
            cc[i] = cr[i] = cb[i] = i;
            constraint[i] = 0;
        }

        constraint[0] = 0;
        int end_row = allIter ? NORMS : row;
        int end_col = allIter ? NORMS : col;

        // get column constraint
        for (int r = 0; r < end_row; r++) {
            cc[sudoku[r][col]] = 0;
        }

        // get row constraint
        for (int c = 0; c < end_col; c++) {
            cr[sudoku[row][c]] = 0;
        }

        int br = (row / 3) * 3;
        int bc = (col / 3) * 3;

        // get block constraint
        for (int c = bc; c < bc + 3; c++) {

            for (int r = br; r < br + 3; r++) {
                cb[sudoku[r][c]] = 0;
            }

        }

        int idx = 0;
        int count;

        for (int i = 1; i <= NORMS; i++) {

            count = 0;

            for (int j = 1; j <= NORMS; j++) {

                count += (cc[j] == i ? 1 : 0);
                count += (cr[j] == i ? 1 : 0);
                count += (cb[j] == i ? 1 : 0);

                if (3 == count) {
                    break;
                }
            }

            if (3 == count) {
                constraint[idx] = i;
                idx++;
            }
        }
    }

    //updateAllConstraints
    private void updateAllConstraints() {

        for (int col = 0; col < NORMS; col++) {

            for (int row = 0; row < NORMS; row++) {

                if (0 == sudoku[row][col]) {
                    getConstraint(col, row, ctbl[row][col], true);
                }
            }
        }
    } 

    //update constraint for sudoku[row][col], remove val
    private void updateConstraints(int row, int col, int val) {

        int len;
        boolean shouldMove;

        // cut out the value from the constraints of the cells in the same row
        for (int _col = 0; _col < NORMS; _col++) {
            len = constraintSize(ctbl[row][_col]);
            shouldMove = false;

            for (int i = 0; i < len; i++) {
                if (false == shouldMove && ctbl[row][_col][i] == val) {
                    ctbl[row][_col][i] = 0;
                    shouldMove = true;
                }

                if (shouldMove) {
                    ctbl[row][_col][i] = ctbl[row][_col][i + 1];
                }
            }
        }

        // cut out the value from the constraints of the cells in the same column
        for (int _row = 0; _row < NORMS; _row++) {
            len = constraintSize(ctbl[_row][col]);
            shouldMove = false;
            for (int i = 0; i < len; i++) {
                if (false == shouldMove && ctbl[_row][col][i] == val) {
                    ctbl[_row][col][i] = 0;
                    shouldMove = true;
                }
                if (shouldMove) {
                    ctbl[_row][col][i] = ctbl[_row][col][i + 1];
                }
            }
        }


        // cut out the value from the constraints of the cells in the same block
        for (int i = row / 3 * 3; i < row / 3 * 3 + 3; i++) {
            for (int j = col / 3 * 3; j < col / 3 * 3 + 3; j++) {
                if (i != row && j != col) {
                    len = constraintSize(ctbl[i][j]);
                    shouldMove = false;
                    for (int s = 0; s < len; s++) {
                        if (false == shouldMove && ctbl[i][j][s] == val) {
                            ctbl[i][j][s] = 0;
                            shouldMove = true;
                        }
                        if (shouldMove) {
                            ctbl[i][j][s] = ctbl[i][j][s + 1];
                        }
                    }
                }
            }
        }
        
    }

    //get random constraint
    private int randomCandidate(int[] constraint) {
        int len = constraintSize(constraint);
        Random r = new Random();
        int result = r.nextInt(len);
        return constraint[result];
    }

    //set sudoku with sudoku_copy
    private void resetSudoku() {

        for (int row = 0; row < NORMS; row++) {
            System.arraycopy(sudoku_swap[row], 0, sudoku[row], 0, NORMS);
        }

    }

    //get size for constraint
    private int constraintSize(int[] constraint) {
        int len = 0;
        for (int i = 0; i < 10; i++) {
            if (0 == constraint[i]) {
                len = i;
                break;
            }
        }
        return len;
    }


     //Sudoku ausdruchen
    private void printSudoku(boolean sparse) {

        for (int row = 0; row < NORMS; row++) {
            for (int col = 0; col < NORMS; col++) {
                if (sudoku[row][col] > 0 && sparse) {
                    System.out.printf(" *%d", sudoku[row][col]);
                } else {
                    System.out.printf("%3d", sudoku[row][col]);
                }
            }
            System.out.println();
        }

    } 
}
