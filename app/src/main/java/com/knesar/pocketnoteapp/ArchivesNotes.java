package com.knesar.navigationdemoapp;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ArchivesNotes extends Fragment {

    RecyclerView recyclerView;
    Archives_recyclerView adapter;
    NotesViewModel notesViewModel;
    TextView hintTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_archives_notes, container, false);

        hintTV = root.findViewById(R.id.hintTV_archives_fragment);
        buildRecyclerView(root);
        notesViewModel = new ViewModelProvider(this).get(NotesViewModel.class);
        observeData();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN
                | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                observeDataOnSwipe(fromPosition, toPosition);
                return false;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.green))
                        .addSwipeLeftLabel("Restore").setSwipeLeftLabelTextSize(1, 16)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.green))
                        .addSwipeRightLabel("Restore").setSwipeRightLabelTextSize(1, 16)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int adapterPosition = viewHolder.getAdapterPosition();
                notesViewModel.delete(adapter.getNoteAt(adapterPosition));
                observeData();

                String title = adapter.getNoteAt(adapterPosition).getItems();
                int color = adapter.getNoteAt(adapterPosition).getColor();
                String description = adapter.getNoteAt(adapterPosition).getDescription();
                if (title.equalsIgnoreCase("Quick Notes")) {
                    NotesEntity entity = new NotesEntity(description, color);
                    notesViewModel.insert(entity);
                } else {
                    ManageNotes notes = new ManageNotes(description, color);
                    notesViewModel.insert(notes);
                }

                Snackbar snackbar = Snackbar.make(recyclerView, "Restored", Snackbar.LENGTH_SHORT);
                snackbar.show();


            }
        }).attachToRecyclerView(recyclerView);

        return root;

    }

    private void observeData() {
        notesViewModel.getArchives().observe(getActivity(), new Observer<List<Archives>>() {
            @Override
            public void onChanged(List<Archives> archives) {
                adapter.submitList(archives);

                if (!archives.isEmpty()) {
                    hintTV.setVisibility(View.GONE);
                } else {
                    hintTV.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void observeDataOnSwipe(int fromPosition, int toPosition) {
        notesViewModel.getAllNotes().observe(getActivity(), new Observer<List<NotesEntity>>() {
            @Override
            public void onChanged(List<NotesEntity> notesEntities) {
                Collections.swap(notesEntities, fromPosition, toPosition);
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    private void buildRecyclerView(View root) {
        recyclerView = root.findViewById(R.id.archivesFragmentRecyclerView);
        recyclerView.hasFixedSize();
        adapter = new Archives_recyclerView();
        recyclerView.setAdapter(adapter);

    }

}
