package com.attej.sudoku.backend;

import java.io.FileOutputStream;

public class GameRecord {
    private int timeSeconds = 0;
    private int difficulty = 0;


    public GameRecord(int timeSeconds, int difficulty) {
        this.timeSeconds = timeSeconds;
        this.difficulty = difficulty;
    }


    public int getTimeSeconds() {
        return timeSeconds;
    }


    public int getDifficulty() {
        return difficulty;
    }

}
