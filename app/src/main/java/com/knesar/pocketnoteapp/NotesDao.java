package com.knesar.navigationdemoapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NotesDao {

    @Insert
    void insert(NotesEntity notesEntity);

    @Update
    void update(NotesEntity notesEntity);

    @Delete
    void delete(NotesEntity notesEntity);

    @Query("DELETE FROM notes_table")
    void deleteAllNotes();

    @Query("SELECT * FROM notes_table")
    LiveData<List<NotesEntity>> getAllNotes();

    @Insert
    void insert(ManageNotes notes);

    @Insert
    void insert(Archives notes);

    @Update
    void update(ManageNotes manageNotes);


    @Delete
    void delete(ManageNotes notes);

    @Delete
    void delete(Archives notes);


    @Query("SELECT * FROM manageNotes_table")
    LiveData<List<ManageNotes>> getManageNotes();

    @Query("SELECT * FROM archives_table")
    LiveData<List<Archives>> getArchives();
}
