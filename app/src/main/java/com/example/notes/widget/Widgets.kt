package com.example.notes.widget

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.notes.R
import com.example.notes.ScreenName
import com.example.notes.data.model.NotesDataModel
import java.text.SimpleDateFormat


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    title: String = "",
    description: String = "",
    date: String,
    totalSelected : Int,
    isPinned: Boolean,
    colorIndex: Int = 0,
    goToDetailScreen : () -> Unit,
    cardSelected : () -> Unit,
) {
    val marked = rememberSaveable { mutableStateOf(false) }
    if(totalSelected == 0) {
        marked.value = false
    }
    val colorOption = ColorOption.values().getOrElse(colorIndex) { ColorOption.Default }
    val isSystemInDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    val isDefault = colorIndex == 0
    val cardColor = if (isDefault) MaterialTheme.colorScheme.primaryContainer else colorOption.darkColor
    val textColor = if (isDefault) MaterialTheme.colorScheme.onSurface else Color.White

    Card(
        modifier = Modifier
            .width(200.dp)
            .padding(10.dp)
            .combinedClickable(
                onClick = {
                    if (totalSelected == 0) {
                        goToDetailScreen()
                    } else {
                        marked.value = !marked.value
                        cardSelected()
                    }
                },
                onLongClick = {
                    cardSelected()
                    marked.value = !marked.value
                }
            ),
        shape = RoundedCornerShape(topEnd = 15.dp, bottomStart = 15.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Box( modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier =
                if (marked.value) {
                    Modifier
                        .background(Color.Transparent)
                        .alpha(0.5f)
                        .fillMaxSize()
                } else { Modifier.fillMaxSize() },
                color = Color.Transparent
            ) {
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        maxLines = 1,

                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 5.dp, bottom = 2.5.dp, top = 5.dp),
                        color = textColor
                    )
                    Text(
                        text = description,
                        maxLines = 4,

                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 5.dp, bottom = 2.5.dp, top = 2.5.dp),
                        color = textColor
                    )
                    Row {
                        Text(
                            text = date,
                            fontWeight = FontWeight.W400,

                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 5.dp, bottom = 5.dp, top = 2.5.dp),
                            color = textColor
                        )
                        if(isPinned) {
                            Icon(
                                imageVector = ImageVector.vectorResource(
                                    id = R.drawable.pin
                                ),
                                contentDescription = "pin",
                                tint = Color.Red
                            )
                        }
                    }

                }
            }
            if(totalSelected > 0)
            Surface(
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .size(33.dp)
                    .clip(CircleShape)
                    .clickable {
                        marked.value = !marked.value
                        cardSelected()
                    },
                color = Color.Gray

            ) {
                if(marked.value)
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "selected",
                        modifier = Modifier.size(30.dp),
                    )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    selectedNotes: Int,
    goBack : () -> Unit,
    saveNote: () -> Unit,
    showColorToggle: () -> Unit,
    navController: NavHostController,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    TopAppBar(
        title = {
            if (currentBackStackEntry?.destination?.route == ScreenName.HomeScreen.name && selectedNotes == 0){
                Text(text = "Notes", fontSize = 40.sp, fontWeight = FontWeight.Bold)
            }
        },
        navigationIcon = {
                    AnimatedVisibility(visible = selectedNotes>0 || currentBackStackEntry?.destination?.route != ScreenName.HomeScreen.name) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier
                                .size(35.dp)
                                .clickable {
                                    goBack()
                                }
                        )
                    }
        },
        actions = {
            if (currentBackStackEntry?.destination?.route != ScreenName.HomeScreen.name) {

                Icon(
                    imageVector = Icons.Filled.Palette,
                    contentDescription = "Color",
                    modifier = Modifier
                        .size(35.dp)
                        .clickable { showColorToggle() }
                )
                Spacer(Modifier.padding(end=20.dp))
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done",
                    modifier = Modifier
                        .size(35.dp)
                        .clickable { saveNote() }
                )
            }
        },
        modifier = Modifier.padding(start = 5.dp)
    )
}

