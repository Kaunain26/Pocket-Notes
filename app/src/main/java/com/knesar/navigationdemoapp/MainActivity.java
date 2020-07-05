package com.knesar.navigationdemoapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements NavRecyclerAdapter.OnItemClickListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    NavRecyclerAdapter adapter;
    NotesViewModel notesViewModel;
    int color;
    Dialog dialog;
    List<ManageNotes> notes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer,
                R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AllNotesFragment()).commit();
            navigationView.setCheckedItem(R.id.quick_task);
        }

        RecyclerView recyclerView = findViewById(R.id.nav_recyclerview);
        recyclerView.hasFixedSize();
        adapter = new NavRecyclerAdapter(this);
        recyclerView.setAdapter(adapter);

        notesViewModel = new ViewModelProvider(this).get(NotesViewModel.class);
        notesViewModel.getManageNotes().observe(this, new Observer<List<ManageNotes>>() {
            @Override
            public void onChanged(List<ManageNotes> manageNotes) {
                adapter.submitList(manageNotes);
            }
        });

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.quick_task:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AllNotesFragment())
                                .commit();
                        getSupportActionBar().setTitle("Quick Notes");
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.new_items:
                        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);

                        View v = getLayoutInflater().inflate(R.layout.alert_dialog, null);
                        final EditText addNewItems = v.findViewById(R.id.Add_new_items);
                        TextInputLayout textInputLayout = v.findViewById(R.id.textInputLayout);
                        v.findViewById(R.id.alertTVCategory);
                        v.findViewById(R.id.alertTVPickColor);
                        SpectrumPalette palette = v.findViewById(R.id.dialogColorPicker);
                        palette.setOnColorSelectedListener(
                                clr -> color = clr
                        );
                        palette.setSelectedColor(getResources().getColor(R.color.white));
                        color = getResources().getColor(R.color.white);

                        b.setView(v);

                        b.setPositiveButton("Add", (dialog, which) -> {
                            String s = addNewItems.getText().toString();

                            if (s.trim().isEmpty()) {
                                Snackbar snackbar = Snackbar.make(recyclerView, "Please insert note name", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } else {
                                ManageNotes manageNotes = new ManageNotes(s, color);
                                if (notes.contains(manageNotes)) {
                                    Snackbar snackbar = Snackbar.make(recyclerView, "Already Present in list", Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                }
                                notesViewModel.insert(manageNotes);

                                Snackbar snackbar = Snackbar.make(recyclerView, "Note name added", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }

                        });

                        b.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                        b.setCancelable(false);
                        b.show();
                        break;

                    case R.id.archives:
                        FingerPrintAuth();
                        Executor executor = ContextCompat.getMainExecutor(getApplicationContext());
                        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                                super.onAuthenticationError(errorCode, errString);
                            }

                            @Override
                            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ArchivesNotes())
                                        .addToBackStack(null).commit();
                                getSupportActionBar().setTitle("Archives Notes");
                                drawerLayout.closeDrawer(GravityCompat.START);
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                            }
                        });

                        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                                .setTitle("Verify Identity")
                                .setDescription("Use your fingerprint to access the Archives Notes")
                                .setNegativeButtonText("Cancel")
                                .build();

                        biometricPrompt.authenticate(promptInfo);
                        break;
                }
                return true;
            }
        });

    }


    public void FingerPrintAuth() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "The device don't have the fingerprint sensor \n use your pattern lock ", Toast.LENGTH_SHORT).show();
                patternLockAuth();
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "The biometric sensor is currently unavailable", Toast.LENGTH_SHORT).show();
                patternLockAuth();
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "Your device don't have any fingerprint saved. please check your settings!!", Toast.LENGTH_SHORT).show();
                patternLockAuth();
                break;
            case BiometricManager.BIOMETRIC_SUCCESS:
//                Toast.makeText(this, "Your device is eligible to use fingerprint", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void patternLockAuth() {
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String password = preferences.getString("password", "0");
        if (password.equals("0")) {
            Intent i = new Intent(getApplicationContext(), CreatePattern.class);
            startActivity(i);

        } else {
            Intent in = new Intent(getApplicationContext(), DrawPattern.class);
            startActivity(in);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ArchivesNotes())
                .addToBackStack(null).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.setting) {
            Intent intent = new Intent(MainActivity.this, ManageNavListItems.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        ManageNotes noteAt = adapter.getNoteAt(position);
        String title = noteAt.getItems();
        Intent in = new Intent(MainActivity.this, CustomCategory.class);
        in.putExtra("title", title);
        startActivity(in);
        drawerLayout.closeDrawer(GravityCompat.START);
    }
}