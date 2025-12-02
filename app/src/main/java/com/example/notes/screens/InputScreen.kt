package com.example.notes.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notes.data.model.NotesDataModel
import com.example.notes.widget.GetInput
import java.util.Date
import java.text.SimpleDateFormat
import java.time.Instant

@Composable
fun InputScreen(
    modifier: Modifier = Modifier,
    title : String,
    description : String,
    onTitleChange : (String) -> Unit,
    onDesChange : (String) -> Unit,
    currNote: NotesDataModel,
    saveData : () -> Unit,
) {
    val date : String = SimpleDateFormat("dd/MM/YYYY HH:mm").format(currNote?.date)


    Column(modifier = modifier) {
        GetInput(value = title, onValueChange = onTitleChange,
            placeholder = "Title",
            fontWeight = FontWeight.Bold,
            fontSize = 25,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            saveData = {},
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "$date  |  ${description.length} characters",
            fontWeight = FontWeight.W400,
            fontSize = 13.sp,
            modifier = Modifier.padding(start = 15.dp, bottom = 5.dp, top = 0.dp)
        )

        GetInput(value = description, onValueChange = onDesChange,
            placeholder = "Start typing..",
            fontWeight = FontWeight.Normal,
            fontSize = 20,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default,
            ),
            saveData = { saveData() },
            modifier = Modifier.fillMaxSize()
        )


    }
}
