package com.attej.sudoku;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.attej.sudoku.backend.Board;
import com.attej.sudoku.backend.Cell;
import com.attej.sudoku.backend.CellGroupFragment;
import com.attej.sudoku.backend.CheckSolution;
import com.attej.sudoku.backend.GameRecord;
import com.attej.sudoku.backend.GenerateSudoku;
import com.attej.sudoku.backend.Stats;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class GameActivity extends AppCompatActivity implements CellGroupFragment.OnFragmentInteractionListener {
    private Cell clickedCell;
    private Cell previouslySelected;

    private final ArrayList<Cell> wrongCells = new ArrayList<>();

    private FirebaseAnalytics mFireBaseAnalytics;

    private int clickedGroup;
    private int clickedCellId;
    private int difficulty = 1;
    private int mistakes = 0;
    private int hintsLeft = 1;

    private int[] cellGroupFragments;

    private Board startBoard;
    private Board currentBoard;
    private Board solution;

    private boolean note = false;

    private TextView timer;
    private TextView mistakesText;
    private TextView hintsText;

    private long millisecondTime, startTime, timeBuff, updateTime = 0L ;
    private Handler handler;
    private int minutes;

    private Stats stats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        stats = new Stats(getApplicationContext());

        difficulty = getIntent().getIntExtra("difficulty", 0);
        createBoard();
        createSudoku(difficulty);
        addGivens();
        setButtColors();

        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);

        mistakesText = findViewById(R.id.mistakesCounter);
        hintsText = findViewById(R.id.hintsCounter);
        updateCounters();

        timer = findViewById(R.id.stopWatch);
        handler = new Handler() ;

        startTimer();
    }


    private void createSudoku(int difficulty) {
        int givens = generateGivens(difficulty);
        solution = new Board(GenerateSudoku.getSolution());
        startBoard = new Board(GenerateSudoku.removeNumbers(solution.getGameCells(), 81- givens));
        currentBoard = new Board();
        currentBoard.copyValues(startBoard.getGameCells());

    }


    private void createBoard() {
        cellGroupFragments = new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3, R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        for (int i = 1; i < 10; i++) {
            CellGroupFragment thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[i-1]);
            if (thisCellGroupFragment != null)
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
                    if (tempCellGroupFragment != null)
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


    private void updateCounters() {
        mistakesText.setText(String.format(getString(R.string.mistakes), mistakes));
        hintsText.setText(String.format(getString(R.string.hints), hintsLeft));
        if (mistakes >= 3)
            gameLost();
    }


    private void setCellNum(int num) {
        if (note && clickedCell.getNumber() == 0) {
            setNote(num);
            return;
        }

        int row = getSelectedRow();
        int column = getSelectedColumn();

        int previousNumber = clickedCell.getNumber();

        clickedCell.setNumber(num, false);
        currentBoard.setValue(row, column, num);

        updateNumberButtons();

        checkAddedNumber(num, previousNumber);

        if (num != 0)
            removeMatchingNotes(num);
        refreshCellColors();
    }


    private void checkAddedNumber(int num, int previousNumber) {
        if ((!checkNumber()) && num != 0 && !CheckSolution.checkGrid(currentBoard)) {
            if (num != previousNumber) {
                mistakes++;
                updateCounters();
            }
            if (!wrongCells.contains(clickedCell))
                wrongCells.add(clickedCell);
        }
        if(checkNumber())
            wrongCells.remove(clickedCell);

        if (CheckSolution.checkGrid(currentBoard) && checkBoard())
            gameWon();
    }


    private boolean checkNumber() {
        int row = ((clickedGroup - 1) / 3) * 3 + (clickedCellId / 3);
        int column = ((clickedGroup - 1) % 3) * 3 + ((clickedCellId) % 3);
        return clickedCell.getNumber() == solution.getGameCells()[row][column];
    }


    private boolean checkNumber(int row, int col) {
        return currentBoard.getGameCells()[row][col] == solution.getGameCells()[row][col];
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


    private void updateNumberButtons() {
        for (int i = 1; i < 10; i++) {
            if (currentBoard.isNumberLeft(i) || note)
                addNumberButton(i);
            else
                removeNumberButton(i);
        }
        setNumButtColors();
    }


    private void removeNumberButton(int num) {
        Button[] numButts = getNumButts();

        numButts[num - 1].setVisibility(View.INVISIBLE);
        numButts[num - 1].setEnabled(false);
    }


    private void addNumberButton(int num) {
        Button[] numButts = getNumButts();

        numButts[num - 1].setVisibility(View.VISIBLE);
        numButts[num - 1].setEnabled(true);
    }


    private void setNumButtColors() {
        Button[] butts = getNumButts();
        for (Button butt : butts) {
            if (note)
                butt.setBackgroundColor(getResources().getColor(R.color.note_selected));
            else
                butt.setBackgroundColor(getResources().getColor(R.color.purple_500));
        }
    }


    public Button[] getNumButts() {
        Button[] numButts = new Button[9];
        numButts[0] = findViewById(R.id.butt1);
        numButts[1] = findViewById(R.id.butt2);
        numButts[2] = findViewById(R.id.butt3);
        numButts[3] = findViewById(R.id.butt4);
        numButts[4] = findViewById(R.id.butt5);
        numButts[5] = findViewById(R.id.butt6);
        numButts[6] = findViewById(R.id.butt7);
        numButts[7] = findViewById(R.id.butt8);
        numButts[8] = findViewById(R.id.butt9);

        return numButts;
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
        updateNumberButtons();
        if (note)
            butt.setBackgroundColor(getResources().getColor(R.color.note_selected));
        else
            butt.setBackgroundColor(getResources().getColor(R.color.note));
    }


    public void onHintButtonClicked(View view) {
        if (clickedCell == null || clickedCell.getNumber() != 0)
            Toast.makeText(getApplicationContext(), "First select unfilled cell", Toast.LENGTH_SHORT).show();
        else if (hintsLeft > 0) {
            clickedCell.setNumber(solution.getValue(getSelectedRow(), getSelectedColumn()), true);
            currentBoard.setValue(getSelectedRow(), getSelectedColumn(), clickedCell.getNumber());
            hintsLeft--;
            updateNumberButtons();
            removeMatchingNotes(clickedCell.getNumber());
            refreshCellColors();
            updateCounters();
        }
        else
            Toast.makeText(getApplicationContext(), "No Hints Left", Toast.LENGTH_SHORT).show();
    }


    public void onGoBackButtonClicked(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Quit")
                .setMessage("Do you really want to quit? This will be counted as a loss.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    GameRecord record = new GameRecord(-1, difficulty);
                    stats.addRecord(record);
                    stats.saveStats();

                    Intent intent = new Intent();
                    intent.putExtra("Lost", 1);
                    setResult(1, intent);

                    finish();
                })
                .setNegativeButton(android.R.string.no, null).show();
    }


    private void selectCell() {
        if (clickedCell != null) {
            refreshCellColors();
        }
    }


    private void removeMatchingNotes(int value) {
        int row = (int)Math.floor(clickedCellId / 3.0);
        int column = clickedCellId % 3;
        int fragmentRow = (int)Math.floor((clickedGroup - 1) / 3.0);
        int fragmentColumn = (clickedGroup - 1)%3;
        int fragmentNumber = clickedGroup-1;
        CellGroupFragment subgrid = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber]);
        // Removes notes from the same subgrid
        for (int i = 0; i < 9; i++) {
            if (subgrid != null)
                if (subgrid.getCell(i).isNote())
                    subgrid.getCell(i).removeNote(value);
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
                    if (fragmentOnRow != null)
                        if (fragmentOnRow.getCell(cellId).isNote())
                            fragmentOnRow.getCell(cellId).removeNote(value);
                }
            }
        }
        if ((fragmentNumber+1)%3 == 2) {
            CellGroupFragment fragmentOnRow = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber - 1]);
            for (int j = 0; j < 3; j++) {
                int cellId = row * 3 + j;
                if (fragmentOnRow != null)
                    if (fragmentOnRow.getCell(cellId).isNote())
                        fragmentOnRow.getCell(cellId).removeNote(value);
            }
            fragmentOnRow = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber+1]);
            for (int j = 0; j < 3; j++) {
                int cellId = row * 3 + j;
                if (fragmentOnRow != null)
                    if (fragmentOnRow.getCell(cellId).isNote())
                        fragmentOnRow.getCell(cellId).removeNote(value);
            }
        }
        if ((fragmentNumber+1)%3 == 1) {
            for (int i = 0; i < 2; i++) {
                CellGroupFragment fragmentOnRow = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber + (i+1)]);
                for (int j = 0; j < 3; j++) {
                    int cellId = row * 3 + j;
                    if (fragmentOnRow != null)
                        if (fragmentOnRow.getCell(cellId).isNote())
                            fragmentOnRow.getCell(cellId).removeNote(value);
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
                    if (frag != null)
                        if (frag.getCell(cellId).isNote())
                            frag.getCell(cellId).removeNote(value);
                }
            }
        }
        if (fragmentRow == 1) {
            int fragmentId = fragmentColumn;
            CellGroupFragment frag = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentId]);
            for (int j = 0; j < 3; j++) {
                int cellId = 3 * j + column;
                if (frag != null)
                    if (frag.getCell(cellId).isNote())
                        frag.getCell(cellId).removeNote(value);
            }
            fragmentId = 3*(fragmentRow + 1) + fragmentColumn;
            frag = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentId]);
            for (int j = 0; j < 3; j++) {
                int cellId = 3 * j + column;
                if (frag != null)
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
                    if (frag != null)
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
            previouslySelected.setBackground(ResourcesCompat.getDrawable(getApplicationContext().getResources(), R.drawable.table_border_cell, getApplicationContext().getTheme()));

        for (int i = 0; i < wrongCells.size(); i++) {
            wrongCells.get(i).setBackground(ResourcesCompat.getDrawable(getApplicationContext().getResources(), R.drawable.table_border_cell_wrong, getApplicationContext().getTheme()));
        }

        if (clickedCell != null) {
            if (wrongCells.contains(clickedCell))
                clickedCell.setBackground(ResourcesCompat.getDrawable(getApplicationContext().getResources(), R.drawable.table_border_cell_wrong_selected, getApplicationContext().getTheme()));
            else
                clickedCell.setBackground(ResourcesCompat.getDrawable(getApplicationContext().getResources(), R.drawable.table_border_cell_selected, getApplicationContext().getTheme()));
        }
    }


    private void startTimer() {
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
    }


    private void stopTimer() {
        timeBuff += millisecondTime;
        handler.removeCallbacks(runnable);
    }


    private int getSelectedRow() {
        return ((clickedGroup - 1) / 3) * 3 + (clickedCellId / 3);
    }


    private int getSelectedColumn() {
        return ((clickedGroup - 1) % 3) * 3 + ((clickedCellId) % 3);
    }


    public void gameWon() {
        stopTimer();
        int seconds = (int) (updateTime / 1000);

        minutes = seconds / 60;
        seconds = seconds % 60;
        String message = String.format(getString(R.string.game_won_message), minutes, seconds);
        if (stats.getBestTime(difficulty) > (int) (updateTime / 1000) && stats.getBestTime(difficulty) != 0)
            message += "New Best Time!";

        GameRecord record = new GameRecord((int)(updateTime / 1000), difficulty);
        if (difficulty == 0)
            stats.addExperience(5);
        if (difficulty == 1)
            stats.addExperience(10);
        if (difficulty == 2)
            stats.addExperience(15);
        stats.addRecord(record);
        stats.saveStats();

        new AlertDialog.Builder(this)
                .setTitle("Game won!")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("New game?", (dialog, whichButton) -> {
                    Intent intent = new Intent();
                    intent.putExtra("Lost", 0);
                    setResult(1, intent);
                    finish();
                })
                .setNegativeButton("Go Home?", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.putExtra("Lost", 1);
                    setResult(2, intent);
                    finish();
                }).setCancelable(false).show();
    }


    private void gameLost() {
        stopTimer();
        GameRecord record = new GameRecord(-1, difficulty);
        stats.addRecord(record);
        stats.saveStats();
        new AlertDialog.Builder(this)
                .setTitle("Game Lost!")
                .setMessage("Game Lost!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("New game?", (dialog, whichButton) -> {
                    Intent intent = new Intent();
                    intent.putExtra("Lost", 1);
                    setResult(1, intent);
                    finish();
                })
                .setNegativeButton("Go Home?", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.putExtra("Lost", 1);
                    setResult(2, intent);
                    finish();
                }).setCancelable(false).show();
    }


    private final Runnable runnable = new Runnable() {

        public void run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime;
            updateTime = timeBuff + millisecondTime;

            int seconds = (int) (updateTime / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;

            timer.setText(String.format(getString(R.string.timer), minutes, seconds));
            handler.postDelayed(this, 0);
        }

    };
}
