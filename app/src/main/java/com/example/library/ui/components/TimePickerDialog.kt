package com.example.library.ui.components

import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import java.time.LocalTime
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(showDialog) {
        if(showDialog) {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(
                context,
                { _,
                selectedHour, selectedMinute ->
                    val selectedTime = LocalTime.of(selectedHour, selectedMinute)
                    onTimeSelected(selectedTime)
                },
                hour, minute, true
            )

            timePicker.setOnCancelListener { onDismiss() }
            timePicker.show()
        }
    }
}