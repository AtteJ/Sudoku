package com.attej.sudoku.backend;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.attej.sudoku.R;

public class ExperienceBar extends Fragment {
    private TextView levelText;
    private TextView expText;
    private ProgressBar progressBar;


    public ExperienceBar() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.experience_bar, container, false);
        levelText = view.findViewById(R.id.textLevel);
        expText = view.findViewById(R.id.textExperience);
        progressBar = view.findViewById(R.id.experienceBar);
        return view;
    }


    public void setLevel(int level) {
        levelText.setText(String.format(getResources().getString(R.string.level), level));
    }


    public void setExperience(int experience) {
        progressBar.setMax(100);
        progressBar.setProgress(experience);

        progressBar.setBackgroundColor(Color.WHITE);
        progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple_700)));

        expText.setText(String.format(getResources().getString(R.string.experience), experience));
    }
}
