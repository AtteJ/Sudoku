package com.attej.sudoku.backend;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
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
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }


    public int getGroupId() {
        return groupId;
    }


    public void setValue(int position, int value, boolean startingCell) {
        cells = new int[]{R.id.cell1, R.id.cell2, R.id.cell3, R.id.cell4,
                R.id.cell5, R.id.cell6, R.id.cell7, R.id.cell8, R.id.cell9};
        Cell currentCell = view.findViewById(cells[position]);
        currentCell.setNumber(value, startingCell);
        refreshContentDescriptions();
    }


    private void refreshContentDescriptions() {
        Cell cell;
        for (int i = 0; i < 9; i++) {
            cell = view.findViewById(cells[i]);
            cell.setContentDescription(String.format(getString(R.string.cell_description), groupId, i, cell.getNumber()));
        }
    }


    public Cell getCell(int i) {
        return view.findViewById(cells[i]);
    }


    public boolean checkGroupCorrect() {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int cell : cells) {
            TextView textView = view.findViewById(cell);
            int number = Integer.parseInt(textView.getText().toString());
            if (numbers.contains(number)) {
                return false;
            } else {
                numbers.add(number);
            }
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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