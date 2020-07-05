package com.knesar.navigationdemoapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class CreatePattern extends AppCompatActivity {
    PatternLockView patternLockView;
    TextView status;
    Button clear_button, confirm_button;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_pattern);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        clear_button = findViewById(R.id.clear_button);
        confirm_button = findViewById(R.id.confirm_buttonn);
        status = findViewById(R.id.statusTV);
        status.setText("Draw your new pattern:");
        patternLockView();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        NavUtils.navigateUpFromSameTask(this);

    }

    public void patternLockView() {
        patternLockView = findViewById(R.id.patternLockView);
        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
                status.setText("Release finger when done");
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("password", PatternLockUtils.patternToString(patternLockView, pattern));
                editor.apply();

                status.setText("Pattern recorded");
                clear_button.setVisibility(View.VISIBLE);
                confirm_button.setVisibility(View.VISIBLE);
                clear_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();
                        status.setText("Draw your new pattern:");
                        patternLockView.clearPattern();
                    }
                });

                confirm_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavUtils.navigateUpFromSameTask(getParent());
                    }
                });
//                String password = preferences.getString("password", "0");
//                if (password.equals(PatternLockUtils.patternToString(patternLockView, pattern))) {
//                    Intent in = new Intent(getApplicationContext(), DrawPattern.class);
//                    startActivity(in);
//                    status.setText("Draw again");
//                }

            }

            @Override
            public void onCleared() {

            }
        });
    }
}
