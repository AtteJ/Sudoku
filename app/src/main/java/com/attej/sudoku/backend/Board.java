package com.attej.sudoku.backend;

import androidx.annotation.NonNull;

public class Board {
    private final int[][] gameCells = new int[9][9];

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
            System.arraycopy(newGameCells[i], 0, gameCells[i], 0, newGameCells[i].length);
        }
    }


    public int getValue(int row, int column) {
        return gameCells[row][column];
    }


    public boolean isNumberLeft(int number) {
        int sum = 0;
        for (int[] gameCell : gameCells) {
            for (int i : gameCell) {
                if (i == number)
                    sum++;
            }
        }
        return sum < 9;
    }


    @NonNull
    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder();
        for (int[] gameCell : gameCells) {
            for (int j = 0; j < gameCell.length; j++) {
                if (j == 0) {
                    temp.append("\n");
                }

                int currentNumber = gameCell[j];
                if (currentNumber == 0) {
                    temp.append("-");
                } else {
                    temp.append(currentNumber);
                }

                if (j != (gameCell.length - 1)) {
                    temp.append(" ");
                }
            }
        }
        return temp.toString();
    }
}