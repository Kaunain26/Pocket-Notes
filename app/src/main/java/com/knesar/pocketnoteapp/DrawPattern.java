package com.knesar.navigationdemoapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class DrawPattern extends AppCompatActivity {
    PatternLockView patternLockView;
    String password;
    TextView status;
    Button forgotPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_pattern);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        forgotPassword = findViewById(R.id.forgetPass);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder b = new AlertDialog.Builder(DrawPattern.this);
                b.setTitle("You have to delete the App data from setting to set a new password");
                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                b.show();
            }
        });

        status = findViewById(R.id.statusTV_Draw);
        status.setText("");
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        password = preferences.getString("password", "0");
        patternLockView();
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);

    }

    public void patternLockView() {
        patternLockView = findViewById(R.id.patternLockView_Draw);
        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                if (password.equals(PatternLockUtils.patternToString(patternLockView, pattern))) {

                    finish();
                } else {
                    patternLockView.setWrongStateColor(getResources().getColor(R.color.red));
//                    patternLockView.setCorrectStateColor(getResources().getColor(R.color.green));
                    status.setText("Wrong pattern:");
                    forgotPassword.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCleared() {

            }
        });
    }
}
