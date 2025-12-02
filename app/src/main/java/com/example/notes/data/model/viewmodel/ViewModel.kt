package com.example.notes.data.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.model.NotesDataModel
import com.example.notes.data.model.SyncState
import com.example.notes.repository.NoteRepository
import com.example.notes.repository.SupabaseNotesRepository
import com.example.notes.util.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val supabaseRepo: SupabaseNotesRepository,
    private val connectivityObserver: ConnectivityObserver // Injected
) : ViewModel() {

    private val _notes = MutableStateFlow<List<NotesDataModel>>(emptyList())
    val notes: StateFlow<List<NotesDataModel>> = _notes.asStateFlow()

    init {
        // Observe local database changes and filter out deleted notes for the UI
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllNotes().distinctUntilChanged().collect { list ->
                _notes.value = list.filter { it.syncState != SyncState.DELETED }
                sort()
            }
        }

        // Initial sync from remote
        syncNotes()

        // Observe network status to sync pending notes when online
        connectivityObserver.observe()
            .onEach { status ->
                if (status == ConnectivityObserver.Status.Available) {
                    syncPendingNotes()
                }
            }
            .launchIn(viewModelScope)
    }

    // Sync pending inserts, updates, and deletes
    private fun syncPendingNotes() = viewModelScope.launch(Dispatchers.IO) {
        val allNotes = repository.getAllNotes().first()

        // Sync pending inserts/updates
        val pendingNotes = allNotes.filter { it.syncState == SyncState.PENDING }
        pendingNotes.forEach { note ->
            try {
                supabaseRepo.uploadNoteToSupabase(note)
                repository.updateNote(note.copy(syncState = SyncState.SYNCED))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Sync pending deletes
        val deletedNotes = allNotes.filter { it.syncState == SyncState.DELETED }
        deletedNotes.forEach { note ->
            try {
                supabaseRepo.deleteNoteFromSupabase(note.id)
                repository.deleteNote(note) // Permanent local delete after remote delete
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Sync all notes (remote -> local)
    private fun syncNotes() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val remoteNotes = supabaseRepo.fetchNotesFromSupabase()
            val localNotes = repository.getAllNotes().first()
            val localIds = localNotes.map { it.id }.toSet()
            remoteNotes.forEach { remoteNote ->
                if (remoteNote.id !in localIds) {
                    repository.addNote(remoteNote.copy(syncState = SyncState.SYNCED))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun sort() = _notes.update { it.sortedByDescending { it.isPinned } }

    // ------------------------------------------------------------------
    // Add note locally, mark as pending for sync
    // ------------------------------------------------------------------
    fun addNotes(newNote: NotesDataModel) = viewModelScope.launch {
        repository.addNote(newNote.copy(syncState = SyncState.PENDING))
        syncPendingNotes() // Attempt to sync immediately if online
    }

    // ------------------------------------------------------------------
    // Update both local + Supabase
    // ------------------------------------------------------------------
    private fun updateNotes(note: NotesDataModel) = viewModelScope.launch {
        repository.updateNote(note.copy(syncState = SyncState.PENDING))
        syncPendingNotes() // Attempt to sync immediately if online
    }

    // ------------------------------------------------------------------
    // Soft delete locally, then sync deletion
    // ------------------------------------------------------------------
    private fun deleteNotes(note: NotesDataModel) = viewModelScope.launch {
        repository.updateNote(note.copy(syncState = SyncState.DELETED))
        syncPendingNotes() // Attempt to sync deletion immediately if online
    }

    fun deleteAllNotes(noteList: List<NotesDataModel>) {
        noteList.forEach { deleteNotes(it) }
    }

    fun pinNotes(noteList: List<NotesDataModel>, pinned: Boolean) = viewModelScope.launch {
        noteList.forEach { note ->
            val updated = note.copy(isPinned = !pinned)
            updateNotes(updated)
        }
    }
}
