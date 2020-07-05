package com.knesar.navigationdemoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ManageNavListItems extends AppCompatActivity {
    public static int UNIQUE_ID = 1;
    public static final int ADD_REQUEST_CODE = 1;
    public static final int EDIT_NOTE_REQUEST = 2;
    ManageActivityRecyclerAdapter adapter;
    RecyclerView recyclerView;
    List<ManageNotes> notes = new ArrayList<>();
    NotesViewModel notesViewModel;
    int color;
    TextView hintTV_ManageActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_nav_list_items);
        getSupportActionBar().setTitle("Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hintTV_ManageActivity = findViewById(R.id.hintTV_ManageActivtiy);
        recyclerView = findViewById(R.id.manageRecyclerView);
        recyclerView.hasFixedSize();
        adapter = new ManageActivityRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        notesViewModel = new ViewModelProvider(this).get(NotesViewModel.class);
        listDataOnChanged();


        adapter.setOnItemCLickListener(new ManageActivityRecyclerAdapter.OnItemClickListener() {
            ManageNotes deletedItem = null;

            @Override
            public void onLongCLick(ManageNotes note) {
                Intent intent = new Intent(ManageNavListItems.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId1());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getItems());
                intent.putExtra(AddEditNoteActivity.EXTRA_COLOR, note.getColor());
                startActivityForResult(intent, ADD_REQUEST_CODE);
            }

            @Override
            public void onItemCLick(int pos) {
//                notesViewModel.delete(adapter.getNoteAt(pos));

                deletedItem = adapter.getNoteAt(pos);
                String deletedItemName = adapter.getNoteAt(pos).getItems();
                notesViewModel.delete(deletedItem);
                AlertDialog.Builder b = new AlertDialog.Builder(ManageNavListItems.this);

                View v = getLayoutInflater().inflate(R.layout.alert_custom_dialog, null);
                v.findViewById(R.id.alertTVCustom);
                v.findViewById(R.id.alertDescriptionTVCustom);
                b.setView(v);
                b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePrefs(deletedItemName);
                        dialog.dismiss();
                    }
                });
                b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notesViewModel.insert(deletedItem);
                        dialog.dismiss();
                    }
                });
                b.setCancelable(false);
                b.show();
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN
                | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            ManageNotes deletedItem = null;

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                notesViewModel.getManageNotes().observe(ManageNavListItems.this, new Observer<List<ManageNotes>>() {
                    @Override
                    public void onChanged(List<ManageNotes> manageNotes) {
                        Collections.swap(manageNotes, fromPosition, toPosition);
                        recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                    }
                });

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        deletedItem = adapter.getNoteAt(position);
                        notesViewModel.delete(deletedItem);

                        String items = adapter.getNoteAt(position).getItems();
                        int color = adapter.getNoteAt(position).getColor();
                        String description = getSupportActionBar().getTitle().toString();
                        Archives notes = new Archives(description, color, items);
                        notesViewModel.insertArchives(notes);

                        Snackbar s = Snackbar.make(recyclerView, "Archived", Snackbar.LENGTH_SHORT);
                        s.show();

                        break;
                    case ItemTouchHelper.RIGHT:

                        final ManageNotes deletedItem = adapter.getNoteAt(position);
                        notesViewModel.delete(deletedItem);
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageNavListItems.this);
                        builder.setTitle("Do you want to hide this Note?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notesViewModel.insert(deletedItem);
                                dialog.dismiss();
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();
                        break;
                }
                listDataOnChanged();

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(ManageNavListItems.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(ManageNavListItems.this, R.color.green))
                        .addSwipeLeftLabel("Archive").setSwipeLeftLabelTextSize(1, 16)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(ManageNavListItems.this, R.color.lightGreen))
                        .addSwipeRightLabel("Hide").setSwipeRightLabelTextSize(1, 16)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        }).attachToRecyclerView(recyclerView);


    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(ManageNavListItems.this, "Note name can't be updated", Toast.LENGTH_SHORT).show();
            }
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            int color = data.getIntExtra(AddEditNoteActivity.EXTRA_COLOR, -8469267);
            ManageNotes manageNotes = new ManageNotes(title, color);
            manageNotes.setId1(id);

            if (notes.contains(manageNotes)) {
                Snackbar snackbar = Snackbar.make(recyclerView, "Note name is Already Present in list", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }

            notesViewModel.update(manageNotes);
            Snackbar snackbar = Snackbar.make(recyclerView, "Note name updated", Snackbar.LENGTH_SHORT);
            snackbar.show();

        } else {
            Snackbar snackbar = Snackbar.make(recyclerView, "Note name isn't saved", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }

        if (!notes.isEmpty()) {
            hintTV_ManageActivity.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_menu_item, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_list) {
            AlertDialog.Builder b = new AlertDialog.Builder(ManageNavListItems.this);

            View v = getLayoutInflater().inflate(R.layout.alert_dialog, null);
            final EditText addNewItems = v.findViewById(R.id.Add_new_items);

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
                    Snackbar snackbar = Snackbar.make(recyclerView, "Please insert category name", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    ManageNotes manageNotes = new ManageNotes(s, color);
                    if (notes.contains(manageNotes)) {
                        Snackbar snackbar = Snackbar.make(recyclerView, "Already Present in list", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                    notesViewModel.insert(manageNotes);
                }

            });

            b.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            b.setCancelable(false);
            b.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void deletePrefs(String deletedItemName) {
        SharedPreferences preferences = getSharedPreferences(CustomCategory.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(deletedItemName);
        editor.apply();
    }

    public void listDataOnChanged() {
        notesViewModel.getManageNotes().observe(this, new Observer<List<ManageNotes>>() {
            @Override
            public void onChanged(List<ManageNotes> manageNotes) {
                adapter.submitList(manageNotes);
                if (!manageNotes.isEmpty()) {
                    hintTV_ManageActivity.setVisibility(View.GONE);
                } else {
                    hintTV_ManageActivity.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}