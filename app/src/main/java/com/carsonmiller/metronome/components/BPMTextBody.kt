package com.carsonmiller.metronome.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.carsonmiller.metronome.ui.theme.typography

/**
 * Text that shows current bpm
 */
@Composable
fun BpmTextBody(modifier: Modifier = Modifier, bpm: Int = 100) {
    ConstraintLayout(
        textConstraints(), modifier = modifier
    ) {
        //number
        Text(
            modifier = Modifier.layoutId("num"),
            text = "$bpm",
            color = MaterialTheme.colorScheme.onBackground,
            style = typography.labelLarge.copy(fontSize = 25.sp) // Adjust the font size as needed
        )

        //bpm text
        Text(
            modifier = Modifier.layoutId("bpmText"),
            text = "bpm",
            color = MaterialTheme.colorScheme.secondary,
            style = typography.labelLarge.copy(fontSize = 23.sp), // Adjust the font size as needed
            fontStyle = FontStyle.Italic
        )
    }
}