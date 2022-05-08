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
            if (records.get(i).getDifficulty() == difficulty)
                sum += records.get(i).getTimeSeconds();
        }
        return sum / records.size();
    }


    public int getBestTime(int difficulty) {
        if (records.size() == 0)
            return 0;
        int best = Integer.MAX_VALUE;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getDifficulty() == difficulty) {
                if (records.get(i).getTimeSeconds() < best)
                    best = records.get(i).getTimeSeconds();
            }
        }
        if (best != Integer.MAX_VALUE)
            return best;
        else
            return 0;
    }


    public void saveStats() {
        System.out.println("Writing to file");
        for (int i = 0; i < records.size(); i++) {
            String data = records.get(i).getTimeSeconds() + "|" + records.get(i).getDifficulty();
            File file = new File(context.getFilesDir(), FILENAME);

            try {
                FileWriter writer = new FileWriter(file);
                writer.write("");
                writer.append(data + "\n");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void readStats() {
        try {
            FileInputStream fi = context.openFileInput(FILENAME);
            InputStreamReader inStream = new InputStreamReader(fi, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(inStream);
            String line;
            String[] record;

            while((line = br.readLine()) != null) {
                System.out.println("Stats:");
                System.out.println("Reading from: " + fi.getFD());
                System.out.println(line);
                if (!line.equals("")) {
                    record = line.split("\\|");
                    try {
                        int seconds = Integer.parseInt(record[0]);
                        int difficulty = Integer.parseInt(record[1]);
                        addRecord(new GameRecord(seconds, difficulty));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
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
