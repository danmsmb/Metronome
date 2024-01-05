package com.carsonmiller.metronome.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.carsonmiller.metronome.R
import com.carsonmiller.metronome.state.PersistentMusicSegment
import com.carsonmiller.metronome.state.ScreenSettings

@Composable
fun ButtonBody(modifier: Modifier = Modifier, settings: PersistentMusicSegment) = Row(
    modifier = modifier.padding(5.dp), horizontalArrangement = Arrangement.Center
) {
    val buttonSize = 70.dp // Adjust the button size
    fun buttonModifier(size: Dp) = Modifier
        .align(Alignment.CenterVertically)
        .padding(ScreenSettings.innerPadding / 2)
        .size(size)

    MusicButton(
        modifier = buttonModifier(buttonSize),
        onClick = { settings.bpm -= 1 },
        contents = {
            Image(
                painter = painterResource(id = R.drawable.ic_minus),
                contentDescription = "Minus Icon",
                modifier = Modifier.size(40.dp) // Adjust the icon size
            )
        },
        isHoldable = true
    )

    MusicButton(
        modifier = buttonModifier(buttonSize * 1.2f),
        onClick = { settings.reset() },
        contents = {
            Image(
                painter = painterResource(id = R.drawable.ic_reset),
                contentDescription = "Reset Icon",
                modifier = Modifier.size(40.dp) // Adjust the icon size
            )
        },
        isHoldable = false,
        buttonColor = Color.Red
    )

    MusicButton(
        modifier = buttonModifier(buttonSize),
        onClick = { settings.bpm += 1 },
        contents = {
            Image(
                painter = painterResource(id = R.drawable.ic_plus),
                contentDescription = "Plus Icon",
                modifier = Modifier.size(40.dp) // Adjust the icon size
            )
        },
        isHoldable = true
    )
}
