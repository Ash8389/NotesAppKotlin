package com.example.notes

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notes.data.model.NotesDataModel
import com.example.notes.data.model.viewmodel.NotesViewModel
import com.example.notes.screens.HomeScreen
import com.example.notes.screens.InputScreen
import com.example.notes.screens.NotesDetailScreen
import com.example.notes.ui.theme.NotesTheme
import com.example.notes.widget.AppBar
import com.example.notes.widget.BottomBar
import com.example.notes.widget.ColorCards
import com.example.notes.widget.DeletePopUp
import com.example.notes.widget.share
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.max
import kotlin.math.min

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesTheme {
                Surface(modifier = Modifier) {
                    var title = rememberSaveable { mutableStateOf("") }
                    var description = rememberSaveable { mutableStateOf("") }
                    val notesVewModel:NotesViewModel by viewModels()
                    ChangeScreen(
                        notesViewModel = notesVewModel,
                        title = title,
                        description = description,
                        onTitleChange = { title.value = it },
                        onDesChange = { description.value = it },
                    )
                }
            }
        }
    }
}

enum class ScreenName {
    HomeScreen,
    DetailScreen,
    InputScreen
}

fun pinned(note: List<NotesDataModel>) : Boolean{
    note.forEach { if(it.isPinned) return true }

    return false
}

fun saveData(notesViewModel: NotesViewModel, title: String, description: String, note: NotesDataModel, colorIndex: Int) {
    if(title == "" && description == "")
        return;
    if(title == "" || description == "") {
        if(title == ""){
            notesViewModel.addNotes(note.copy(title = description.substring(0, max(1, min(5, description.length))), description = description, date = note.date, colorIndex = colorIndex))
        }else{
            notesViewModel.addNotes(note.copy(title = title, description = description, date = note.date, colorIndex = colorIndex))
        }
    }else {
        notesViewModel.addNotes(note.copy(title = title, description = description, date = note.date, colorIndex = colorIndex))
    }
}

