package com.attej.sudoku.backend;

import java.util.ArrayList;

public class Stats {
    private ArrayList<GameRecord> records = new ArrayList<GameRecord>();

    public Stats() {

    }


    public void addRecord(GameRecord record) {
        records.add(record);
    }


    public int getAverage(int difficulty) {
        int sum = 0;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getDifficulty() == difficulty)
                sum += records.get(i).getTimeSeconds();
        }
        return sum / records.size();
    }


    public int getBestTime(int difficulty) {
        int best = Integer.MAX_VALUE;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getDifficulty() == difficulty) {
                if (records.get(i).getTimeSeconds() < best)
                    best = records.get(i).getTimeSeconds();
            }
        }
        return best;
    }
}
