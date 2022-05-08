package com.attej.sudoku;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.attej.sudoku.backend.Board;
import com.attej.sudoku.backend.Cell;
import com.attej.sudoku.backend.CellGroupFragment;
import com.attej.sudoku.backend.CheckSolution;
import com.attej.sudoku.backend.GameRecord;
import com.attej.sudoku.backend.GenerateSudoku;
import com.attej.sudoku.backend.Stats;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class GameActivity extends AppCompatActivity implements CellGroupFragment.OnFragmentInteractionListener {
    private Cell clickedCell;
    private Cell previouslySelected;

    private ArrayList<Cell> wrongCells = new ArrayList<Cell>();

    private int clickedGroup;
    private int clickedCellId;
    private int difficulty = 1;
    private int givens = 0;
    private int mistakes;

    private int[] cellGroupFragments;

    private Board startBoard;
    private Board currentBoard;
    private Board solution;

    private boolean note = false;

    private TextView timer;
    private TextView mistakesText;

    private long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    private Handler handler;
    private int Seconds, Minutes, MilliSeconds ;

    private String FILENAME = "SavedTimes.dat";
    private Stats stats = new Stats();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        readData();

        difficulty = getIntent().getIntExtra("difficulty", 0);
        createBoard();
        createSudoku(difficulty);
        addGivens();
        setButtColors();

        mistakesText = findViewById(R.id.mistakesCounter);
        updateMistakeCounter();

        timer = findViewById(R.id.stopWatch);
        handler = new Handler() ;

        startTimer();
    }


    private void createSudoku(int difficulty) {
        givens = generateGivens(difficulty);
        solution = new Board(GenerateSudoku.getSolution());
        startBoard = new Board(GenerateSudoku.removeNumbers(solution.getGameCells(), 81-givens));
        currentBoard = new Board();
        currentBoard.copyValues(startBoard.getGameCells());

    }


    private void createBoard() {
        cellGroupFragments = new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3, R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        for (int i = 1; i < 10; i++) {
            CellGroupFragment thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[i-1]);
            thisCellGroupFragment.setGroupId(i);
        }
    }


    private void addGivens() {
        //Add all givens from the current board
        CellGroupFragment tempCellGroupFragment;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int column = j / 3;
                int row = i / 3;

                int fragmentNumber = (row * 3) + column;
                tempCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber]);
                int groupColumn = j % 3;
                int groupRow = i % 3;

                int groupPosition = (groupRow * 3) + groupColumn;
                int currentValue = currentBoard.getValue(i, j);

                if (currentValue != 0) {
                    tempCellGroupFragment.setValue(groupPosition, currentValue, true);
                }
            }
        }
    }


    private int generateGivens(int difficulty) {
        switch (difficulty) {
            case 0: {
                return ThreadLocalRandom.current().nextInt(30, 36);
            }
            case 1: {
                return ThreadLocalRandom.current().nextInt(25, 30);
            }
            case 2: {
                return ThreadLocalRandom.current().nextInt(17, 25);
            }
        }
        return 0;
    }


    private void setButtColors() {
        findViewById(R.id.del).setBackgroundColor(getResources().getColor(R.color.del));
        findViewById(R.id.buttNote).setBackgroundColor(getResources().getColor(R.color.note));
    }


    private void updateMistakeCounter() {
        mistakesText.setText("Mistakes: " + mistakes + "/3");
        if (mistakes >= 3)
            gameLost();
    }


    private void setCellNum(int num) {
        if (note && clickedCell.getNumber() == 0) {
            setNote(num);
            return;
        }

        int row = ((clickedGroup - 1) / 3) * 3 + (clickedCellId / 3);
        int column = ((clickedGroup - 1) % 3) * 3 + ((clickedCellId) % 3);

        int previousNumber = clickedCell.getNumber();

        clickedCell.setNumber(num, false);
        currentBoard.setValue(row, column, num);

        if ((!checkNumber()) && num != 0 && !CheckSolution.checkGrid(currentBoard)) {
            if (num != previousNumber) {
                mistakes++;
                updateMistakeCounter();
            }
            if (!wrongCells.contains(clickedCell))
                wrongCells.add(clickedCell);
        }
        if(checkNumber() && wrongCells.contains(clickedCell))
            wrongCells.remove(clickedCell);

        if (CheckSolution.checkGrid(currentBoard) && checkBoard())
            gameWon();

        removeMatchingNotes(num);
        refreshCellColors();
    }


    private boolean checkNumber() {
        int row = ((clickedGroup - 1) / 3) * 3 + (clickedCellId / 3);
        int column = ((clickedGroup - 1) % 3) * 3 + ((clickedCellId) % 3);
        if (clickedCell.getNumber() == solution.getGameCells()[row][column])
            return true;
        return false;
    }


    private boolean checkNumber(int row, int col) {
        if (currentBoard.getGameCells()[row][col] == solution.getGameCells()[row][col])
            return true;
        return false;
    }


    private boolean checkBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; i < 9; i++) {
                if (!checkNumber(i, j))
                    return false;
            }
        }
        return true;
    }


    public void setNote(int num) {
        clickedCell.setNote(num);
    }


    public void gameWon() {
        stopTimer();
        saveData();
        new AlertDialog.Builder(this)
                .setTitle("Game won!")
                .setMessage("Game won!  Time: " + String.format("%02d:%02d", Minutes, Seconds))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("New game?", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent();
                        intent.putExtra("Lost", 0);
                        setResult(1, intent);
                        finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }


    private void gameLost() {
        stopTimer();
        new AlertDialog.Builder(this)
                .setTitle("Game Lost!")
                .setMessage("Game Lost!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("New game?", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent();
                        intent.putExtra("Lost", 1);
                        setResult(1, intent);
                        finish();
                    }}).setCancelable(false).show();
    }


    @Override
    public void onFragmentInteraction(int groupId, int cellId, View view) {
        if(((Cell) view).isStartingCell())
            return;
        if (clickedCell != null) {
            previouslySelected = clickedCell;
        }
        clickedCell = (Cell) view;

        clickedGroup = groupId;
        clickedCellId = cellId;

        Log.i(TAG, "Clicked group " + groupId + ", cell " + cellId);

        if (!isStartPiece(groupId, cellId)) {
           selectCell();
        }
    }


    public void onNumButtClicked(View view) {
        Button clicked = (Button) view;
        int num = Integer.parseInt(clicked.getTag().toString());
        if (clickedCell != null) {
            setCellNum(num);
        }
    }


    public void onDelButtClicked(View view) {
        if (clickedCell != null && clickedCell.getNumber() != 0) {
            wrongCells.remove(clickedCell);
            setCellNum(0);
        }
        if (clickedCell != null && clickedCell.isNote()) {
            setCellNum(0);
        }
    }


    public void onNoteButtClicked(View view) {
        note = !note;
        Button butt = view.findViewById(R.id.buttNote);
        if (note)
            butt.setBackgroundColor(getResources().getColor(R.color.note_selected));
        else
            butt.setBackgroundColor(getResources().getColor(R.color.note));
    }


    public void onGoBackButtonClicked(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Quit")
                .setMessage("Do you really want to quit?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }


    private void selectCell() {
        if (clickedCell != null) {
            refreshCellColors();
        }
    }


    private void removeMatchingNotes(int value) {
        int row = (int)Math.floor(clickedCellId/3);
        int column = clickedCellId%3;
        int fragmentRow = (int)Math.floor((clickedGroup-1)/3);
        int fragmentColumn = (clickedGroup - 1)%3;
        int fragmentNumber = clickedGroup-1;
        CellGroupFragment subgrid = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber]);
        // Removes notes from the same subgrid
        for (int i = 0; i < 9; i++) {
            if (subgrid.getCell(i).isNote()) {
                subgrid.getCell(i).removeNote(value);
            }
        }
        removeNotesOnRow(value, row, fragmentNumber);
        removeNotesOnColumn(value, fragmentRow, fragmentColumn, column);

    }


    private void removeNotesOnRow(int value, int row, int fragmentNumber) {
        if ((fragmentNumber+1)%3 == 0) {
            for (int i = 0; i < 2; i++) {
                CellGroupFragment fragmentOnRow = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber - (i+1)]);
                for (int j = 0; j < 3; j++) {
                    int cellId = row * 3 + j;
                    if (fragmentOnRow.getCell(cellId).isNote()) {
                        fragmentOnRow.getCell(cellId).removeNote(value);
                    }
                }
            }
        }
        if ((fragmentNumber+1)%3 == 2) {
            CellGroupFragment fragmentOnRow = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber - 1]);
            for (int j = 0; j < 3; j++) {
                int cellId = row * 3 + j;
                if (fragmentOnRow.getCell(cellId).isNote()) {
                    fragmentOnRow.getCell(cellId).removeNote(value);
                }
            }
            fragmentOnRow = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber+1]);
            for (int j = 0; j < 3; j++) {
                int cellId = row * 3 + j;
                if (fragmentOnRow.getCell(cellId).isNote()) {
                    fragmentOnRow.getCell(cellId).removeNote(value);
                }
            }
        }
        if ((fragmentNumber+1)%3 == 1) {
            for (int i = 0; i < 2; i++) {
                CellGroupFragment fragmentOnRow = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber + (i+1)]);
                for (int j = 0; j < 3; j++) {
                    int cellId = row * 3 + j;
                    if (fragmentOnRow.getCell(cellId).isNote()) {
                        fragmentOnRow.getCell(cellId).removeNote(value);
                    }
                }
            }
        }
    }


    private void removeNotesOnColumn(int value, int fragmentRow, int fragmentColumn, int column) {
        if (fragmentRow == 0) {
            for (int i = 0; i < 2; i++) {
                int fragmentId = 3*(fragmentRow + i + 1) + fragmentColumn;
                CellGroupFragment frag = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentId]);
                for (int j = 0; j < 3; j++) {
                    int cellId = 3 * j + column;
                    if (frag.getCell(cellId).isNote())
                        frag.getCell(cellId).removeNote(value);
                }
            }
        }
        if (fragmentRow == 1) {
            int fragmentId = 3*(fragmentRow - 1) + fragmentColumn;
            CellGroupFragment frag = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentId]);
            for (int j = 0; j < 3; j++) {
                int cellId = 3 * j + column;
                if (frag.getCell(cellId).isNote())
                    frag.getCell(cellId).removeNote(value);
            }
            fragmentId = 3*(fragmentRow + 1) + fragmentColumn;
            frag = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentId]);
            for (int j = 0; j < 3; j++) {
                int cellId = 3 * j + column;
                if (frag.getCell(cellId).isNote())
                    frag.getCell(cellId).removeNote(value);
            }
        }
        if (fragmentRow == 2) {
            for (int i = 0; i < 2; i++) {
                int fragmentId = 3*(fragmentRow - (i + 1)) + fragmentColumn;
                CellGroupFragment frag = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentId]);
                for (int j = 0; j < 3; j++) {
                    int cellId = 3 * j + column;
                    if (frag.getCell(cellId).isNote())
                        frag.getCell(cellId).removeNote(value);
                }
            }
        }
    }


    private boolean isStartPiece(int group, int cell) {
        int row = ((group-1)/3)*3 + (cell/3);
        int column = ((group-1)%3)*3 + ((cell)%3);
        return startBoard.getValue(row, column) != 0;
    }


    private void refreshCellColors() {
        if (previouslySelected != null)
            previouslySelected.setBackground(getResources().getDrawable(R.drawable.table_border_cell));

        for (int i = 0; i < wrongCells.size(); i++) {
            wrongCells.get(i).setBackground(getResources().getDrawable(R.drawable.table_border_cell_wrong));
        }

        if (clickedCell != null) {
            if (wrongCells.contains(clickedCell))
                clickedCell.setBackground(getResources().getDrawable(R.drawable.table_border_cell_wrong_selected));
            else
                clickedCell.setBackground(getResources().getDrawable(R.drawable.table_border_cell_selected));
        }
    }


    private void saveData() {
        byte[] data = new byte[3];
        data[0] = (byte) Seconds;
        data[1] = (byte) '|';
        data[2] = (byte) difficulty;
        try {
            FileOutputStream fo = getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fo.write(data);
            fo.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


    private void readData() {
        File file = new File(FILENAME);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String[] record;

            while((line = br.readLine()) != null) {
                record = line.split("|");
                stats.addRecord(new GameRecord(Integer.parseInt(record[0]), Integer.parseInt(record[1])));
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startTimer() {
        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
    }


    private void stopTimer() {
        TimeBuff += MillisecondTime;
        handler.removeCallbacks(runnable);
    }


    private Runnable runnable = new Runnable() {

        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);

            timer.setText(String.format("%02d:%02d", Minutes, Seconds));
            handler.postDelayed(this, 0);
        }

    };
}
