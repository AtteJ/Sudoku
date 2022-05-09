package com.attej.sudoku.backend;

import java.util.ArrayList;

public class Board {
    private int[][] gameCells = new int[9][9];

    public Board() {

    }


    public Board(int[][] board) {
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                setValue(i, j, board[i][j]);
            }
        }
    }


    public void setValue(int row, int column, int value) {
        gameCells[row][column] = value;
    }


    public int[][] getGameCells() {
        return gameCells;
    }


    public void copyValues(int[][] newGameCells) {
        for (int i = 0; i < newGameCells.length; i++) {
            for (int j = 0; j < newGameCells[i].length; j++) {
                gameCells[i][j] = newGameCells[i][j];
            }
        }
    }


    public int getValue(int row, int column) {
        return gameCells[row][column];
    }


    public boolean isNumberLeft(int number) {
        int sum = 0;
        for (int i = 0; i < gameCells.length; i++) {
            for (int j = 0; j < gameCells[i].length; j++) {
                if (gameCells[i][j] == number)
                    sum++;
            }
        }
        if (sum < 9)
            return true;
        return false;
    }


    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < gameCells.length; i++) {
            for (int j = 0; j < gameCells[i].length; j++) {
                if (j == 0) {
                    temp.append("\n");
                }

                int currentNumber = gameCells[i][j];
                if (currentNumber == 0) {
                    temp.append("-");
                } else {
                    temp.append(currentNumber);
                }

                if (j != (gameCells[i].length-1)) {
                    temp.append(" ");
                }
            }
        }
        return temp.toString();
    }
}