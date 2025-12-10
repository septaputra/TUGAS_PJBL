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
import com.kelasxi.aplikasimonitoringkelas.data.model.Assignment
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Spacing
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.AssignmentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarTugasGuruScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToSubmissions: (Int) -> Unit,
    viewModel: AssignmentViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()

    val assignments by viewModel.assignments
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    var selectedKelas by remember { mutableStateOf<String?>(null) }
    var selectedMataPelajaran by remember { mutableStateOf<String?>(null) }
    var selectedTipe by remember { mutableStateOf<String?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }

    // Load assignments
    LaunchedEffect(selectedKelas, selectedMataPelajaran, selectedTipe) {
        token?.let {
            viewModel.loadAssignments(it, selectedKelas, selectedMataPelajaran, selectedTipe)
        }
    }

    // Handle error
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Tugas") },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Buat Tugas")
            }
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
                // Active filters
                if (selectedKelas != null || selectedMataPelajaran != null || selectedTipe != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        selectedKelas?.let { kelas ->
                            FilterChip(
                                selected = true,
                                onClick = { selectedKelas = null },
                                label = { Text("Kelas $kelas") },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove filter",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                        selectedMataPelajaran?.let { mapel ->
                            FilterChip(
                                selected = true,
                                onClick = { selectedMataPelajaran = null },
                                label = { Text(mapel) },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove filter",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                        selectedTipe?.let { tipe ->
                            FilterChip(
                                selected = true,
                                onClick = { selectedTipe = null },
                                label = { Text(tipe.uppercase()) },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove filter",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                    Divider()
                }

                // Assignment list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    if (assignments.isEmpty()) {
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
                                        text = "Belum ada tugas",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(Spacing.xs))
                                    Text(
                                        text = "Tekan tombol + untuk membuat tugas baru",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(assignments) { assignment ->
                            GuruAssignmentCard(
                                assignment = assignment,
                                onClick = { onNavigateToSubmissions(assignment.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Filter Dialog
    if (showFilterDialog) {
        var tempKelas by remember { mutableStateOf(selectedKelas ?: "") }
        var tempMapel by remember { mutableStateOf(selectedMataPelajaran ?: "") }
        var tempTipe by remember { mutableStateOf(selectedTipe ?: "") }

        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter Tugas") },
            text = {
                Column {
                    // Kelas filter
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = { }
                    ) {
                        OutlinedTextField(
                            value = tempKelas,
                            onValueChange = { tempKelas = it },
                            label = { Text("Kelas") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Semua Kelas") }
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    // Mata Pelajaran filter
                    OutlinedTextField(
                        value = tempMapel,
                        onValueChange = { tempMapel = it },
                        label = { Text("Mata Pelajaran") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Semua Mata Pelajaran") }
                    )

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    // Tipe filter
                    var expandedTipe by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedTipe,
                        onExpandedChange = { expandedTipe = !expandedTipe }
                    ) {
                        OutlinedTextField(
                            value = tempTipe,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Tipe") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            placeholder = { Text("Semua Tipe") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipe)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTipe,
                            onDismissRequest = { expandedTipe = false }
                        ) {
                            listOf("tugas", "ulangan", "ujian").forEach { tipe ->
                                DropdownMenuItem(
                                    text = { Text(tipe.uppercase()) },
                                    onClick = {
                                        tempTipe = tipe
                                        expandedTipe = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    selectedKelas = if (tempKelas.isBlank()) null else tempKelas
                    selectedMataPelajaran = if (tempMapel.isBlank()) null else tempMapel
                    selectedTipe = if (tempTipe.isBlank()) null else tempTipe
                    showFilterDialog = false
                }) {
                    Text("Terapkan")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedKelas = null
                    selectedMataPelajaran = null
                    selectedTipe = null
                    showFilterDialog = false
                }) {
                    Text("Reset")
                }
            }
        )
    }
}

@Composable
fun GuruAssignmentCard(
    assignment: Assignment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
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
                        text = assignment.judul,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(Spacing.xs))

                    Text(
                        text = assignment.mata_pelajaran,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Kelas ${assignment.kelas}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when (assignment.tipe) {
                        "ujian" -> MaterialTheme.colorScheme.errorContainer
                        "ulangan" -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }
                ) {
                    Text(
                        text = assignment.tipe.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            Divider()

            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        text = formatDeadline(assignment.deadline),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Grade,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        text = "${assignment.bobot} poin",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        text = "${assignment.total_submissions ?: 0} siswa",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
