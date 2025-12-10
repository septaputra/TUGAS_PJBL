package com.kelasxi.aplikasimonitoringkelas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalPage(userRole: String = "Siswa", viewModel: ScheduleViewModel = viewModel()) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    
    var selectedHari by remember { mutableStateOf("") }
    var selectedKelas by remember { mutableStateOf("") }
    var isHariDropdownExpanded by remember { mutableStateOf(false) }
    var isKelasDropdownExpanded by remember { mutableStateOf(false) }
    
    val schedules by viewModel.schedules
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    
    // Load all schedules initially
    LaunchedEffect(Unit) {
        token?.let { viewModel.loadSchedules(it) }
    }
    
    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            // In a real app, you might want to show this in a more user-friendly way
            println("Error: $message")
            viewModel.clearError()
        }
    }
    
    // Filter schedules based on selected day and class
    val filteredSchedules = remember(schedules, selectedHari, selectedKelas) {
        schedules.filter { schedule ->
            (selectedHari.isEmpty() || schedule.hari == selectedHari) &&
            (selectedKelas.isEmpty() || schedule.kelas == selectedKelas)
        }
    }
    
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
    val kelasList = listOf("X RPL", "XI RPL", "XII RPL")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // User Info
        Text(
            text = "Selamat Datang, $userRole",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Spinner Hari
        ExposedDropdownMenuBox(
            expanded = isHariDropdownExpanded,
            onExpandedChange = { isHariDropdownExpanded = !isHariDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedHari,
                onValueChange = { },
                readOnly = true,
                label = { Text("Pilih Hari") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isHariDropdownExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isHariDropdownExpanded,
                onDismissRequest = { isHariDropdownExpanded = false }
            ) {
                hariList.forEach { hari ->
                    DropdownMenuItem(
                        text = { Text(hari) },
                        onClick = {
                            selectedHari = hari
                            isHariDropdownExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Spinner Kelas
        ExposedDropdownMenuBox(
            expanded = isKelasDropdownExpanded,
            onExpandedChange = { isKelasDropdownExpanded = !isKelasDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedKelas,
                onValueChange = { },
                readOnly = true,
                label = { Text("Pilih Kelas") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isKelasDropdownExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isKelasDropdownExpanded,
                onDismissRequest = { isKelasDropdownExpanded = false }
            ) {
                kelasList.forEach { kelas ->
                    DropdownMenuItem(
                        text = { Text(kelas) },
                        onClick = {
                            selectedKelas = kelas
                            isKelasDropdownExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Jadwal Cards
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: $errorMessage",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            filteredSchedules.isNotEmpty() -> {
                Text(
                    text = if (selectedHari.isNotEmpty() && selectedKelas.isNotEmpty()) {
                        "Jadwal $selectedHari - $selectedKelas"
                    } else if (selectedHari.isNotEmpty()) {
                        "Jadwal $selectedHari"
                    } else if (selectedKelas.isNotEmpty()) {
                        "Jadwal $selectedKelas"
                    } else {
                        "Semua Jadwal"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSchedules) { schedule ->
                        JadwalCard(schedule = schedule)
                    }
                }
            }
            
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (token != null) "Tidak ada jadwal tersedia" else "Silakan login terlebih dahulu",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun JadwalCard(schedule: Schedule) {
    val isBreakTime = schedule.mata_pelajaran.contains("Istirahat", ignoreCase = true) ||
                     schedule.mata_pelajaran.contains("Break", ignoreCase = true) ||
                     schedule.mata_pelajaran.isBlank()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBreakTime) 
                MaterialTheme.colorScheme.surfaceVariant 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = if (isBreakTime) "Jam ${schedule.jam_mulai} - ${schedule.jam_selesai} - Istirahat" else "Jam ${schedule.jam_mulai} - ${schedule.jam_selesai}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isBreakTime) 
                    MaterialTheme.colorScheme.onSurfaceVariant
                else 
                    MaterialTheme.colorScheme.primary
            )
            
            if (!isBreakTime) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = schedule.mata_pelajaran,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                if (schedule.guru.name.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${schedule.guru.name}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JadwalPagePreview() {
    AplikasiMonitoringKelasTheme {
        JadwalPage("Siswa")
    }
}