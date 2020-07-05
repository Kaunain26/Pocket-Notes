package com.knesar.navigationdemoapp;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.app.Activity.RESULT_OK;

public class AllNotesFragment extends Fragment {
    public static final int ADD_NOTE_REQUEST_CODE = 1;
    public static final int EDIT_NOTE_REQUEST = 2;
    FloatingActionButton floatingActionButton;
    FragmentRecyclerAdapter adapter;
    NotesViewModel notesViewModel;
    RecyclerView recyclerView;
    TextView hintTV;
    List<NotesEntity> mNotes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_all_notes, container, false);

        fabButton(root);
        hintTV = root.findViewById(R.id.hintTV_fragment);
        recyclerView = root.findViewById(R.id.allFragmentRecyclerView);
        recyclerView.hasFixedSize();
        adapter = new FragmentRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        notesViewModel = new ViewModelProvider(requireActivity()).get(NotesViewModel.class);
        observeViewModel();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN
                | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            NotesEntity deleted = null;

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                observeViewModelOnSwipe(fromPosition, toPosition);

//                Collections.swap(mNotes, fromPosition, toPosition);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        notesViewModel.delete(adapter.getNoteAt(position));
                        deleted = adapter.getNoteAt(position);
                        observeViewModel();
                        Snackbar snackbar = Snackbar.make(recyclerView, "1 Item deleted", Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        notesViewModel.insert(deleted);
                                    }
                                });
                        snackbar.show();
                        break;

                    case ItemTouchHelper.RIGHT:
                        notesViewModel.delete(adapter.getNoteAt(position));
                        deleted = adapter.getNoteAt(position);

                        String title = adapter.getNoteAt(position).getTitle();
                        int color = adapter.getNoteAt(position).getColor();

                        Archives data = new Archives("Quick Notes", color, title);

                        notesViewModel.insertArchives(data);

                        Snackbar s = Snackbar.make(recyclerView, "Archived", Snackbar.LENGTH_SHORT);
                        s.show();

                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.red))
                        .addSwipeLeftLabel("Delete").setSwipeLeftLabelTextSize(1, 16)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.green))
                        .addSwipeRightLabel("Archive").setSwipeRightLabelTextSize(1, 16)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new FragmentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NotesEntity note) {

                Intent intent = new Intent(getContext(), AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_COLOR, note.getColor());
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });
        return root;
    }

    public void observeViewModel() {
        notesViewModel.getAllNotes().observe(getActivity(), new Observer<List<NotesEntity>>() {
            @Override
            public void onChanged(List<NotesEntity> notes) {
                adapter.submitList(notes);
                if (!notes.isEmpty()) {
                    hintTV.setVisibility(View.GONE);
                } else {
                    hintTV.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public void observeViewModelOnSwipe(int fromPosition, int toPosition) {

        notesViewModel.getAllNotes().observe(getActivity(), new Observer<List<NotesEntity>>() {
            @Override
            public void onChanged(List<NotesEntity> notesEntities) {
                Collections.swap(notesEntities, fromPosition, toPosition);
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    public void fabButton(View root) {
        floatingActionButton = root.findViewById(R.id.fab_all);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddEditNoteActivity.class);
                startActivityForResult(i, ADD_NOTE_REQUEST_CODE);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST_CODE && resultCode == RESULT_OK) {
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            int color = data.getIntExtra(AddEditNoteActivity.EXTRA_COLOR, -8469267);
            NotesEntity notesEntity = new NotesEntity(title, color);

            if (mNotes.contains(notesEntity)) {
                Snackbar snackbar = Snackbar.make(recyclerView, "Note is Already Present in list", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
            notesViewModel.insert(notesEntity);

            Snackbar snackbar = Snackbar.make(recyclerView, "Note saved", Snackbar.LENGTH_SHORT);
            snackbar.show();

        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(getContext(), "Note can't be updated", Toast.LENGTH_SHORT).show();
            }
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            int color = data.getIntExtra(AddEditNoteActivity.EXTRA_COLOR, -8469267);
            NotesEntity notesEntity = new NotesEntity(title, color);
            notesEntity.setId(id);

            if (mNotes.contains(notesEntity)) {
                Snackbar snackbar = Snackbar.make(recyclerView, "Task is Already Present in list", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }

            notesViewModel.update(notesEntity);
            Snackbar snackbar = Snackbar.make(recyclerView, "Note updated", Snackbar.LENGTH_SHORT);
            snackbar.show();

        } else {
            Snackbar snackbar = Snackbar.make(recyclerView, "Note isn't saved", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }

        if (!mNotes.isEmpty()) {
            hintTV.setVisibility(View.GONE);
        }
    }

}

