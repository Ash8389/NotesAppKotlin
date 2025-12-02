package com.example.notes.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.notes.data.model.NotesDataModel

@Composable
fun NotesDetailScreen(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDesChange: (String) -> Unit,
    data: NotesDataModel,
    saveData: () -> Unit,
) {

    InputScreen(
        modifier = modifier,
        title = title,
        description = description,
        onTitleChange = onTitleChange,
        onDesChange = onDesChange,
        currNote = data,
        saveData = { saveData() },
    )

}

