package com.kelasxi.aplikasimonitoringkelas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.kelasxi.aplikasimonitoringkelas.data.model.AssignmentSubmission
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolButton
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolTextField
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Spacing
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.utils.GradeUtils
import com.kelasxi.aplikasimonitoringkelas.viewmodel.AssignmentViewModel
import com.kelasxi.aplikasimonitoringkelas.viewmodel.GradeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputNilaiScreen(
    assignmentId: Int,
    onNavigateBack: () -> Unit,
    assignmentViewModel: AssignmentViewModel = viewModel(),
    gradeViewModel: GradeViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()

    val assignment by assignmentViewModel.selectedAssignment
    val submissions by assignmentViewModel.submissions
    val isLoading by assignmentViewModel.isLoading
    val isSubmitting by gradeViewModel.isSubmitting
    val errorMessage by gradeViewModel.errorMessage
    val successMessage by gradeViewModel.successMessage

    var selectedSubmission by remember { mutableStateOf<AssignmentSubmission?>(null) }
    var showGradeDialog by remember { mutableStateOf(false) }

    // Load assignment detail and submissions
    LaunchedEffect(assignmentId) {
        token?.let { tkn ->
            assignmentViewModel.loadAssignmentDetail(tkn, assignmentId)
            assignmentViewModel.loadSubmissions(tkn, assignmentId)
        }
    }

    // Handle messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            gradeViewModel.clearError()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            gradeViewModel.clearSuccess()
            showGradeDialog = false
            // Reload submissions
            token?.let { assignmentViewModel.loadSubmissions(it, assignmentId) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Pengumpulan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Assignment Info Header
                assignment?.let { tugas ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.md)
                        ) {
                            Text(
                                text = tugas.judul,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${tugas.mata_pelajaran} - Kelas ${tugas.kelas}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Text(
                                text = "Total Pengumpulan: ${submissions.size} siswa",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Submissions list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    if (submissions.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Spacing.lg),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Assignment,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(Spacing.sm))
                                    Text(
                                        text = "Belum ada pengumpulan",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(submissions) { submission ->
                            SubmissionCard(
                                submission = submission,
                                onGrade = {
                                    selectedSubmission = submission
                                    showGradeDialog = true
                                }
                            )
                        }
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(Spacing.md))
                    }
                }
            }
        }
    }

    // Grade Input Dialog
    if (showGradeDialog && selectedSubmission != null) {
        var nilai by remember { mutableStateOf(selectedSubmission?.grade?.nilai?.toString() ?: "") }
        var catatan by remember { mutableStateOf(selectedSubmission?.grade?.catatan ?: "") }

        AlertDialog(
            onDismissRequest = { if (!isSubmitting) showGradeDialog = false },
            title = {
                Column {
                    Text("Input Nilai")
                    Text(
                        text = selectedSubmission?.siswa?.name ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            text = {
                Column {
                    SchoolTextField(
                        value = nilai,
                        onValueChange = { nilai = it },
                        label = "Nilai (0-${assignment?.bobot ?: 100})",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    SchoolTextField(
                        value = catatan,
                        onValueChange = { catatan = it },
                        label = "Catatan (Opsional)",
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val nilaiDouble = nilai.toDoubleOrNull()
                        if (nilaiDouble == null) {
                            Toast.makeText(context, "Nilai harus berupa angka", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Feature deprecated: Assignment and Grade features have been removed from the system
                        Toast.makeText(
                            context,
                            "Fitur penilaian tugas sudah tidak tersedia",
                            Toast.LENGTH_SHORT
                        ).show()
                        showGradeDialog = false
                        
                        /* Original code - deprecated:
                        token?.let { tkn ->
                            selectedSubmission?.let { sub ->
                                if (sub.grade != null) {
                                    // Update existing grade
                                    gradeViewModel.updateGrade(
                                        tkn,
                                        sub.grade.id,
                                        nilaiDouble,
                                        catatan.ifBlank { null }
                                    )
                                } else {
                                    // Create new grade
                                    gradeViewModel.createGrade(
                                        tkn,
                                        sub.siswa_id,
                                        assignmentId,
                                        nilaiDouble,
                                        catatan.ifBlank { null }
                                    )
                                }
                            }
                        }
                        */
                    },
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Simpan")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showGradeDialog = false },
                    enabled = !isSubmitting
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun SubmissionCard(
    submission: AssignmentSubmission,
    onGrade: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = submission.siswa?.name ?: "Unknown Student",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(Spacing.xs))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = formatDeadline(submission.submitted_at),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (submission.status == "late") {
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = "TERLAMBAT",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                // Grade display or input button
                if (submission.grade != null) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = getGradeColor(GradeUtils.numberToGradeLetter(submission.grade.nilai)),
                        modifier = Modifier.size(60.dp),
                        onClick = onGrade
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = GradeUtils.numberToGradeLetter(submission.grade.nilai),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = submission.grade.nilai.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = onGrade,
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text("Nilai")
                    }
                }
            }

            // File info
            if (!submission.file_path.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Divider()
                Spacer(modifier = Modifier.height(Spacing.sm))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        text = "File terlampir",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Grade comment
            if (submission.grade != null && !submission.grade.catatan.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = "Catatan: ${submission.grade.catatan}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


