package com.attej.sudoku.backend;

import androidx.annotation.NonNull;

public class Board {
    private int[][] solution = new int[9][9];
    private int[][] startBoard = new int[9][9];
    private final int[][] currentBoard = new int[9][9];
    private final int[][][] notes = new int[9][9][9];

    public Board() {

    }


    public void setValue(int row, int column, int value) {
        currentBoard[row][column] = value;
    }


    public void setNote(int row, int column, int value) {
        notes[row][column][value-1] = 1;
    }


    public int[][] getSolution() { return solution; }


    public int getCorrectValue(int row, int column) { return solution[row][column]; }


    public int getGiven(int row, int column) {
        return startBoard[row][column];
    }


    public int[][] getCurrentBoard() { return currentBoard; }


    public boolean isNumberLeft(int number) {
        int sum = 0;
        for (int[] gameCell : currentBoard) {
            for (int i : gameCell) {
                if (i == number)
                    sum++;
            }
        }
        return sum < 9;
    }


    public Board addSolution(int[][] solution) {
        this.solution = solution;
        return this;
    }


    public void addStartBoard(int[][] startBoard) {
        this.startBoard = startBoard;

        for (int i = 0; i < startBoard.length; i++) {
            System.arraycopy(startBoard[i], 0, currentBoard[i], 0, currentBoard[i].length);
        }
    }


    public boolean checkAddedNumber(int num, int row, int column) {
        return (checkNumber(row, column)) || num == 0;
    }

    private boolean checkNumber(int row, int column) {
        return currentBoard[row][column] == solution[row][column];
    }


    public boolean checkBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; i < 9; i++) {
                if (solution[i][j] != currentBoard[i][j])
                    return false;
            }
        }
        return true;
    }


    @NonNull
    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder();
        for (int[] gameCell : currentBoard) {
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