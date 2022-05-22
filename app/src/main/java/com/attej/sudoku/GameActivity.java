package com.attej.sudoku;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.attej.sudoku.backend.AdManager;
import com.attej.sudoku.backend.Board;
import com.attej.sudoku.backend.Cell;
import com.attej.sudoku.backend.CellGroupFragment;
import com.attej.sudoku.backend.CheckSolution;
import com.attej.sudoku.backend.GameRecord;
import com.attej.sudoku.backend.GenerateSudoku;
import com.attej.sudoku.backend.Stats;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.PlayGames;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GameActivity extends AppCompatActivity implements CellGroupFragment.OnFragmentInteractionListener {
    private Cell clickedCell;
    private Cell previouslySelected;

    private final ArrayList<Cell> wrongCells = new ArrayList<>();

    private int clickedGroup;
    private int clickedCellId;
    private int difficulty = 1;
    private int mistakes = 0;
    private int hintsLeft = 1;

    private int[] cellGroupFragments;

    private Board board;

    private boolean note = false;

    private TextView timer;
    private TextView mistakesText;
    private TextView hintsText;

    private long millisecondTime, startTime, timeBuff, updateTime = 0L ;
    private int timeSeconds;
    private Handler handler;

    private Stats stats;

    private RewardedAd mRewardedAd;
    private final String TAG = "GameActivity";
    FirebaseAnalytics mFirebaseAnalytics;

    LeaderboardsClient leaderboardsClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setAnalytics();
        AdManager admanager = new AdManager();
        admanager.loadAd(this);

        leaderboardsClient = PlayGames.getLeaderboardsClient(this);
        stats = new Stats(getApplicationContext());

        int givens = getIntent().getIntExtra("givens", 0);
        difficulty = getIntent().getIntExtra("difficulty", 0);

        createBoard();
        createSudoku(givens);
        addGivens();
        setButtColors();
        refreshCellSizes();

        mistakesText = findViewById(R.id.mistakesCounter);
        hintsText = findViewById(R.id.hintsCounter);
        updateCounters();

        timer = findViewById(R.id.stopWatch);
        handler = new Handler() ;

        startTimer();
    }


    @Override
    protected void onPause() {
        Log.d(TAG, "Application going to background");
        super.onPause();
        stopTimer();
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "Application resuming from background");
        super.onResume();
        startTimer();
    }


    @Override
    public void onFragmentInteraction(int groupId, int cellId, View view) {
        int row = ((groupId-1)/3)*3 + (cellId/3);
        int column = ((groupId-1)%3)*3 + ((cellId)%3);
        if(board.getGiven(row, column) != 0)
            return;
        if (clickedCell != null) {
            previouslySelected = clickedCell;
        }
        clickedCell = (Cell) view;

        clickedGroup = groupId;
        clickedCellId = cellId;

        Log.i("TAG", "Clicked group " + groupId + ", cell " + cellId);

        selectCell();
    }


    private void setAnalytics() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }


    private void recordEvent(String event, String id, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, message);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "game_activity");
        mFirebaseAnalytics.logEvent(event, bundle);
    }


    private void createSudoku(int givens) {
        board = new Board();
        board.addSolution(GenerateSudoku.getSolution());
        board.addStartBoard(GenerateSudoku.removeNumbers(board.getSolution(), 81- givens));
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


    private void refreshCellSizes() {
        for (int cellGroupFragment : cellGroupFragments) {
            ((CellGroupFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(cellGroupFragment))).setCellSize();
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
                int currentValue = board.getGiven(i, j);

                if (currentValue != 0) {
                    if (tempCellGroupFragment != null)
                        tempCellGroupFragment.setValue(groupPosition, currentValue, true);
                }
            }
        }
    }


    private void setButtColors() {
        findViewById(R.id.del).setBackgroundColor(getResources().getColor(R.color.del));
        findViewById(R.id.buttNote).setBackgroundColor(getResources().getColor(R.color.note));
        findViewById(R.id.hint).setBackgroundColor(getResources().getColor(R.color.teal_700));

        setNumButtColors();
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
        else if (note && clickedCell.getNumber() != 0) {
            return;
        }

        int row = getSelectedRow();
        int column = getSelectedColumn();

        int previousNumber = clickedCell.getNumber();

        clickedCell.setNumber(num, false);
        board.setValue(row, column, num);

        updateNumberButtons();

        checkAddedNumber(num, previousNumber);

        if (num != 0)
            removeMatchingNotes(num);
        refreshCellColors();
    }


    private void checkAddedNumber(int num, int previousNumber) {
        if ((!board.checkAddedNumber(num, getSelectedRow(), getSelectedColumn())) && num != previousNumber) {
            mistakes++;
            updateCounters();
            if (!wrongCells.contains(clickedCell))
                wrongCells.add(clickedCell);
        }

        if(board.checkAddedNumber(num, getSelectedRow(), getSelectedColumn()))
            wrongCells.remove(clickedCell);

        if (CheckSolution.checkGrid(board) && board.checkBoard())
            gameWon();
    }


    public void setNote(int num) {
        if (clickedCell.getNumber() != 0)
            return;
        clickedCell.setNote(num);
        board.setNote(getSelectedRow(), getSelectedColumn(), num);
    }


    private void updateNumberButtons() {
        for (int i = 1; i < 10; i++) {
            if (board.isNumberLeft(i) || note)
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


    private void enableNumButts(boolean enabled) {
        Button[] butts = getNumButts();
        for (Button butt : butts) {
            butt.setEnabled(enabled);
        }
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
        Log.d(((Button) view).getText().toString(), "Number button clicked");
        Button clicked = (Button) view;
        int num = Integer.parseInt(clicked.getTag().toString());
        if (clickedCell != null) {
            setCellNum(num);
        }
    }


    public void onDelButtClicked(View view) {
        Log.d(((Button) view).getText().toString(), "Delete button clicked");
        if (clickedCell != null && clickedCell.getNumber() != 0) {
            wrongCells.remove(clickedCell);
            setCellNum(0);
        }
        if (clickedCell != null && clickedCell.isNote()) {
            setCellNum(0);
        }
    }


    public void onNoteButtClicked(View view) {
        Log.d(((Button) view).getText().toString(), "Note button clicked");
        note = !note;
        Button butt = view.findViewById(R.id.buttNote);
        updateNumberButtons();
        if (note)
            butt.setBackgroundColor(getResources().getColor(R.color.note_selected));
        else
            butt.setBackgroundColor(getResources().getColor(R.color.note));
    }


    public void onHintButtonClicked(View view) {
        Log.d(((Button) view).getText().toString(), "Hint button clicked");
        if (clickedCell == null || clickedCell.getNumber() != 0)
            Toast.makeText(getApplicationContext(), "First select unfilled cell", Toast.LENGTH_SHORT).show();
        else if (hintsLeft > 0) {
            clickedCell.setNumber(board.getCorrectValue(getSelectedRow(), getSelectedColumn()), true);
            board.setValue(getSelectedRow(), getSelectedColumn(), clickedCell.getNumber());
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
        Log.d(((Button) view).getText().toString(), "Go back clicked");
        new AlertDialog.Builder(this)
                .setTitle("Quit")
                .setMessage("Do you really want to quit? This will be counted as a loss.")
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    GameRecord record = new GameRecord(-1, difficulty);
                    stats.addRecord(record);
                    stats.saveStats();

                    finish();
                })
                .setNegativeButton(android.R.string.no, null).show();
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Quit")
                .setMessage("Do you really want to quit? This will be counted as a loss.")
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    GameRecord record = new GameRecord(-1, difficulty);
                    stats.addRecord(record);
                    stats.saveStats();

                    finish();
                })
                .setNegativeButton("Cancel", null).show();
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
        // timeSeconds += (int) (updateTime / 1000);

        submitTime();
        incrementAchievements();

        Intent intent = new Intent(this, GameWonActivity.class);
        intent.putExtra("time", timeSeconds);
        intent.putExtra("difficulty", difficulty);
        GameWonActivityResultLauncher.launch(intent);
    }


    private void submitTime() {
        String leaderboard = "";
        if (difficulty == 0)
            leaderboard = getString(R.string.leaderboard_easy);
        if (difficulty == 1)
            leaderboard = getString(R.string.leaderboard_normal);
        if (difficulty == 2)
            leaderboard = getString(R.string.leaderboard_hard);
        if (difficulty == 3)
            leaderboard = getString(R.string.leaderboard_extreme);
        leaderboardsClient.submitScore(leaderboard, updateTime);
    }


    private void incrementAchievements() {
        PlayGames.getAchievementsClient(this).unlock(getString(R.string.achievement_first_win));
        PlayGames.getAchievementsClient(this).increment(getString(R.string.achievement_win_10_games), 1);
        PlayGames.getAchievementsClient(this).increment(getString(R.string.achievement_win_50_games), 1);
        PlayGames.getAchievementsClient(this).increment(getString(R.string.achievement_win_100_games), 1);
        if (difficulty == 2) {
            PlayGames.getAchievementsClient(this).unlock(getString(R.string.achievement_win_a_hard_game));
            PlayGames.getAchievementsClient(this).increment(getString(R.string.achievement_win_10_hard_games), 1);
            PlayGames.getAchievementsClient(this).increment(getString(R.string.achievement_win_50_hard_games), 1);
            PlayGames.getAchievementsClient(this).increment(getString(R.string.achievement_win_100_hard_games), 1);
        }
        if (difficulty == 3) {
            PlayGames.getAchievementsClient(this).unlock(getString(R.string.achievement_win_an_extreme_game));
            PlayGames.getAchievementsClient(this).increment(getString(R.string.achievement_win_10_extreme_games), 1);
            PlayGames.getAchievementsClient(this).increment(getString(R.string.achievement_win_50_extreme_games), 1);
            PlayGames.getAchievementsClient(this).increment(getString(R.string.achievement_win_100_extreme_games), 1);
        }
        if (stats.getTotalPlaytime() >= 3600)
            PlayGames.getAchievementsClient(this).unlock(getString(R.string.achievement_total_playtime_of_one_hour));
        if (stats.getTotalPlaytime() >= 36000)
            PlayGames.getAchievementsClient(this).unlock(getString(R.string.achievement_total_playtime_of_10_hours));
        if (stats.getTotalPlaytime() >= 360000)
            PlayGames.getAchievementsClient(this).unlock(getString(R.string.achievement_total_playtime_of_100_hours));

    }


    private void gameLost() {
        stopTimer();
        enableNumButts(false);


        timeSeconds += (int) (updateTime / 1000);

        Intent intent = new Intent(this, GameLostActivity.class);

        GameLostActivityResultLauncher.launch(intent);
    }


    private void saveRecord(boolean won) {
        GameRecord record;
        if (won) {
            record = new GameRecord(timeSeconds, difficulty);
        }
        else {
            record = new GameRecord(-1, difficulty);
        }
        stats.addRecord(record);
        stats.saveStats();
    }


    private final Runnable runnable = new Runnable() {

        public void run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime;
            updateTime = timeBuff + millisecondTime;

            timeSeconds = (int) (updateTime / 1000);
            int minutes = timeSeconds / 60;
            int seconds = timeSeconds % 60;

            timer.setText(String.format(getString(R.string.timer), minutes, seconds));
            handler.postDelayed(this, 0);
        }

    };


    final ActivityResultLauncher<Intent> GameLostActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData().getIntExtra("Ad watched", 0) == 1) {
                    mistakes--;
                    startTimer();
                    enableNumButts(true);
                }
                if (result.getData().getIntExtra("Ad watched", 0) == 2) {
                    Toast.makeText(this, "Ad failed to load", Toast.LENGTH_LONG).show();
                    mistakes--;
                    startTimer();
                    enableNumButts(true);
                }
                if (result.getData().getIntExtra("Ad watched", 0) == 0) {
                    saveRecord(false);
                    stats.addPlaytime(timeSeconds);
                    PlayGames.getAchievementsClient(this).increment(getString(R.string.achievement_lose_10_games), 1);
                    Intent intent = new Intent();
                    intent.putExtra("Lost", 1);
                    setResult(1, intent);
                    finish();
                }
            });


    final ActivityResultLauncher<Intent> GameWonActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData().getIntExtra("Game won", 0) == 1) {
                    if (difficulty == 0)
                        stats.addExperience(5);
                    if (difficulty == 1)
                        stats.addExperience(10);
                    if (difficulty == 2)
                        stats.addExperience(15);
                    if (difficulty == 3)
                        stats.addExperience(25);

                    stats.addPlaytime(timeSeconds);
                    saveRecord(true);

                    submitTime();
                    incrementAchievements();

                    Intent intent = new Intent();
                    intent.putExtra("Go home", 0);
                    setResult(1, intent);
                }
                if (result.getData().getIntExtra("Go home", 0) == 1) {
                    if (difficulty == 0)
                        stats.addExperience(5);
                    if (difficulty == 1)
                        stats.addExperience(10);
                    if (difficulty == 2)
                        stats.addExperience(15);
                    if (difficulty == 3)
                        stats.addExperience(25);

                    stats.addPlaytime(timeSeconds);
                    saveRecord(true);

                    submitTime();
                    incrementAchievements();

                    Intent intent = new Intent();
                    intent.putExtra("Go home", 1);
                    setResult(1, intent);
                }
                finish();
            });
}
