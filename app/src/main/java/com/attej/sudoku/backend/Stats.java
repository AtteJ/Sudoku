package com.attej.sudoku.backend;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Stats {
    private Context context;
    private ArrayList<GameRecord> records = new ArrayList<GameRecord>();
    private String FILENAME = "game_stats.dat";

    private int experience = 0;

    public Stats(Context context) {
        this.context = context;
        readStats();
    }


    public void addRecord(GameRecord record) {
        records.add(record);
    }


    public int getAverage(int difficulty) {
        int sum = 0;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getDifficulty() == difficulty && records.get(i).getTimeSeconds() != -1)
                sum += records.get(i).getTimeSeconds();
        }
        if (getDifficultyPlayed(difficulty) == 0)
            return 0;
        return sum / getDifficultyWon(difficulty);
    }


    public int getBestTime(int difficulty) {
        if (records.size() == 0)
            return 0;
        int best = Integer.MAX_VALUE;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getDifficulty() == difficulty && records.get(i).getTimeSeconds() != -1) {
                if (records.get(i).getTimeSeconds() < best)
                    best = records.get(i).getTimeSeconds();
            }
        }
        if (best != Integer.MAX_VALUE)
            return best;
        else
            return 0;
    }


    public int getWinPercentage(int difficulty) {
        int losses = 0;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getDifficulty() == difficulty && records.get(i).getTimeSeconds() == -1)
                losses++;
        }
        int played = getDifficultyPlayed(difficulty);
        if (played > 0)
            return (int)Math.round(((played - losses) / (double)played) * 100);
        return 0;
    }


    public int getDifficultyWon(int difficulty) {
        int sum = 0;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getDifficulty() == difficulty && records.get(i).getTimeSeconds() != -1)
                sum++;
        }
        return sum;
    }


    public int getDifficultyPlayed(int difficulty) {
        int sum = 0;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getDifficulty() == difficulty)
                sum++;
        }
        return sum;
    }


    public void addExperience(int xp) {
        experience += xp;
    }


    public int getExperience() {
        return experience;
    }


    public void clearStats() {
        records.clear();

    }


    public void saveStats() {
        System.out.println("Writing to file " + context.getFilesDir() + "/" + FILENAME);
        File file = new File(context.getFilesDir(), FILENAME);
        try {
            FileWriter writer = new FileWriter(file);
            String data = experience + "";
            writer.write(data + "\n");
            for (int i = 0; i < records.size(); i++) {
                data = records.get(i).getTimeSeconds() + "|" + records.get(i).getDifficulty();
                System.out.println(data);
                writer.append(data + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void readStats() {
        try {
            FileInputStream fi = context.openFileInput(FILENAME);
            InputStreamReader inStream = new InputStreamReader(fi, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(inStream);
            String line;
            String[] record;

            System.out.println("Stats:");
            System.out.println("Reading from: " + context.getFilesDir() + "/" + FILENAME);

            while((line = br.readLine()) != null) {
                System.out.println(line);
                if (!line.equals("")) {
                    record = line.split("\\|");
                    if (record.length == 1) {
                        try {
                            experience = Integer.parseInt(record[0]);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            int seconds = Integer.parseInt(record[0]);
                            int difficulty = Integer.parseInt(record[1]);
                            addRecord(new GameRecord(seconds, difficulty));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
