package com.attej.sudoku;

import org.junit.Test;
import static org.junit.Assert.*;
import com.attej.sudoku.backend.GenerateSudoku;

public class GenerateSudokuTest {

    @Test
    public void getSolution_test() {
        int[][] grid = GenerateSudoku.getSolution();
        for (int i = 0; i <  grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                assertNotEquals("Number should not be zero \n" + gridToString(grid), grid[i][j], 0);
            }
        }
    }


    @Test
    public void generateEmptyGrid_test() {
        int[][] grid = GenerateSudoku.generateEmptyGrid();
        for (int i = 0; i <  grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                assertEquals(grid[i][j], 0);
            }
        }
    }


    @Test
    public void randNumber_test() {
        for (int i = 0; i < 10; i++) {
            int rand = GenerateSudoku.randNumber(1, 10);
            assertTrue("Random number is out of range: " + rand, 1 <= rand && rand < 10);
        }
    }


    private String gridToString(int[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                sb.append(grid[i][j] + " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
