package com.knesar.navigationdemoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CustomCategory extends AppCompatActivity {
    public static final int ADD_REQUEST_CODE = 1;
    public static final int EDIT_REQUEST_CODE = 2;
    public static final String SHARED_PREFS = "com.knesar.navigationdemoapp_SHARED_PREFS";

    CustomCategoryAdapter adapter;
    List<CustomCategoryModelClass> listItems;
    SharedPreferences preferences;
    RecyclerView recyclerView;
    String title;
    int position;
    TextView hintTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_category);

        title = getIntent().getStringExtra("title");
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hintTv = findViewById(R.id.hintTV_Custom);

        fabButton();

        loadList();
        recyclerView = findViewById(R.id.customCategoryRecyclerView);
        recyclerView.hasFixedSize();
        adapter = new CustomCategoryAdapter(listItems);
        recyclerView.setAdapter(adapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN
                | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                Collections.swap(listItems, fromPosition, toPosition);
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                CustomCategoryModelClass deletedItem = listItems.get(position);
                listItems.remove(position);
                adapter.notifyItemRemoved(position);
                saveList();
                textVisibility();

                Snackbar snackbar = Snackbar.make(recyclerView, "Item deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listItems.add(position, deletedItem);
                                adapter.notifyItemInserted(position);
                                saveList();
                            }
                        });
                snackbar.show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(CustomCategory.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(CustomCategory.this, R.color.red))
                        .addSwipeLeftLabel("Delete").setSwipeLeftLabelTextSize(1, 16)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(CustomCategory.this, R.color.red))
                        .addSwipeRightLabel("Delete").setSwipeRightLabelTextSize(1, 16)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new CustomCategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, CustomCategoryModelClass note) {
                position = pos;
                Intent intent = new Intent(CustomCategory.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTask());
                intent.putExtra(AddEditNoteActivity.EXTRA_COLOR, note.getColor());
                startActivityForResult(intent, EDIT_REQUEST_CODE);
            }
        });
    }

    public void textVisibility() {
        if (!listItems.isEmpty()) {
            hintTv.setVisibility(View.GONE);
        } else {
            hintTv.setVisibility(View.VISIBLE);
        }
    }

    private void loadList() {

        preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CustomCategoryModelClass>>() {
        }.getType();
        String json = preferences.getString(title, null);
        listItems = gson.fromJson(json, type);

        if (listItems == null) {
            listItems = new ArrayList<>();
        }
        textVisibility();

    }


    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);

    }

    private void fabButton() {
        FloatingActionButton fab = findViewById(R.id.fab_custom);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomCategory.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_category_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.delete) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Do you want to delete all notes?");
            b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    listItems.clear();
                    saveList();
                    adapter.notifyDataSetChanged();

                    //removing data from the SharedPreferences
                    preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove(title);
                    editor.apply();
                }
            });
            b.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK) {
            String task = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            int color = data.getIntExtra(AddEditNoteActivity.EXTRA_COLOR, -8469267);
            CustomCategoryModelClass modelClass = new CustomCategoryModelClass(task, color);
            adapter.addItem(modelClass);
            if (listItems.contains(modelClass)) {
                Snackbar snackbar = Snackbar.make(recyclerView, "Task Already exists!!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
            saveList();
            Snackbar snackbar = Snackbar.make(recyclerView, "Note saved", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, "Note can't be updated", Toast.LENGTH_SHORT).show();
            }

            String task = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            int color = data.getIntExtra(AddEditNoteActivity.EXTRA_COLOR, -8469267);
            CustomCategoryModelClass modelClass = new CustomCategoryModelClass(task, color);
            modelClass.setId(id);
//            listItems.remove(2);
            listItems.add(position, modelClass);
            adapter.notifyItemInserted(position);
            position++;
            listItems.remove(position);
            adapter.notifyItemRemoved(position);

            saveList();
            Snackbar snackbar = Snackbar.make(recyclerView, "Note updated", Snackbar.LENGTH_SHORT);
            snackbar.show();

        } else {
            Snackbar snackbar = Snackbar.make(recyclerView, "Note not saved", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }

    }

    private void saveList() {

        preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(listItems);
        editor.putString(title, json);
        editor.apply();
        textVisibility();

    }
}