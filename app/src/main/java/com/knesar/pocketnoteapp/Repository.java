package com.knesar.navigationdemoapp;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

class Repository {

    private NotesDao notesDao;
    private LiveData<List<NotesEntity>> notes;
    private LiveData<List<ManageNotes>> manageNotes;
    private LiveData<List<Archives>> archives;

    public Repository(Application application) {
        NotesDatabase notesDatabase = NotesDatabase.getInstance(application);
        notesDao = notesDatabase.notesDao();
        notes = notesDao.getAllNotes();
        manageNotes = notesDao.getManageNotes();
        archives = notesDao.getArchives();
    }

    public void insert(NotesEntity notesEntity) {
        new InsertNotesAsyncTask(notesDao).execute(notesEntity);
    }

    public void update(NotesEntity notesEntity) {
        new UpdateNotesAsyncTask(notesDao).execute(notesEntity);
    }

    public void delete(NotesEntity notesEntity) {
        new DeleteNotesAsyncTask(notesDao).execute(notesEntity);

    }

    public void deleteAllNotes() {
        new DeleteAllNotesAsyncTask(notesDao).execute();
    }

    public LiveData<List<NotesEntity>> getAllNotes() {
        return notes;
    }


    public void insert(ManageNotes notesEntity) {
        new InsertManageNotesAsyncTask(notesDao).execute(notesEntity);
    }

    public void insert(Archives archives) {
        new InsertArchivesAsyncTask(notesDao).execute(archives);
    }


    public void delete(Archives archives) {
        new DeleteArchivesNotesAsyncTask(notesDao).execute(archives);

    }
    public void update(ManageNotes manageNotes) {
        new UpdateManageNotesAsyncTask(notesDao).execute(manageNotes);
    }

    public void delete(ManageNotes notesEntity) {
        new DeleteManageNotesAsyncTask(notesDao).execute(notesEntity);

    }

    public LiveData<List<ManageNotes>> getManageNotes() {
        return manageNotes;
    }

    public LiveData<List<Archives>> getArchives() {
        return archives;
    }


    private static class InsertNotesAsyncTask extends AsyncTask<NotesEntity, Void
            , Void> {
        private NotesDao notesDao;

        public InsertNotesAsyncTask(NotesDao notesDao) {
            this.notesDao = notesDao;
        }

        @Override
        protected Void doInBackground(NotesEntity... notes) {
            notesDao.insert(notes[0]);
            return null;
        }
    }


    private static class UpdateNotesAsyncTask extends AsyncTask<NotesEntity, Void
            , Void> {
        private NotesDao notesDao;

        public UpdateNotesAsyncTask(NotesDao notesDao) {
            this.notesDao = notesDao;
        }

        @Override
        protected Void doInBackground(NotesEntity... notes) {
            notesDao.update(notes[0]);
            return null;
        }
    }

    private static class DeleteNotesAsyncTask extends AsyncTask<NotesEntity, Void
            , Void> {
        private NotesDao notesDao;

        public DeleteNotesAsyncTask(NotesDao notesDao) {
            this.notesDao = notesDao;
        }

        @Override
        protected Void doInBackground(NotesEntity... notes) {
            notesDao.delete(notes[0]);
            return null;
        }
    }

    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void
            , Void> {
        private NotesDao notesDao;

        public DeleteAllNotesAsyncTask(NotesDao notesDao) {
            this.notesDao = notesDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            notesDao.deleteAllNotes();
            return null;
        }
    }

    // ManageNotes..........................................................................

    private static class InsertManageNotesAsyncTask extends AsyncTask<ManageNotes, Void
            , Void> {
        private NotesDao notesDao;

        public InsertManageNotesAsyncTask(NotesDao notesDao) {
            this.notesDao = notesDao;
        }

        @Override
        protected Void doInBackground(ManageNotes... notes) {
            notesDao.insert(notes[0]);
            return null;
        }
    }

    private static class UpdateManageNotesAsyncTask extends AsyncTask<ManageNotes, Void
            , Void> {
        private NotesDao notesDao;

        public UpdateManageNotesAsyncTask(NotesDao notesDao) {
            this.notesDao = notesDao;
        }

        @Override
        protected Void doInBackground(ManageNotes... notes) {
            notesDao.update(notes[0]);
            return null;
        }
    }

    private static class DeleteManageNotesAsyncTask extends AsyncTask<ManageNotes, Void
            , Void> {
        private NotesDao notesDao;

        public DeleteManageNotesAsyncTask(NotesDao notesDao) {
            this.notesDao = notesDao;
        }

        @Override
        protected Void doInBackground(ManageNotes... notes) {
            notesDao.delete(notes[0]);
            return null;
        }
    }

    private static class InsertArchivesAsyncTask extends AsyncTask<Archives, Void
            , Void> {
        private NotesDao notesDao;

        public InsertArchivesAsyncTask(NotesDao notesDao) {
            this.notesDao = notesDao;
        }

        @Override
        protected Void doInBackground(Archives... notes) {
            notesDao.insert(notes[0]);
            return null;
        }
    }

    private static class DeleteArchivesNotesAsyncTask extends AsyncTask<Archives, Void
            , Void> {
        private NotesDao notesDao;

        public DeleteArchivesNotesAsyncTask(NotesDao notesDao) {
            this.notesDao = notesDao;
        }

        @Override
        protected Void doInBackground(Archives... notes) {
            notesDao.delete(notes[0]);
            return null;
        }
    }

}

