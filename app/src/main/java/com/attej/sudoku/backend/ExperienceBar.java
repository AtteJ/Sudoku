package com.attej.sudoku.backend;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.attej.sudoku.R;

public class ExperienceBar extends Fragment {
    private TextView levelText;
    private TextView expText;
    private ProgressBar prog;
    private View view;
    private static Context context;


    public ExperienceBar() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.experience_bar, container, false);
        levelText = view.findViewById(R.id.textLevel);
        expText = view.findViewById(R.id.textExperience);
        prog = view.findViewById(R.id.experienceBar);
        return view;
    }


    public void setLevel(int level) {
        levelText.setText("Level: " + level);
    }


    public void setExperience(int experience) {
        prog.setMax(100);
        prog.setProgress(experience);

        // Drawable progressDrawable = prog.getProgressDrawable().mutate();
        // progressDrawable.setColorFilter(getResources().getColor(R.color.teal_700), PorterDuff.Mode.SRC_IN);
        // prog.setProgressDrawable(progressDrawable);
        prog.setBackgroundColor(Color.WHITE);
        prog.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple_700)));

        expText.setText("Experience: " + experience + "/100");
    }
}
