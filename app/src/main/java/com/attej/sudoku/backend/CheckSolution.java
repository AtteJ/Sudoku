package com.attej.sudoku.backend;

public class CheckSolution {
    private static int counter = 0;


    /**
     * Checks if the sudoku has a solution
     * @param grid sudoku to be checked
     * @return true if the sudoku has exactly one solution
     */
    public static boolean checkSolution(int[][] grid) {
        counter = 0;
        solveSudoku(grid);
        return counter == 1;
    }


    private static boolean solveSudoku(int[][] grid) {
        int row = 0;
        int col = 0;
        for (int i = 0; i < 81; i++)
        {
            row = (int)(Math.floor(i / 9.0));
            col = i % 9;
            if (grid[row][col] == 0)
            {
                for (int j = 1; j <= 9; j++)
                {
                    if (isValid(grid, row, col, j))
                    {
                        grid[row][col] = j;
                        if (checkGrid(grid))
                        {
                            counter++;
                            break;
                        }
                        else
                        {
                            if (solveSudoku(grid))
                                return true;
                        }
                    }
                }
                break;
            }

        }
        grid[row][col] = 0;
        return false;
    }


    /**
     * Checks if a number is valid in the given position
     * @param grid sudoku to be checked
     * @param row where the number is to be placed
     * @param col where the number is to be placed
     * @param num number to be placed
     * @return true if the number is in a valid position, otherwise false
     */
    public static boolean isValid(int[][] grid, int row, int col, int num) {
        for (int i = 0; i < 9; i++)
        {
            if (grid[row][i] == num && i != col)
            {
                return false;
            }
        }
        for (int i = 0; i < 9; i++)
        {
            if (grid[i][col] == num && i != row)
            {
                return false;
            }
        }
        int[][] subGrid = getSubGrid(grid, row, col);
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                if (subGrid[i][j] == num && !(i == row && j == col))
                {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Checks if all numbers are filled
     * @param board sudoku to be checked
     * @return true if completely filled, otherwise false
     */
    public static boolean checkGrid(Board board) {
        int[][] grid = board.getCurrentBoard();
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                if (grid[i][j] == 0)
                {
                    return false;
                }
            }
        }
        return true;
    }


    public static boolean checkGrid(int[][] grid) {
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                if (grid[i][j] == 0)
                {
                    return false;
                }
            }
        }
        return true;
    }


    private static int[][] getSubGrid(int[][] grid, int row, int col) {
        int[][] subGrid = new int[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };
        int rowStart = row - row % 3;
        int colStart = col - col % 3;
        for (int i = 0; i < 3; i++)
        {
            System.arraycopy(grid[i + rowStart], colStart, subGrid[i], 0, 3);
        }
        return subGrid;
    }
}
