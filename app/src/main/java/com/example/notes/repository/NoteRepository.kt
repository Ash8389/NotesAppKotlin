package com.example.notes.repository

import com.example.notes.data.model.NotesDataModel
import com.example.notes.data.model.NotesDatabaseDao
import com.example.notes.data.model.SyncState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NoteRepository @Inject constructor(private val notesDatabaseDao: NotesDatabaseDao) {
    suspend fun addNote(note: NotesDataModel) = notesDatabaseDao.insert(note)

    suspend fun updateNote(note: NotesDataModel) = notesDatabaseDao.update(note)

    suspend fun deleteNote(note: NotesDataModel) = notesDatabaseDao.deleteNote(note)

    suspend fun deleteAllNote() = notesDatabaseDao.deleteAll()

    suspend fun getAllNotes(): Flow<List<NotesDataModel>> = notesDatabaseDao.getNotes().flowOn(Dispatchers.IO).conflate()

    suspend fun markAsSynced(id: String) {
        notesDatabaseDao.updateSyncState(id, SyncState.SYNCED)
    }

    suspend fun getPendingNotes(): List<NotesDataModel> = notesDatabaseDao.getPendingNotes()

}