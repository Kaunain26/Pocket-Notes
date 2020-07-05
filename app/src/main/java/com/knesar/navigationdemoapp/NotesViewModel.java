package com.knesar.navigationdemoapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NotesViewModel extends AndroidViewModel {

    private Repository repository;
    private LiveData<List<NotesEntity>> notes;
    private LiveData<List<ManageNotes>> manageNotes;
    private LiveData<List<Archives>> archives;

    public NotesViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        notes = repository.getAllNotes();
        manageNotes = repository.getManageNotes();
        archives = repository.getArchives();
    }

    public void insert(NotesEntity notesEntity) {
        repository.insert(notesEntity);
    }

    public void update(NotesEntity notesEntity) {
        repository.update(notesEntity);
    }

    public void delete(NotesEntity notesEntity) {
        repository.delete(notesEntity);
    }

    public void deleteAllNotes() {
        repository.deleteAllNotes();
    }

    public LiveData<List<NotesEntity>> getAllNotes() {
        return notes;
    }


    //ManageNotes...................................................................
    public void insert(ManageNotes notes) {
        repository.insert(notes);
    }

    public void update(ManageNotes manageNotes) {
        repository.update(manageNotes);
    }

    public void delete(ManageNotes notesEntity) {
        repository.delete(notesEntity);
    }

    public LiveData<List<ManageNotes>> getManageNotes() {
        return manageNotes;
    }

    //Archives........................................................................

    public void insertArchives(Archives archives) {
        repository.insert(archives);
    }

    public void delete(Archives archives) {
        repository.delete(archives);
    }

    public LiveData<List<Archives>> getArchives() {
        return archives;
    }


}
