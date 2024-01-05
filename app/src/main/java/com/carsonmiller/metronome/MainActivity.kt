package com.carsonmiller.metronome

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.constraintlayout.compose.ConstraintLayout
import com.carsonmiller.metronome.components.*
import com.carsonmiller.metronome.ui.theme.MetronomeTheme
import com.carsonmiller.metronome.state.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ComposeActivity : ComponentActivity() {
    private val appContext: Context
        get() = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MetronomeTheme {
                MainLayout(
                    musicSettingsList = PersistentMusicSegmentList(this),
                    appSettings = PersistentAppSettings(this)
                )

            }
            MainLayout(musicSettingsList =PersistentMusicSegmentList(this) , appSettings = PersistentAppSettings(this))
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainLayout(
        musicSettingsList: PersistentMusicSegmentList,
        appSettings: PersistentAppSettings
    ) = ConstraintLayout(
        remember { containerConstraints() },
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
    ) {
        val musicSettings by remember { mutableStateOf(musicSettingsList[appSettings.currentMusicSettings]) }
        var elapsedTime by remember { mutableStateOf(0L) }
        var job: Job? by remember { mutableStateOf(null) }

        // Music staff container
        HeaderBody(
            modifier = Modifier
                .containerModifier(ScreenSettings.headerContainerHeight)
                .layoutId("headerBox"),
            numerator = musicSettings.numerator,
            denominator = musicSettings.denominator,
            appSettings = appSettings,
            musicSettings = musicSettings
        )

        // Button container
        ButtonBody(
            modifier = Modifier
                .containerModifier(ScreenSettings.buttonContainerHeight)
                .layoutId("buttonBox"),
            settings = musicSettings // only pass in settings when state is being changed.
        )

        // Settings container
        PagerContainer(
            modifier = Modifier
                .containerModifier(ScreenSettings.settingsContainerHeight)
                .layoutId("settingsBox"),
            {
                // BPM text and Play/Pause button container
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // BPM text
                    BpmTextBody(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(8.dp)
                            .layoutId("bpmText"), bpm = musicSettings.bpm

                    )

                    var noteIndex by remember { mutableStateOf(0) } // Initialize with 0
                    PlayPauseButton(
                        NotPlaying = musicSettings.NotPlaying,
                        onClick = {
                            // Handle play/pause click
                            musicSettings.togglePlaying()

                            if (musicSettings.NotPlaying) {
                                // If playing, start the timer coroutine
                                job = CoroutineScope(Dispatchers.Default).launch {
                                    while (musicSettings.NotPlaying) {
                                        // Calculate delay based on BPM
                                        val delay = calculateDelay(musicSettings.bpm)
                                        delay(delay)

                                        // Set noteIndex to 0 if it reaches the end
                                        if (noteIndex == musicSettings.numOfNotes) {
                                            noteIndex = 0
                                        }

                                        val noteIntensity = musicSettings.get(noteIndex).level

                                        // Play audio based on note intensity
                                        playAudio(appContext, noteIntensity)

                                        noteIndex += 1
                                        elapsedTime += 1
                                    }
                                }
                            } else {
                                // If stopped, cancel the timer coroutine and reset noteIndex
                                job?.cancel()
                                noteIndex = 0
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(50.dp)
                    )
                }
            },
            {
                var todoText by remember {
                    mutableStateOf("")
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = todoText,
                            onValueChange = { todoText = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .clip(CircleShape),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        Button(onClick = {
                            musicSettingsList.add()
                            todoText = ""
                        }, enabled = todoText.isNotBlank()) {
                            Text(text = "Add")
                        }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(musicSettingsList.asList()) { index, musicSegment ->
                            Card(elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
                                androidx.compose.material3.ListItem(
                                    headlineText = {
                                        Text(
                                            text = musicSegment.presetName,
                                            fontSize = 18.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )


                                    },
                                    trailingContent = {
                                        IconButton(onClick = {
                                            musicSettingsList.remove(index)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "",
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            },
            { Text("Test3") }
        )
    }



    fun playAudio(context: Context, noteIntensity: NoteIntensity) {
        when (noteIntensity) {
            NoteIntensity.Rest -> {
                // Log the intensity
                Log.d("NoteIntensity", "Rest")
            }
            NoteIntensity.Quiet -> {
                // Log the intensity
                Log.d("NoteIntensity", "Quiet")

                // Play audio using quiet MediaPlayer
                val quiet: MediaPlayer = MediaPlayer.create(context, R.raw.quiet)
                quiet.start()
            }
            NoteIntensity.Normal -> {
                // Log the intensity
                Log.d("NoteIntensity", "Normal")

                // Play audio using normal MediaPlayer
                val normal: MediaPlayer = MediaPlayer.create(context, R.raw.normal)
                normal.start()
            }
            NoteIntensity.Loud -> {
                // Log the intensity
                Log.d("NoteIntensity", "Loud")

                // Play audio using loud MediaPlayer
                val strong: MediaPlayer = MediaPlayer.create(context, R.raw.strong)
                strong.start()
            }
        }
    }



    @Preview
    @Composable
    fun MainLayoutPreview() {
        val musicSettingsList = PersistentMusicSegmentList(activity = this)
        val appSettings = PersistentAppSettings(activity = this)

        MainLayout(musicSettingsList = musicSettingsList, appSettings = appSettings)
    }


    fun calculateDelay(bpm: Int): Long {
        return if (bpm > 0) {
            (60 * 1000 / bpm).toLong()
        } else {
            0
        }
    }



    @Composable
    fun PlayPauseButton(NotPlaying: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
        // Use vector drawables for play and pause
        val playDrawable = if (NotPlaying) R.drawable.ic_pause else R.drawable.ic_play

        // Use painterResource to load the vector drawable
        val playPauseIcon = painterResource(id = playDrawable)

        // Display the icon in your Compose UI with a larger size
        var isButtonClicked by remember { mutableStateOf(false) }

        Box(
            modifier = modifier
                .size(180.dp) // Adjust the size as needed
                .clip(CircleShape)
                .clickable {
                    isButtonClicked = true
                    onClick()

                    // Log statement for the click along with NotPlaying status
                    Log.d("PlayPauseButton", "Button clicked! Status: ${if (NotPlaying) "Playing" else "Stopped"}")
                }
        ) {
            Icon(
                painter = playPauseIcon,
                contentDescription = "Play/Pause",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp) // Add padding to center the vector
                    .drawBehind {
                        drawCircle(
                            color = Color.Red, // Adjust the outline color as needed
                            center = center,
                            radius = size.minDimension / 2,
                            style = Stroke(width = 10.dp.toPx())
                        )
                    }
            )
        }

        // Use LaunchedEffect to reset the button state after a delay
        LaunchedEffect(isButtonClicked) {
            delay(200)
            isButtonClicked = false
        }

        // Use LaunchedEffect to reset the button state after a delay
        LaunchedEffect(isButtonClicked) {
            delay(200)
            isButtonClicked = false
        }
    }


}


