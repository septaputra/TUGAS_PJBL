package com.kelasxi.aplikasimonitoringkelas.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolButton
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolDropdownField
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolTextField
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Spacing
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.AssignmentViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuatTugasScreen(
    onNavigateBack: () -> Unit,
    viewModel: AssignmentViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()

    val isSubmitting by viewModel.isSubmitting
    val errorMessage by viewModel.errorMessage
    val successMessage by viewModel.successMessage

    var kelas by remember { mutableStateOf("") }
    var mataPelajaran by remember { mutableStateOf("") }
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var tipe by remember { mutableStateOf("tugas") }
    var bobot by remember { mutableStateOf("100") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // File picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            selectedFileName = getFileName(context, it)
        }
    }

    // Handle messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearSuccess()
            // Reset form
            kelas = ""
            mataPelajaran = ""
            judul = ""
            deskripsi = ""
            tipe = "tugas"
            bobot = "100"
            selectedDate = null
            selectedTime = null
            selectedFileUri = null
            selectedFileName = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Tugas Baru") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Spacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            // Kelas
            SchoolDropdownField(
                value = kelas,
                onValueChange = { kelas = it },
                label = "Kelas",
                options = listOf("X", "XI", "XII"),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Mata Pelajaran
            SchoolTextField(
                value = mataPelajaran,
                onValueChange = { mataPelajaran = it },
                label = "Mata Pelajaran",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Judul
            SchoolTextField(
                value = judul,
                onValueChange = { judul = it },
                label = "Judul Tugas",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Deskripsi
            SchoolTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it },
                label = "Deskripsi",
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Tipe
            SchoolDropdownField(
                value = tipe,
                onValueChange = { tipe = it },
                label = "Tipe",
                options = listOf("tugas", "ulangan", "ujian"),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Bobot
            SchoolTextField(
                value = bobot,
                onValueChange = { bobot = it },
                label = "Bobot Nilai",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Deadline Date
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null)
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = selectedDate?.let {
                        SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date(it))
                    } ?: "Pilih Tanggal Deadline"
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Deadline Time
            OutlinedButton(
                onClick = { showTimePicker = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedDate != null
            ) {
                Icon(Icons.Default.Schedule, contentDescription = null)
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = selectedTime?.let { (hour, minute) ->
                        String.format("%02d:%02d", hour, minute)
                    } ?: "Pilih Waktu Deadline"
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // File attachment (optional)
            Button(
                onClick = { filePickerLauncher.launch("*/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Default.AttachFile, contentDescription = null)
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(if (selectedFileName.isEmpty()) "Lampirkan File (Opsional)" else selectedFileName)
            }

            if (selectedFileName.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedFileName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        selectedFileUri = null
                        selectedFileName = ""
                    }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove file",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Submit button
            SchoolButton(
                text = if (isSubmitting) "Menyimpan..." else "Buat Tugas",
                onClick = {
                    if (kelas.isBlank() || mataPelajaran.isBlank() || judul.isBlank() || 
                        deskripsi.isBlank() || bobot.isBlank() || selectedDate == null || selectedTime == null) {
                        Toast.makeText(context, "Lengkapi semua field", Toast.LENGTH_SHORT).show()
                        return@SchoolButton
                    }

                    token?.let { tkn ->
                        // Format deadline
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = selectedDate!!
                        calendar.set(Calendar.HOUR_OF_DAY, selectedTime!!.first)
                        calendar.set(Calendar.MINUTE, selectedTime!!.second)
                        calendar.set(Calendar.SECOND, 0)
                        
                        val deadline = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(calendar.time)

                        val file = selectedFileUri?.let { uri ->
                            uriToFile(context, uri, selectedFileName)
                        }

                        viewModel.createAssignment(
                            token = tkn,
                            kelas = kelas,
                            mataPelajaran = mataPelajaran,
                            judul = judul,
                            deskripsi = deskripsi,
                            deadline = deadline,
                            tipe = tipe,
                            bobot = bobot.toIntOrNull() ?: 100,
                            file = file
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting
            )
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDate = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = Pair(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Batal")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}
