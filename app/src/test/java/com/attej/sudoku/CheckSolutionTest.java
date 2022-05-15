package com.attej.sudoku;

import org.junit.Test;
import static org.junit.Assert.*;
import com.attej.sudoku.backend.GenerateSudoku;
import com.attej.sudoku.backend.CheckSolution;

public class CheckSolutionTest {

    @Test
    public void checkGrid_test() {
        int[][] grid = GenerateSudoku.generateEmptyGrid();
        assertFalse("Method should find all zeros", CheckSolution.checkGrid(grid));
        grid = GenerateSudoku.getSolution();
        assertTrue("Method should not find zeros in a filled grid", CheckSolution.checkGrid(grid));
    }

}
