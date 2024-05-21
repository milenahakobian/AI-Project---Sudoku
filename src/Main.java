import java.util.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Input size of the Sudoku puzzle
        System.out.print("Enter size of the Sudoku puzzle(subgrid size): ");
        int size = scanner.nextInt();

        // Input difficulty level of the Sudoku puzzle
        System.out.print("Enter difficulty level (e for easy, m for medium, h for hard): ");
        char difficulty = scanner.next().charAt(0);

        // Create a Sudoku board based on user input
        long startTime = System.nanoTime();
        SudokuBoard sudokuBoard = new SudokuBoard(size, difficulty);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // Convert nanoseconds to milliseconds

        // Print the original Sudoku puzzle
        System.out.println("Original Sudoku puzzle:");
        sudokuBoard.print();
        System.out.println();

        // Print the solved Sudoku puzzle
        System.out.println("Solved Sudoku puzzle:");
        startTime = System.nanoTime();
        SudokuSolver solver = new SudokuSolver();


        //var strategies - "next", "random", "mrv"
        //val strategies - "ordered", "random", "lcv"
        //inference types - "none", "forward_checking", "mac"
        SudokuBoard solvedBoard = solver.Backtracking_Search(sudokuBoard, "mrv", "lcv", "none");
        endTime = System.nanoTime();
        duration += (endTime - startTime) / 1000000; // Add solving time to total duration
        solvedBoard.print();
        System.out.println();

        System.out.println("Backtracks " + solver.getBacktrackCount());
        System.out.println("Number of value assignments " + solver.getValueAssignmentCount());
        // Report the time taken to solve the puzzle
        System.out.println("Time taken to solve the puzzle: " + duration + " milliseconds");

        scanner.close();
    }
}





        /*
        int[][] sudokuGrid = {
                {1, 0, 0, 0, 0, 0, 5, 7, 2},
                {0, 6, 0, 2, 1, 0, 0, 0, 9},
                {2, 0, 8, 5, 0, 9, 4, 6, 0},
                {8, 0, 2, 1, 4, 0, 6, 9, 3},
                {0, 0, 3, 0, 8, 5, 1, 2, 4},
                {0, 0, 1, 0, 0, 2, 7, 0, 0},
                {7, 2, 6, 4, 9, 3, 8, 1, 5},
                {3, 0, 0, 7, 0, 8, 0, 4, 6},
                {4, 0, 9, 6, 5, 1, 0, 3, 0}
        };
        int[][] sudokuGrid2 = {
                {0, 5, 3, 0, 0, 0, 0, 8, 0},
                {2, 0, 0, 0, 0, 0, 0, 0, 9},
                {1, 4, 0, 2, 6, 0, 0, 5, 0},
                {0, 0, 2, 0, 1, 9, 0, 0, 0},
                {0, 0, 0, 0, 2, 0, 0, 0, 0},
                {0, 0, 0, 5, 3, 0, 1, 0, 0},
                {0, 2, 0, 0, 9, 4, 0, 7, 8},
                {7, 0, 0, 0, 0, 0, 0, 0, 6},
                {0, 3, 0, 0, 0, 0, 4, 9, 0}
        };

    SudokuSolver s = new SudokuSolver();
    SudokuBoard b = new SudokuBoard(sudokuGrid2);
    //Testing with the matrices created above
        // s.Backtracking_Search(c, "random", "lcv", "mac").print();
    //Generating a puzzle of size 3, with difficulty level medium. 'e' is easy, 'm' is medium, 'h' is hard
    SudokuBoard c = new SudokuBoard(3, 'm');
    //printing the puzzle
    c.print();
    //solving and printing the puzzle
    s.Backtracking_Search(c, "random", "random", "mac").print();

    }
    */