package com.example.notes.data.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.notes.converter.DateConverter
import com.example.notes.converter.UUIDConverter
import com.example.notes.converter.SyncStateConverter


@Database(
    entities = [NotesDataModel::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    DateConverter::class,
    UUIDConverter::class,
    SyncStateConverter::class   // ✅ MUST ADD THIS
)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDatabaseDao
}
