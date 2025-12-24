package com.example.library.ui.components

import android.app.DatePickerDialog
import android.graphics.fonts.Font
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
import java.time.LocalDate
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(showDialog) {
        if(showDialog){
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate =
                        LocalDate.of(selectedYear,
                            selectedMonth + 1,
                            selectedDay)
                    onDateSelected(selectedDate)
                },
                year, month, day
            )

            datePicker.setOnCancelListener { onDismiss() }

            datePicker.show()
        }
    }
}