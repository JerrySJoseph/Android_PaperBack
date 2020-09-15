package com.jstechnologies.paperback.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.jstechnologies.paperback.Models.Note;

import java.util.List;

@Dao
public interface NoteDAO {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<Note> getAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertNote(Note note);

    @Delete
    void DeleteNote(Note note);

    @Query("SELECT * FROM notes WHERE id =:id")
    Note getNote(int id);

    @Update
    void Update(Note note);
}
