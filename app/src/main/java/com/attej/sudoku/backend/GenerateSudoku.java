package com.attej.sudoku.backend;

import static com.attej.sudoku.backend.CheckSolution.checkGrid;
import static com.attej.sudoku.backend.CheckSolution.checkSolution;
import static com.attej.sudoku.backend.CheckSolution.isValid;

import java.util.Random;

public class GenerateSudoku {


    public static int[][] getSolution() {
        int[][] grid = generateEmptyGrid();
        while (!checkGrid(grid))
        {
            grid = fillGrid(generateEmptyGrid());
        }
        return grid;
    }


    public static int[][] generateEmptyGrid() {
        return new int[][] { { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                             { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                             { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                             { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                             { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                             { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                             { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                             { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                             { 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
    }


    public static int[][] fillGrid(int[][] grid) {
        for (int i = 0; i < 81; i++)
        {
            int row = (int)(Math.floor(i / 9.0));
            int col = i % 9;
            if (grid[row][col] == 0)
            {
                int[] possibilities = shuffleNumbers();
                for (int j = 0; j < 9; j++)
                {
                    if (isValid(grid, row, col, possibilities[j]))
                    {
                        grid[row][col] = possibilities[j];
                    }
                }
            }
        }
        return grid;
    }


    private static int[] shuffleNumbers() {
        int[] numbers = new int[9];
        boolean sameNumber = false;

        for (int i = 0; i < 9; i++)
        {
            int rand = randNumber(1, 10);
            for (int j = 0; j < 9; j++)
            {
                if (numbers[j] == rand)
                {
                    sameNumber = true;
                    break;
                }
            }
            if (sameNumber)
            {
                i--;
            }
            else
            {
                numbers[i] = rand;
            }
            sameNumber = false;
        }
        return numbers;
    }


    public static int randNumber(int min, int max) {
        Random rnd = new Random();
        int rand = rnd.nextInt(max);
        while(rand < min)
            rand = rnd.nextInt(max);
        return rand;
    }


    public static int[][] removeNumbers(int[][] grid, int n) {
        int attempts = 0;
        int[][] copy = copyGrid(grid);
        for (int i = 0; i < n; i++)
        {
            int row = randNumber(0, 9);
            int col = randNumber(0, 9);
            while (copy[row][col] == 0)
            {
                row = randNumber(0, 9);
                col = randNumber(0, 9);
            }

            int backup = copy[row][col];
            copy[row][col] = 0;

            int[][] gridCopy = copyGrid(copy);

            if (!checkSolution(gridCopy))
            {
                copy[row][col] = backup;
                attempts++;
                i--;
                if (attempts > 10)
                    return copy;
            }
        }
        return copy;
    }


    public static int[][] copyGrid(int[][] grid) {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, 9);
        }
        return copy;
    }

}
