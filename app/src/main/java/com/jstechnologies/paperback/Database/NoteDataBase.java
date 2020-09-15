package com.jstechnologies.paperback.Database;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.room.Database;
import androidx.room.Insert;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jstechnologies.paperback.Dao.NoteDAO;
import com.jstechnologies.paperback.Models.Note;

import java.util.ArrayList;
import java.util.List;

@Database(entities = Note.class,version = 1,exportSchema = false)
public abstract class NoteDataBase extends RoomDatabase {
    private static NoteDataBase noteDataBase;
    static Context context;
    public static synchronized NoteDataBase getDatabase(Context _context)
    {
        context=_context;
        if(noteDataBase==null)
        {
            noteDataBase= Room.databaseBuilder(context,
                    NoteDataBase.class,
                    "notes_db")
                    .build();
        }
        return noteDataBase;
    }
    public abstract NoteDAO noteDAO();

    public void SaveNote(final Note note, final DataBaseOperationsListener mlistener)
    {

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mlistener.onSuccess();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                noteDAO().InsertNote(note);
                return null;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                mlistener.onFailure("Operation Cancelled");
            }
        }.execute();

    }
    public void UpdateNote(final Note note, final DataBaseOperationsListener mlistener)
    {

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mlistener.onSuccess();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                noteDAO().DeleteNote(note);
                noteDAO().InsertNote(note);
                return null;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                mlistener.onFailure("Operation Cancelled");
            }
        }.execute();

    }
    public void GetAllNotes(final DataBaseFetchOperationListener mlistener)
    {
        new AsyncTask<Void,Void,Void>(){
            List<Note> models= new ArrayList<>();
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mlistener.onSuccess(models);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                models.clear();
                models.addAll(noteDAO().getAllNotes());
                return null;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                mlistener.onFailure("Operation Cancelled");
            }
        }.execute();
    }
    public void GetNote(final int id, final DatabaseFetchSingleListener mlistener)
    {

        new AsyncTask<Void,Void,Void>(){
            Note note;
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mlistener.onSuccess(note);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                note=noteDAO().getNote(id);
                return null;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                mlistener.onFailure("Operation Cancelled");
            }
        }.execute();

    }

    public interface DataBaseOperationsListener
    {
        void onSuccess();
        void onFailure(String reason);
    }
    public interface DataBaseFetchOperationListener
    {
        void onSuccess(List<Note> notes);
        void onFailure(String reason);
    }
    public interface DatabaseFetchSingleListener
    {
        void onSuccess(Note note);
        void onFailure(String reason);
    }
}
