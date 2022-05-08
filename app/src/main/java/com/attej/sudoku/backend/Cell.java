package com.attej.sudoku.backend;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.attej.sudoku.R;

import java.util.ArrayList;

public class Cell extends TableLayout {
    TableLayout layout;
    private TextView number;
    private TextView[] notes = new TextView[9];
    private int num = 0;
    private boolean note = false;
    private boolean startingCell;

    public Cell(Context context, AttributeSet attrs) {
        super(context, attrs);

        createMainView();
    }


    private void createMainView() {
        if (layout != null)
            layout.removeAllViews();
        String service = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(service);
        layout = (TableLayout) li.inflate(R.layout.fragment_cell, this, true);

        number = layout.findViewById(R.id.fragment_cell_main);
        invalidate();
        requestLayout();
    }


    private void createNoteViews() {
        layout.removeAllViews();
        String service = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(service);
        layout = (TableLayout) li.inflate(R.layout.fragment_cell_hints, this, true);

        notes[0] = layout.findViewById(R.id.note_1);
        notes[1] = layout.findViewById(R.id.note_2);
        notes[2] = layout.findViewById(R.id.note_3);
        notes[3] = layout.findViewById(R.id.note_4);
        notes[4] = layout.findViewById(R.id.note_5);
        notes[5] = layout.findViewById(R.id.note_6);
        notes[6] = layout.findViewById(R.id.note_7);
        notes[7] = layout.findViewById(R.id.note_8);
        notes[8] = layout.findViewById(R.id.note_9);
        for (int i = 0; i < notes.length; i++) {
            notes[i].setTextSize(0);
            notes[i].setText("");
        }
        invalidate();
        requestLayout();
    }


    public void setNumber(int num, boolean startingCell) {
        this.startingCell = startingCell;
        if (note) {
            note = false;
        }

        if (num != 0) {
            createMainView();
            number.setTextSize(15);
            number.setText("" + num);
        }
        else {
            number.setTextSize(0);
            number.setText("");
        }
        this.num = num;

        if (startingCell) {
            number.setTextColor(Color.BLACK);
            number.setTypeface(null, Typeface.BOLD);
        }

        invalidate();
        requestLayout();
    }


    public void setNote(int value) {
        if (!note)
            createNoteViews();
        if (num == 0) {
            if (value != 0) {
                if (notes[value-1].getText().equals("")) {
                    note = true;
                    notes[value - 1].setTextSize(7);
                    notes[value - 1].setText("" + value);
                }
                else {
                    notes[value-1].setText("");
                }

            }
        }
    }


    public void removeNote(int value) {
        notes[value-1].setText("");
    }


    public void clearNotes() {
        for (int i = 0; i < notes.length; i++) {
            notes[i].setText("");
        }
    }


    public int getNumber() {
        return num;
    }


    public boolean isNote() {
        return note;
    }


    public boolean isStartingCell() {
        return startingCell;
    }
}
