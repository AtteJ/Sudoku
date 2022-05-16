package com.attej.sudoku;

import org.junit.Test;
import static org.junit.Assert.*;
import com.attej.sudoku.backend.GenerateSudoku;

public class GenerateSudokuTest {

    @Test
    public void getSolution_test() {
        int[][] grid = GenerateSudoku.getSolution();
        for (int[] ints : grid) {
            for (int anInt : ints) {
                assertNotEquals("Number should not be zero \n" + gridToString(grid), anInt, 0);
            }
        }
    }


    @Test
    public void generateEmptyGrid_test() {
        int[][] grid = GenerateSudoku.generateEmptyGrid();
        for (int[] ints : grid) {
            for (int anInt : ints) {
                assertEquals(anInt, 0);
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
        for (int[] ints : grid) {
            for (int anInt : ints) {
                sb.append(anInt).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