@Composable
fun Toaster(message: String){
    Toast.makeText( LocalContext.current, message , Toast.LENGTH_SHORT).show()
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChangeScreen(
    modifier: Modifier = Modifier,
    title: MutableState<String>,
    description: MutableState<String>,
    onTitleChange: (String) -> Unit,
    onDesChange: (String) -> Unit,
    navController: NavHostController = rememberNavController(),
    notesViewModel: NotesViewModel
    ) {
        val notesData = notesViewModel.notes.collectAsStateWithLifecycle().value
        var selectedNote = rememberSaveable { mutableStateOf<List<NotesDataModel>>(emptyList()) }
        var showHomeScreenBottomBar = rememberSaveable { mutableStateOf(false) }
        var showToaster = rememberSaveable { mutableStateOf(false) }
        var showColorOptions = rememberSaveable { mutableStateOf(false) }
        var colorIndex = rememberSaveable { mutableStateOf(0) }

        if(selectedNote.value.isEmpty()){
            showHomeScreenBottomBar.value = false
        }

    Scaffold(
        topBar = {
            AppBar(
                navController = navController,
                selectedNotes = selectedNote.value.size,
                goBack = {
                    if(navController.previousBackStackEntry != null) {
                        navController.previousBackStackEntry?.savedStateHandle?.remove<NotesDataModel>("note")
                        title.value = ""
                        description.value = ""
                        showColorOptions.value = false
                        navController.popBackStack()
                    }else {
                        selectedNote.value = emptyList()
                    }
                },
                saveNote = {
                    val currentRoute = navController.currentDestination?.route
                    val currentNote = if (currentRoute == ScreenName.DetailScreen.name) {
                        navController.previousBackStackEntry?.savedStateHandle?.get<NotesDataModel>("note") ?: NotesDataModel()
                    } else {
                        NotesDataModel()
                    }
                    saveData( notesViewModel = notesViewModel,title = title.value, description =  description.value, note = currentNote, colorIndex = colorIndex.value)
                    navController.previousBackStackEntry?.savedStateHandle?.remove<NotesDataModel>("note")
                    title.value = ""
                    description.value = ""
                    showColorOptions.value = false
                    navController.popBackStack()
                },
                showColorToggle = {
                    showColorOptions.value = !showColorOptions.value
                }
            )
        },
        bottomBar = {
            val context = LocalContext.current
            val pinned = pinned(selectedNote.value)
                AnimatedVisibility(
                    visible = (selectedNote.value.isNotEmpty() || showColorOptions.value)
                ) {
                    if(showToaster.value){
                        Toaster(message = "Deleted")
                        showToaster.value = false
                    }
                    if(showHomeScreenBottomBar.value){
                        DeletePopUp(
                            totalSelected = selectedNote.value.size,
                            delete = {
                                showHomeScreenBottomBar.value = false
                                notesViewModel.deleteAllNotes(selectedNote.value)
                                selectedNote.value = emptyList()
                                showToaster.value = true
                            },
                            cancel = {
                                showHomeScreenBottomBar.value = false
                            }
                        )
                    }
                    if(showColorOptions.value)
                        ColorCards(onColorSelected = { selectedColorIndex ->
                            if (selectedNote.value.isNotEmpty()) {
                                // Update multiple notes
                                notesViewModel.updateNotesColor(selectedNote.value, selectedColorIndex)
                                selectedNote.value = emptyList()
                                showColorOptions.value = false
                            } else {
                                // Update current editing note state
                                colorIndex.value = selectedColorIndex
                            }
                        })

                    else if(!showHomeScreenBottomBar.value && selectedNote.value.isNotEmpty())
                        BottomBar(
                            pinned = pinned,
                            delete = {
                                showHomeScreenBottomBar.value = true
                            },
                            share = {
                                share(context = context, notesList = selectedNote.value)
                            },
                            pin = {
                                notesViewModel.pinNotes(selectedNote.value, pinned)
                                selectedNote.value = emptyList()
                            }
                        )
                }
        }
    ) {innerPadding ->
        NavHost(navController = navController, startDestination = ScreenName.HomeScreen.name){
            composable(route = ScreenName.HomeScreen.name) {
                HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    listOfNotes = notesData,
                    totalSelected = selectedNote.value.size,
                    goToDetailScreen = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("note", it)
                        navController.navigate(route = ScreenName.DetailScreen.name)
                        selectedNote.value = emptyList()
                    },
                    goToInputScreen = {
                        navController.navigate(route = ScreenName.InputScreen.name)
                    },
                    cardSelected = {
                        val isContain = selectedNote.value.contains(it)
                        if(isContain)
                            selectedNote.value -= it;
                        else
                            selectedNote.value += it;
                    },
                )
            }
            composable(route = ScreenName.DetailScreen.name){
                val currentNote = navController.previousBackStackEntry?.savedStateHandle?.get<NotesDataModel>("note")
                if(currentNote != null){
                    LaunchedEffect(Unit) {
                        if(title.value == "" && description.value == "") {
                            title.value = currentNote.title
                            description.value = currentNote.description
                            colorIndex.value = currentNote.colorIndex
                        }
                    }
                    NotesDetailScreen(
                        modifier = modifier.padding(innerPadding),
                        title = title.value,
                        description = description.value,
                        onTitleChange = onTitleChange,
                        onDesChange = onDesChange,
                        data = currentNote,
                        colorIndex = colorIndex.value,
                        saveData = {
                            saveData(
                                notesViewModel = notesViewModel,
                                title = title.value,
                                description = description.value,
                                note = currentNote,
                                colorIndex = colorIndex.value
                            )
//                            newNote -> notesViewModel.addNotes(newNote)
                            navController.previousBackStackEntry?.savedStateHandle?.remove<NotesDataModel>("note")
                            title.value = ""
                            description.value = ""
                            showColorOptions.value = false
                            navController.popBackStack()
                        }
                    )
                }
            }
            composable(route = ScreenName.InputScreen.name) {
                val newNote = NotesDataModel()
                LaunchedEffect(Unit) {
                    if(title.value == "" && description.value == "") {
                        title.value = newNote.title
                        description.value = newNote.description
                        colorIndex.value = newNote.colorIndex
                    }
                }
                InputScreen(
                    modifier = modifier.padding(innerPadding),
                    title = title.value,
                    description = description.value,
                    onTitleChange = onTitleChange,
                    onDesChange = onDesChange,
                    currNote = newNote,
                    colorIndex = colorIndex.value,
                    saveData = {
                        saveData(
                            notesViewModel = notesViewModel,
                            title = title.value,
                            description = description.value,
                            note = newNote,
                            colorIndex = colorIndex.value
                        )
                        title.value = ""
                        description.value = ""
                        showColorOptions.value = false
                        navController.popBackStack()
                    },
                )
            }
        }
    }

}