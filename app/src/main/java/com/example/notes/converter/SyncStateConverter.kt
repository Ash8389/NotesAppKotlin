package com.example.notes.converter

import androidx.room.TypeConverter
import com.example.notes.data.model.SyncState

class SyncStateConverter {
    @TypeConverter
    fun fromSyncState(state: SyncState?): String? = state?.name

    @TypeConverter
    fun toSyncState(value: String?): SyncState? = value?.let { SyncState.valueOf(it) }
}
