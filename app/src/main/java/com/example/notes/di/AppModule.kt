package com.example.notes.di

import android.content.Context
import androidx.room.Room
import com.example.notes.data.model.NoteDatabase
import com.example.notes.data.model.NotesDatabaseDao
import com.example.notes.util.ConnectivityObserver
import com.example.notes.util.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideNotesDao(noteDatabase: NoteDatabase): NotesDatabaseDao = noteDatabase.notesDao()

    @Singleton
    @Provides
    fun provideAppDataBase(@ApplicationContext context: Context): NoteDatabase = Room.databaseBuilder(
        context,
        NoteDatabase::class.java,
        "notes_db")
        .fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideSupabaseRepository(): com.example.notes.repository.SupabaseNotesRepository = com.example.notes.repository.SupabaseNotesRepository()

    @Singleton
    @Provides
    fun provideConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver {
        return NetworkConnectivityObserver(context)
    }
}