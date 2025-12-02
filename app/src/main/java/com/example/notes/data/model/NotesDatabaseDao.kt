package com.example.notes.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDatabaseDao {

    @Query("SELECT * from notes_tbl")
    fun getNotes() : Flow<List<NotesDataModel>>;

    @Query("SELECT * from notes_tbl where id =:id")
    suspend fun getNoteById(id: String) : NotesDataModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NotesDataModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(note: NotesDataModel)

    @Query("DELETE from notes_tbl")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteNote(note: NotesDataModel)

    @Query("UPDATE notes_tbl SET note_sync_state = :state WHERE id = :id")
    suspend fun updateSyncState(id: String, state: SyncState)

    @Query("SELECT * FROM notes_tbl WHERE note_sync_state = 'PENDING'")
    suspend fun getPendingNotes(): List<NotesDataModel>

}
