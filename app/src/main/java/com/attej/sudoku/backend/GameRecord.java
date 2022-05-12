package com.attej.sudoku.backend;

public class GameRecord {
    private final int timeSeconds;
    private final int difficulty;


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
