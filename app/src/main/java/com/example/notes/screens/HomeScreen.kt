package com.example.notes.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notes.data.model.NotesDataModel
import com.example.notes.widget.BottomBar
import com.example.notes.widget.NoteCard
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RememberReturnType")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    listOfNotes: List<NotesDataModel>,
    totalSelected: Int,
    goToDetailScreen: (NotesDataModel) -> Unit,
    goToInputScreen: () -> Unit,
    cardSelected: (NotesDataModel) -> Unit
) {
        NotesList(
            modifier = modifier,
            listOfNotes = listOfNotes,
            goToDetailScreen = goToDetailScreen,
            goToInputScreen = { goToInputScreen() },
            cardSelected = cardSelected,
            totalSelected = totalSelected
        )
}

//@Preview
@SuppressLint("SimpleDateFormat")
@Composable
fun NotesList(
    modifier: Modifier = Modifier,
    listOfNotes: List<NotesDataModel>,
    totalSelected : Int,
    goToDetailScreen : (NotesDataModel) -> Unit = {},
    goToInputScreen : () -> Unit = {},
    cardSelected : (NotesDataModel) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalStaggeredGrid(
            modifier = Modifier,
            columns = StaggeredGridCells.Fixed(2),
            content = {
                items(listOfNotes) { item->
                    val date = SimpleDateFormat("dd/MM/yyyy hh:mm").format(item.date)
                    NoteCard(
                        title = item.title,
                        description = item.description,
                        date = date,
                        totalSelected = totalSelected,
                        isPinned = item.isPinned,
                        goToDetailScreen = { goToDetailScreen(item) },
                        cardSelected = { cardSelected(item) },
                    )
                }
            }
        )
        if(totalSelected<=0)
        Card(
            modifier = Modifier
                .size(50.dp)
                .align(alignment = Alignment.BottomEnd)
                .offset(x = (-20).dp, y = (-33).dp)
                .clickable { goToInputScreen() },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp,
                pressedElevation = 10.dp,
                hoveredElevation = 10.dp,
            ),
            colors = CardDefaults.cardColors(containerColor = Color.Gray)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "add notes",
                tint = Color.Black,
                modifier = Modifier.fillMaxSize()
            )
        }

    }

}