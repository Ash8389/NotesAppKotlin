package com.example.notes.repository

import android.util.Log
import com.example.notes.data.model.NotesDataModel
import com.example.notes.supabase.SupabaseClientHolder
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.postgrest

import javax.inject.Inject

class SupabaseNotesRepository @Inject constructor() {

    private val client = SupabaseClientHolder.client

    // Upload note - Throws exception on failure
    suspend fun uploadNoteToSupabase(note: NotesDataModel) {
        client.postgrest["notes"].upsert(listOf(note))
    }

    // Update note - Throws exception on failure
    suspend fun updateNoteOnSupabase(note: NotesDataModel) {
        client.postgrest["notes"].update(
            {
                set("note_title", note.title)
                set("note_description", note.description)
                set("note_entry_date", note.date)
                set("note_is_pined", note.isPinned)
                set("note_color", note.colorIndex)
            }
        ) {
            filter {
                eq("id", note.id.toString())
            }
        }
    }

    // Delete note - Throws exception on failure
    suspend fun deleteNoteFromSupabase(noteId: String) {
        client.postgrest["notes"].delete {
            filter {
                eq("id", noteId.toString())
            }
        }
    }

    // Fetch all notes - Returns empty list on failure
    suspend fun fetchNotesFromSupabase(): List<NotesDataModel> {
        return try {
            client.postgrest["notes"].select().decodeList<NotesDataModel>()
        } catch (e: HttpRequestException) {
            Log.e("SupabaseNotesRepository", "Error fetching notes", e)
            emptyList()
        }
    }
}