@Composable
fun GetInput(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    saveData : () -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions,
    fontWeight: FontWeight,
    fontSize: Int,
    textColor: Color = Color.Unspecified
) {

    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontWeight = fontWeight,
            fontSize = fontSize.sp,
        ),
        placeholder = { Text(
            text = placeholder,
            fontWeight = fontWeight,
            fontSize = fontSize.sp,
            modifier = Modifier
        ) },
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions (onDone = {saveData()}),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedPlaceholderColor = textColor.copy(alpha = 0.7f),
            unfocusedPlaceholderColor = textColor.copy(alpha = 0.7f)
        )
    )
}

enum class ColorOption(val lightColor: Color, val darkColor: Color) {
    Default(Color(0xFF1E293B), Color(0xFF1E293B)), // Slate 100 / 800
    Red(Color(0xFFFEE2E2), Color(0xFF7F1D1D)),     // Red 100 / 900
    Orange(Color(0xFFFFE4E6), Color(0xFF881337)),  // Rose 100 / 900 (using Rose for Orange slot as it's nicer)
    Yellow(Color(0xFFFEF9C3), Color(0xFF713F12)),  // Yellow 100 / 900
    Green(Color(0xFFDCFCE7), Color(0xFF14532D)),   // Green 100 / 900
    Teal(Color(0xFFCCFBF1), Color(0xFF134E4A)),    // Teal 100 / 900
    Blue(Color(0xFFDBEAFE), Color(0xFF1E3A8A)),    // Blue 100 / 900
    Purple(Color(0xFFF3E8FF), Color(0xFF581C87)),  // Purple 100 / 900
    Pink(Color(0xFFFCE7F3), Color(0xFF831843)),    // Pink 100 / 900
    Brown(Color(0xFFEFEBE9), Color(0xFF3E2723)),   // Brown 50 / 900
    Gray(Color(0xFFE5E7EB), Color(0xFF374151))     // Gray 200 / 700
}

@Composable
fun ColorCards(onColorSelected: (Int) -> Unit) {

    BottomAppBar (
        modifier = Modifier.height(80.dp),
        actions = {
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items(ColorOption.values().size) { index ->
                    ColorCard(ColorOption.values()[index], onClick = { onColorSelected(index) })
                }
            }
        }
    )
}

@Composable
fun ColorCard(appColor: ColorOption, onClick: () -> Unit) {
    val isSystemInDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    val cardColor = if(appColor == ColorOption.Default) MaterialTheme.colorScheme.background else appColor.darkColor
    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(50.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        shape = CircleShape
    ) {
        // Empty content for color circle
    }
}


@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    pinned : Boolean,
    delete: () -> Unit,
    share: () -> Unit,
    pin: () -> Unit
){
    BottomAppBar (
        modifier = Modifier.height(80.dp),
        actions = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                IconButton(onClick = { pin() }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id =  if(!pinned)R.drawable.pin else R.drawable.unpin),
                        contentDescription = "edit",
                        modifier = Modifier.size(33.dp)
                    )
                }

                IconButton(onClick = { delete() }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "delete",
                        modifier = Modifier.size(33.dp)
                    )
                }

                IconButton(onClick = { share() }) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "share",
                        modifier = Modifier.size(33.dp)
                    )
                }
            }

        }
    )
}

@Composable
fun DeletePopUp(
    modifier: Modifier = Modifier,
    totalSelected: Int,
    delete: () -> Unit,
    cancel: () -> Unit
){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(220.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(20.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,

            ) {
                Text(
                    text = "Delete notes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp

                )
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = "Delete $totalSelected item?",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(onClick = { cancel() }) {
                        Text(
                            text = "Cancel",
                            fontSize = 20.sp
                        )
                    }
                    Button(onClick = { delete() }) {
                        Text(
                            text = "Delete",
                            fontSize = 20.sp
                        )
                    }

                }
            }
    }
}

fun share(
    context: Context, notesList: List<NotesDataModel>
) {
    val notesText = notesList.joinToString(separator = "\n\n") { note ->
        "Date : ${SimpleDateFormat("dd/MM/YYYY HH:mm").format(note.date)}\nTitle : ${note.title}\nContent : ${note.description}"
    }

    val sendIntent : Intent = Intent().apply {
        action = Intent.ACTION_SEND_MULTIPLE
        putExtra(Intent.EXTRA_TEXT, notesText)
        type = "text/plain"
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    val shareIntent = Intent.createChooser(sendIntent, "share Notes")
    context.startActivity( shareIntent )
}
