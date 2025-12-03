package com.example.notes.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.Instant
import java.util.UUID

@Serializable
enum class SyncState { SYNCED, PENDING, DELETED }

@Serializable
@Entity(tableName = "notes_tbl")
@Parcelize
data class NotesDataModel(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    @ColumnInfo(name = "user_id")
    @SerialName("user_id")
    val userId: String = "",

    @ColumnInfo(name = "note_title")
    @SerialName("note_title")
    val title: String = "",

    @ColumnInfo(name = "note_description")
    @SerialName("note_description")
    val description: String = "",

    @ColumnInfo(name = "note_entry_date")
    @SerialName("note_entry_date")
    val date: Long = Instant.now().toEpochMilli(),

    @ColumnInfo(name = "note_is_pined")
    @SerialName("note_is_pined")
    val isPinned: Boolean = false,

    @ColumnInfo(name = "note_sync_state")
    @Transient
    val syncState: SyncState = SyncState.PENDING,

    @ColumnInfo(name = "note_color")
    @SerialName("note_color")
    @Transient
    val colorIndex: Int = 0

) : Parcelable
