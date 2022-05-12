package com.attej.sudoku.backend;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.attej.sudoku.R;

public class CellGroupFragment extends Fragment {
    private int groupId;
    private OnFragmentInteractionListener mListener;
    private View view;
    int[] cells;

    public CellGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cell_group, container, false);

        //Set textview click listeners
        cells = new int[]{R.id.cell1, R.id.cell2, R.id.cell3, R.id.cell4,
                R.id.cell5, R.id.cell6, R.id.cell7, R.id.cell8, R.id.cell9};
        for (int cell1 : cells) {
            Cell cell = view.findViewById(cell1);
            cell.setOnClickListener(view -> mListener.onFragmentInteraction(groupId, Integer.parseInt(view.getTag().toString()), view));
        }
        return view;
    }


    private void setContentDescriptors() {
         for (int j = 0; j < getCells().length; j++) {
             view.findViewById(getCells()[j]).setContentDescription("Subgrid " + groupId + "Cell " + (j + 1));

        }
    }


    public void setGroupId(int groupId) {
        this.groupId = groupId;
        setContentDescriptors();
    }


    public void setRowHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        TableRow row = view.findViewById(R.id.row1);
        row.setMinimumHeight(width / 15);

        row = view.findViewById(R.id.row2);
        row.setMinimumHeight(width / 15);

        row = view.findViewById(R.id.row3);
        row.setMinimumHeight(width / 15);
    }


    public void setValue(int position, int value, boolean startingCell) {
        cells = new int[]{R.id.cell1, R.id.cell2, R.id.cell3, R.id.cell4,
                R.id.cell5, R.id.cell6, R.id.cell7, R.id.cell8, R.id.cell9};
        Cell currentCell = view.findViewById(cells[position]);
        currentCell.setNumber(value, startingCell);
        refreshContentDescriptions();
    }


    public void setCellSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        TableRow tableRow = view.findViewById(R.id.row1);
        tableRow.setMinimumWidth(displayMetrics.widthPixels / 15);
        tableRow.setMinimumHeight(displayMetrics.widthPixels / 15);

        tableRow = view.findViewById(R.id.row2);
        tableRow.setMinimumWidth(displayMetrics.widthPixels / 15);
        tableRow.setMinimumHeight(displayMetrics.widthPixels / 15);

        tableRow = view.findViewById(R.id.row3);
        tableRow.setMinimumWidth(displayMetrics.widthPixels / 15);
        tableRow.setMinimumHeight(displayMetrics.widthPixels / 15);
    }


    private void refreshContentDescriptions() {
        Cell cell;
        for (int i = 0; i < 9; i++) {
            cell = view.findViewById(cells[i]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                cell.setContentDescription(String.format(getString(R.string.cell_description), groupId, i, cell.getNumber()));
            }
        }
    }


    public Cell getCell(int i) {
        return view.findViewById(cells[i]);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int groupId, int cellId, View view);
    }
}