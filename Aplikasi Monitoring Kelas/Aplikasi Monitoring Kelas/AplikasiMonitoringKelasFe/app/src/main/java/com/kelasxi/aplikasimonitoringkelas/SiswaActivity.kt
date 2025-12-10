package com.kelasxi.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*
import com.kelasxi.aplikasimonitoringkelas.ui.components.*
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.*
import kotlinx.coroutines.launch

// Helper functions
private fun getCurrentDay(): String {
    val days = arrayOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    val calendar = java.util.Calendar.getInstance()
    return days[calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1]
}

private fun formatDateToIndonesian(dateString: String): String {
    try {
        // Parse ISO 8601 format: 2025-10-15T00:00:00.000000Z
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", java.util.Locale.getDefault())
        inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString)
        
        // Format to Indonesian date format
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val month = calendar.get(java.util.Calendar.MONTH)
        val year = calendar.get(java.util.Calendar.YEAR)
        
        val monthNames = arrayOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )
        
        return "$day ${monthNames[month]} $year"
    } catch (e: Exception) {
        // Fallback: try to parse simple date format
        try {
            val fallbackFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val date = fallbackFormat.parse(dateString)
            val calendar = java.util.Calendar.getInstance()
            calendar.time = date
            
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
            val month = calendar.get(java.util.Calendar.MONTH)
            val year = calendar.get(java.util.Calendar.YEAR)
            
            val monthNames = arrayOf(
                "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"
            )
            
            return "$day ${monthNames[month]} $year"
        } catch (e2: Exception) {
            return dateString
        }
    }
}

private fun formatStatusDisplay(status: String): String {
    return when (status.lowercase()) {
        "hadir" -> "Hadir"
        "telat" -> "Telat"
        "tidak_hadir" -> "Tidak Hadir"
        else -> status.capitalize()
    }
}

private fun getSubjectStatus(schedule: Schedule): SubjectStatus {
    // Simple logic to determine subject status
    val currentTime = java.util.Calendar.getInstance()
    val currentHour = currentTime.get(java.util.Calendar.HOUR_OF_DAY)
    val currentMinute = currentTime.get(java.util.Calendar.MINUTE)
    val currentTimeMinutes = currentHour * 60 + currentMinute

    // Parse start time (assuming format HH:mm)
    try {
        val startParts = schedule.jam_mulai.split(":")
        val startHour = startParts[0].toInt()
        val startMinute = startParts[1].toInt()
        val startTimeMinutes = startHour * 60 + startMinute

        val endParts = schedule.jam_selesai.split(":")
        val endHour = endParts[0].toInt()
        val endMinute = endParts[1].toInt()
        val endTimeMinutes = endHour * 60 + endMinute

        return when {
            currentTimeMinutes < startTimeMinutes -> SubjectStatus.Scheduled
            currentTimeMinutes in startTimeMinutes..endTimeMinutes -> SubjectStatus.Ongoing
            else -> SubjectStatus.Completed
        }
    } catch (e: Exception) {
        return SubjectStatus.Scheduled
    }
}

class SiswaActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                SiswaScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }

    // Check if user is siswa
    LaunchedEffect(Unit) {
        val userRole = sharedPrefManager.getUserRole()
        if (userRole != "siswa") {
            Toast.makeText(context, "Akses ditolak. Anda bukan siswa.", Toast.LENGTH_LONG)
                .show()
            context.startActivity(Intent(context, MainActivity::class.java))
            if (context is ComponentActivity) {
                context.finish()
            }
        }
    }

    Scaffold(
        topBar = {
            SchoolTopBar(
                title = "Dashboard Siswa",
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        SchoolAvatar(
                            name = sharedPrefManager.getUserName() ?: "Siswa",
                            size = AvatarSize.Medium,
                            backgroundColor = SMKPrimary
                        )
                        Text(
                            text = sharedPrefManager.getUserName() ?: "Siswa",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = SMKOnSurface
                        )
                        IconButton(
                            onClick = {
                                sharedPrefManager.logout()
                                context.startActivity(
                                    Intent(
                                        context,
                                        MainActivity::class.java
                                    )
                                )
                                if (context is ComponentActivity) {
                                    context.finish()
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Logout,
                                contentDescription = "Logout",
                                tint = SMKPrimary
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            SiswaBottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "jadwal_pelajaran",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("jadwal_pelajaran") {
                JadwalPage(userRole = "Siswa")
            }
            composable("entri") {
                EntriPage()
            }
            composable("list") {
                ListPage()
            }
        }
    }
}

@Composable
fun SiswaBottomNavigation(navController: NavController) {
    val items = listOf(
        Triple("jadwal_pelajaran", "Jadwal", Icons.Default.Schedule),
        Triple("entri", "Entri Guru", Icons.Default.Add),
        Triple("list", "Laporan", Icons.Default.List)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = SMKSurface,
        contentColor = SMKPrimary,
        tonalElevation = Elevation.small
    ) {
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        icon,
                        contentDescription = label,
                        modifier = Modifier.size(Dimensions.iconSizeMedium)
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = SchoolTypography.navigationLabel,
                        fontWeight = if (currentRoute == route) FontWeight.Bold else FontWeight.Medium
                    )
                },
                selected = currentRoute == route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SMKPrimary,
                    selectedTextColor = SMKPrimary,
                    unselectedIconColor = NeutralGray500,
                    unselectedTextColor = NeutralGray500,
                    indicatorColor = SMKPrimaryContainer
                ),
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalPage(userRole: String) {
    val context = LocalContext.current
    val scheduleViewModel: ScheduleViewModel = viewModel()
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()

    val schedules by scheduleViewModel.schedules
    val isLoading by scheduleViewModel.isLoading
    val errorMessage by scheduleViewModel.errorMessage

    // Load schedules for the user's class when page opens
    LaunchedEffect(Unit) {
        token?.let {
            val userClass = sharedPrefManager.getUserClass()
            if (!userClass.isNullOrEmpty()) {
                scheduleViewModel.loadSchedules(it, kelas = userClass)
            } else {
                scheduleViewModel.loadSchedules(it)
            }
        }
    }

    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            scheduleViewModel.clearError()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Subtle gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SMKPrimary.copy(alpha = 0.02f),
                            SMKSurface,
                            SMKAccent.copy(alpha = 0.01f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg)
        ) {
            // Enhanced Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Jadwal Pelajaran",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                    Text(
                        text = "Hari ini - ${getCurrentDay()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray600
                    )
                    // Show student's class if available
                    sharedPrefManager.getUserClass()?.let { kelas ->
                        Text(
                            text = "Kelas: $kelas",
                            style = MaterialTheme.typography.bodySmall,
                            color = SMKPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = SMKPrimary,
                    modifier = Modifier.size(Dimensions.iconSize)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            when {
                isLoading -> {
                    SchoolLoadingCard(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                errorMessage != null -> {
                    SchoolEmptyState(
                        title = "Terjadi Kesalahan",
                        subtitle = errorMessage ?: "Tidak dapat memuat jadwal",
                        icon = Icons.Default.Error,
                        actionText = "Coba Lagi",
                        onActionClick = {
                            token?.let {
                                val userClass = sharedPrefManager.getUserClass()
                                if (!userClass.isNullOrEmpty()) {
                                    scheduleViewModel.loadSchedules(it, kelas = userClass)
                                } else {
                                    scheduleViewModel.loadSchedules(it)
                                }
                            }
                        }
                    )
                }

                schedules.isEmpty() -> {
                    SchoolEmptyState(
                        title = "Belum Ada Jadwal",
                        subtitle = if (sharedPrefManager.getUserClass() != null) {
                            "Jadwal pelajaran belum tersedia untuk kelas ${sharedPrefManager.getUserClass()} hari ini"
                        } else {
                            "Jadwal pelajaran belum tersedia untuk hari ini"
                        },
                        icon = Icons.Default.EventBusy,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(schedules) { schedule ->
                            SchoolSubjectCard(
                                subjectName = schedule.mata_pelajaran,
                                teacherName = schedule.guru.name,
                                time = "${schedule.jam_mulai} - ${schedule.jam_selesai}",
                                status = getSubjectStatus(schedule),
                                onClick = {
                                    // Handle schedule item click if needed
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// My Reports Page - Siswa dapat melihat laporan monitoring yang telah mereka buat
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReportsPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()
    
    var monitoringList by remember { mutableStateOf<List<Monitoring>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Load my reports when page opens
    LaunchedEffect(Unit) {
        if (token != null) {
            scope.launch {
                isLoading = true
                repository.getMyReports(token)
                    .onSuccess { response ->
                        monitoringList = response.data
                    }
                    .onFailure { error ->
                        errorMessage = error.message
                        Toast.makeText(context, "Gagal memuat laporan: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                isLoading = false
            }
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SMKPrimary.copy(alpha = 0.02f),
                            SMKSurface,
                            SMKAccent.copy(alpha = 0.01f)
                        )
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Laporan Saya",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                    Text(
                        text = "Riwayat monitoring yang telah dibuat",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray600
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = SMKPrimary,
                    modifier = Modifier.size(Dimensions.iconSize)
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.xl))
            
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SMKPrimary)
                    }
                }
                
                errorMessage != null -> {
                    SchoolEmptyState(
                        title = "Terjadi Kesalahan",
                        subtitle = errorMessage ?: "Tidak dapat memuat laporan",
                        icon = Icons.Default.Error,
                        actionText = "Coba Lagi",
                        onActionClick = {
                            if (token != null) {
                                scope.launch {
                                    isLoading = true
                                    repository.getMyReports(token)
                                        .onSuccess { response ->
                                            monitoringList = response.data
                                            errorMessage = null
                                        }
                                        .onFailure { error ->
                                            errorMessage = error.message
                                        }
                                    isLoading = false
                                }
                            }
                        }
                    )
                }
                
                monitoringList.isEmpty() -> {
                    SchoolEmptyState(
                        title = "Belum Ada Laporan",
                        subtitle = "Anda belum membuat laporan monitoring. Buat laporan dari menu Entri.",
                        icon = Icons.Default.EventBusy,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(monitoringList) { monitoring ->
                            MyReportCard(monitoring = monitoring)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyReportCard(monitoring: Monitoring) {
    SchoolCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Date and Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = SMKPrimary,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    Text(
                        text = monitoring.tanggal,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SMKOnSurface
                    )
                    
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = SMKSecondary,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    Text(
                        text = monitoring.jam_laporan,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = SMKSecondary
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                // Mata Pelajaran
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = NeutralGray600,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    Text(
                        text = monitoring.mata_pelajaran,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                // Keterangan
                Text(
                    text = monitoring.catatan ?: "Tidak ada catatan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeutralGray700
                )
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                // Status Badge
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    color = when (monitoring.status_hadir.lowercase()) {
                        "hadir" -> SuccessGreen.copy(alpha = 0.1f)
                        "terlambat" -> WarningYellow.copy(alpha = 0.1f)
                        else -> ErrorRed.copy(alpha = 0.1f)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = when (monitoring.status_hadir.lowercase()) {
                                "hadir" -> Icons.Default.CheckCircle
                                "terlambat" -> Icons.Default.Warning
                                else -> Icons.Default.Cancel
                            },
                            contentDescription = null,
                            tint = when (monitoring.status_hadir.lowercase()) {
                                "hadir" -> SuccessGreen
                                "terlambat" -> WarningYellow
                                else -> ErrorRed
                            },
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = monitoring.status_hadir,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = when (monitoring.status_hadir.lowercase()) {
                                "hadir" -> SuccessGreen
                                "terlambat" -> WarningYellow
                                else -> ErrorRed
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntriPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var todaySchedules by remember { mutableStateOf<List<TodayScheduleWithAttendance>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var selectedSchedule by remember { mutableStateOf<TodayScheduleWithAttendance?>(null) }
    var showEntryDialog by remember { mutableStateOf(false) }

    // Load today's schedules filtered by student's class
    LaunchedEffect(Unit) {
        if (token != null) {
            scope.launch {
                isLoading = true
                // Get the student's class from shared preferences
                val userClass = sharedPrefManager.getUserClass()
                // Set date to today for filtering
                val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date())
                repository.getAllSchedules(token, currentDate, userClass)
                    .onSuccess { response ->
                        todaySchedules = response.data
                    }
                    .onFailure { error ->
                        errorMessage = error.message
                        Toast.makeText(context, "Gagal memuat jadwal: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                isLoading = false
            }
        }
    }

    // Handle success message
    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            successMessage = null
            // Reload schedules
            if (token != null) {
                scope.launch {
                    val userClass = sharedPrefManager.getUserClass()
                    val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        .format(java.util.Date())
                    repository.getAllSchedules(token, currentDate, userClass)
                        .onSuccess { response ->
                            todaySchedules = response.data
                        }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SMKAccent.copy(alpha = 0.02f),
                            SMKSurface,
                            SMKPrimary.copy(alpha = 0.01f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg)
        ) {
            // Enhanced Header
            SchoolCard(
                variant = CardVariant.Gradient,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = SMKPrimary,
                        modifier = Modifier.size(Dimensions.iconSize)
                    )
                    Column {
                        Text(
                            text = "Entri Kehadiran Guru",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SMKOnSurface
                        )
                        Text(
                            text = "Catat kehadiran guru untuk semua jadwal pelajaran",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeutralGray600
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SMKPrimary)
                    }
                }

                errorMessage != null -> {
                    SchoolEmptyState(
                        title = "Terjadi Kesalahan",
                        subtitle = errorMessage ?: "Tidak dapat memuat jadwal",
                        icon = Icons.Default.Error,
                        actionText = "Coba Lagi",
                        onActionClick = {
                            if (token != null) {
                                scope.launch {
                                    isLoading = true
                                    val userClass = sharedPrefManager.getUserClass()
                                    val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                                        .format(java.util.Date())
                                    repository.getAllSchedules(token, currentDate, userClass)
                                        .onSuccess { response ->
                                            todaySchedules = response.data
                                            errorMessage = null
                                        }
                                        .onFailure { error ->
                                            errorMessage = error.message
                                        }
                                    isLoading = false
                                }
                            }
                        }
                    )
                }

                todaySchedules.isEmpty() -> {
                    SchoolEmptyState(
                        title = "Belum Ada Jadwal",
                        subtitle = "Tidak ada jadwal pelajaran yang tersedia",
                        icon = Icons.Default.EventBusy,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        // Group schedules by day
                        val groupedSchedules = todaySchedules.groupBy { it.schedule.hari }
                        val sortedDays = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
                        
                        sortedDays.forEach { day ->
                            groupedSchedules[day]?.let { schedulesForDay ->
                                // Day header
                                item {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = SMKPrimary.copy(alpha = 0.1f),
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(Spacing.md),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarToday,
                                                contentDescription = null,
                                                tint = SMKPrimary,
                                                modifier = Modifier.size(Dimensions.iconSizeSmall)
                                            )
                                            Text(
                                                text = day,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = SMKPrimary
                                            )
                                            Text(
                                                text = "(${schedulesForDay.size} jadwal)",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = NeutralGray600
                                            )
                                        }
                                    }
                                }
                                
                                // Schedules for this day
                                items(schedulesForDay) { scheduleItem ->
                                    TeacherAttendanceEntryCard(
                                        scheduleWithAttendance = scheduleItem,
                                        onEntryClick = {
                                            selectedSchedule = scheduleItem
                                            showEntryDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Entry Dialog
    if (showEntryDialog && selectedSchedule != null) {
        TeacherAttendanceEntryDialog(
            schedule = selectedSchedule!!.schedule,
            existingAttendance = selectedSchedule!!.attendance,
            onDismiss = {
                showEntryDialog = false
                selectedSchedule = null
            },
            onSubmit = { jamMasuk, status, keterangan ->
                if (token != null) {
                    scope.launch {
                        val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            .format(java.util.Date())
                        
                        val request = TeacherAttendanceRequest(
                            scheduleId = selectedSchedule!!.schedule.id,
                            guruId = selectedSchedule!!.schedule.guru.id,
                            tanggal = currentDate,
                            jamMasuk = jamMasuk,
                            status = status,
                            keterangan = keterangan
                        )
                        
                        repository.createTeacherAttendance(token, request)
                            .onSuccess {
                                successMessage = "Kehadiran guru berhasil dicatat"
                                // Tutup modal dan reset state
                                showEntryDialog = false
                                selectedSchedule = null
                            }
                            .onFailure { error ->
                                // Tampilkan error dan tetap buka modal
                                Toast.makeText(context, "Gagal: ${error.message}", Toast.LENGTH_LONG).show()
                                // Modal tidak ditutup agar user bisa coba lagi
                            }
                    }
                }
            }
        )
    }
}

@Composable
fun TeacherAttendanceEntryCard(
    scheduleWithAttendance: TodayScheduleWithAttendance,
    onEntryClick: () -> Unit
) {
    val schedule = scheduleWithAttendance.schedule
    val hasAttendance = scheduleWithAttendance.hasAttendance
    val status = scheduleWithAttendance.status

    SchoolCard(
        modifier = Modifier.fillMaxWidth(),
        variant = if (hasAttendance) CardVariant.Success else CardVariant.Default
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Subject and Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = SMKPrimary,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    Text(
                        text = schedule.mata_pelajaran,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                // Teacher Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = NeutralGray600,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    Text(
                        text = schedule.guru.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray700
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                // Time and Class
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = SMKSecondary,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = "${schedule.jam_mulai} - ${schedule.jam_selesai}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SMKSecondary
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Class,
                            contentDescription = null,
                            tint = NeutralGray600,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = schedule.kelas,
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray600
                        )
                    }
                }

                if (hasAttendance && scheduleWithAttendance.attendance != null) {
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    
                    // Status Badge
                    Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        color = when (status.lowercase()) {
                            "hadir" -> SuccessGreen.copy(alpha = 0.1f)
                            "telat" -> WarningYellow.copy(alpha = 0.1f)
                            else -> ErrorRed.copy(alpha = 0.1f)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Icon(
                                imageVector = when (status.lowercase()) {
                                    "hadir" -> Icons.Default.CheckCircle
                                    "telat" -> Icons.Default.Warning
                                    else -> Icons.Default.Cancel
                                },
                                contentDescription = null,
                                tint = when (status.lowercase()) {
                                    "hadir" -> SuccessGreen
                                    "telat" -> WarningYellow
                                    else -> ErrorRed
                                },
                                modifier = Modifier.size(Dimensions.iconSizeSmall)
                            )
                            Text(
                                text = "Dicatat: ${status.capitalize()} • ${scheduleWithAttendance.attendance.jamMasuk ?: "-"}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (status.lowercase()) {
                                    "hadir" -> SuccessGreen
                                    "telat" -> WarningYellow
                                    else -> ErrorRed
                                }
                            )
                        }
                    }
                }
            }

            if (!hasAttendance) {
                    SchoolButton(
                        onClick = onEntryClick,
                        text = "Catat",
                        variant = ButtonVariant.Primary,
                        leadingIcon = Icons.Default.Add
                    )
            } else {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Sudah dicatat",
                    tint = SuccessGreen,
                    modifier = Modifier.size(Dimensions.iconSize)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherAttendanceEntryDialog(
    schedule: Schedule,
    existingAttendance: TeacherAttendance?,
    onDismiss: () -> Unit,
    onSubmit: (jamMasuk: String, status: String, keterangan: String?) -> Unit
) {
    var jamMasuk by remember {
        mutableStateOf(
            existingAttendance?.jamMasuk ?: java.text.SimpleDateFormat(
                "HH:mm",
                java.util.Locale.getDefault()
            ).format(java.util.Date())
        )
    }
    var selectedStatus by remember { mutableStateOf(existingAttendance?.status ?: "hadir") }
    var keterangan by remember { mutableStateOf(existingAttendance?.keterangan ?: "") }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Catat Kehadiran Guru",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${schedule.mata_pelajaran} - ${schedule.guru.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeutralGray600
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Jam Masuk
                SchoolTextField(
                    value = jamMasuk,
                    onValueChange = { jamMasuk = it },
                    label = "Jam Masuk",
                    placeholder = "HH:mm (contoh: 08:30)",
                    leadingIcon = Icons.Default.Schedule,
                    modifier = Modifier.fillMaxWidth()
                )

                // Status Dropdown
                SchoolDropdownField(
                    value = when (selectedStatus) {
                        "hadir" -> "Hadir"
                        "telat" -> "Telat"
                        "tidak_hadir" -> "Tidak Hadir"
                        else -> selectedStatus.capitalize()
                    },
                    onValueChange = { 
                        selectedStatus = when (it) {
                            "Hadir" -> "hadir"
                            "Telat" -> "telat"
                            "Tidak Hadir" -> "tidak_hadir"
                            else -> it.lowercase()
                        }
                    },
                    options = listOf("Hadir", "Telat", "Tidak Hadir"),
                    label = "Status Kehadiran",
                    leadingIcon = Icons.Default.Check,
                    modifier = Modifier.fillMaxWidth()
                )

                // Keterangan
                SchoolTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    label = "Keterangan (Opsional)",
                    placeholder = "Tambahkan keterangan jika diperlukan",
                    leadingIcon = Icons.Default.Notes,
                    singleLine = false,
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            SchoolButton(
                onClick = {
                    if (jamMasuk.isNotEmpty() && !isSubmitting) {
                        isSubmitting = true
                        onSubmit(jamMasuk, selectedStatus, keterangan.ifBlank { null })
                    }
                },
                text = if (isSubmitting) "Menyimpan..." else "Simpan",
                variant = ButtonVariant.Primary,
                enabled = jamMasuk.isNotEmpty() && !isSubmitting
            )
        },
        dismissButton = {
            SchoolButton(
                onClick = onDismiss,
                text = "Batal",
                variant = ButtonVariant.Secondary,
                enabled = !isSubmitting
            )
        }
    )
}

@Composable
fun ListPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var attendanceList by remember { mutableStateOf<List<TeacherAttendance>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedFilter by remember { mutableStateOf("all") } // all, hadir, telat, tidak_hadir
    var currentPage by remember { mutableStateOf(1) }

    // Function to load attendance data
    fun loadAttendanceData() {
        if (token != null) {
            scope.launch {
                isLoading = true
                val filterStatus = if (selectedFilter == "all") null else selectedFilter
                repository.getTeacherAttendances(
                    token = token,
                    status = filterStatus,
                    page = currentPage,
                    perPage = 20
                )
                    .onSuccess { response ->
                        attendanceList = response.data
                    }
                    .onFailure { error ->
                        errorMessage = error.message
                        Toast.makeText(context, "Gagal memuat data: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                isLoading = false
            }
        }
    }

    // Load data when page opens or filter changes
    LaunchedEffect(selectedFilter) {
        loadAttendanceData()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SMKSuccess.copy(alpha = 0.02f),
                            SMKSurface,
                            SMKInfo.copy(alpha = 0.01f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg)
        ) {
            // Enhanced Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Laporan Kehadiran Guru",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                    Text(
                        text = "Riwayat kehadiran guru yang telah dicatat",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray600
                    )
                }

                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = SMKPrimary,
                    modifier = Modifier.size(Dimensions.iconSize)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                FilterChip(
                    selected = selectedFilter == "all",
                    onClick = { selectedFilter = "all" },
                    label = { Text("Semua") },
                    leadingIcon = {
                        if (selectedFilter == "all") {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(Dimensions.iconSizeSmall)
                            )
                        }
                    }
                )
                FilterChip(
                    selected = selectedFilter == "hadir",
                    onClick = { selectedFilter = "hadir" },
                    label = { Text("Hadir") },
                    leadingIcon = {
                        if (selectedFilter == "hadir") {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(Dimensions.iconSizeSmall)
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SuccessGreen.copy(alpha = 0.2f),
                        selectedLabelColor = SuccessGreen
                    )
                )
                FilterChip(
                    selected = selectedFilter == "telat",
                    onClick = { selectedFilter = "telat" },
                    label = { Text("Telat") },
                    leadingIcon = {
                        if (selectedFilter == "telat") {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(Dimensions.iconSizeSmall)
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WarningYellow.copy(alpha = 0.2f),
                        selectedLabelColor = WarningYellow
                    )
                )
                FilterChip(
                    selected = selectedFilter == "tidak_hadir",
                    onClick = { selectedFilter = "tidak_hadir" },
                    label = { Text("Tidak Hadir") },
                    leadingIcon = {
                        if (selectedFilter == "tidak_hadir") {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null,
                                modifier = Modifier.size(Dimensions.iconSizeSmall)
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ErrorRed.copy(alpha = 0.2f),
                        selectedLabelColor = ErrorRed
                    )
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            when {
                isLoading -> {
                    SchoolLoadingCard(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                errorMessage != null -> {
                    SchoolEmptyState(
                        title = "Terjadi Kesalahan",
                        subtitle = errorMessage ?: "Tidak dapat memuat laporan kehadiran",
                        icon = Icons.Default.Error,
                        actionText = "Coba Lagi",
                        onActionClick = { loadAttendanceData() }
                    )
                }

                attendanceList.isEmpty() -> {
                    SchoolEmptyState(
                        title = "Belum Ada Data",
                        subtitle = if (selectedFilter != "all") {
                            "Tidak ada data kehadiran dengan status ${selectedFilter.replace("_", " ")}"
                        } else {
                            "Belum ada data kehadiran guru yang dicatat"
                        },
                        icon = Icons.Default.HistoryEdu,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(attendanceList) { attendance ->
                            TeacherAttendanceListCard(
                                attendance = attendance,
                                repository = repository,
                                token = token,
                                onReplaced = { loadAttendanceData() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherAttendanceListCard(
    attendance: TeacherAttendance,
    repository: AppRepositoryNew,
    token: String?,
    onReplaced: () -> Unit
) {
    SchoolCard(
        variant = CardVariant.Default,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Date and Time in a more organized layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Date
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = SMKPrimary,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = formatDateToIndonesian(attendance.tanggal),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SMKOnSurface
                        )
                    }
                    
                    // Time
                    attendance.jamMasuk?.let { jamMasuk ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = SMKSecondary,
                                modifier = Modifier.size(Dimensions.iconSizeSmall)
                            )
                            Text(
                                text = jamMasuk,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = SMKSecondary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                // Mata Pelajaran and Class
                attendance.schedule?.let { schedule ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = NeutralGray600,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = schedule.mata_pelajaran,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SMKOnSurface
                        )
                        Text(
                            text = "• ${schedule.kelas}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeutralGray600
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.xs))
                }
                
                // Teacher Name
                attendance.guru?.let { guru ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = NeutralGray600,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = guru.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeutralGray700
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.xs))
                }
                
                // Created By (who recorded the attendance)
                attendance.createdBy?.let { creator ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = SMKSecondary,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = "Dicatat oleh: ${creator.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray600
                        )
                    }
                }
                
                // Keterangan if available
                attendance.keterangan?.let { keterangan ->
                    if (keterangan.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = keterangan,
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray600,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                // Status Badge
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    color = when (attendance.status.lowercase()) {
                        "hadir" -> SuccessGreen.copy(alpha = 0.1f)
                        "telat" -> WarningYellow.copy(alpha = 0.1f)
                        else -> ErrorRed.copy(alpha = 0.1f)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = when (attendance.status.lowercase()) {
                                "hadir" -> Icons.Default.CheckCircle
                                "telat" -> Icons.Default.Warning
                                else -> Icons.Default.Cancel
                            },
                            contentDescription = null,
                            tint = when (attendance.status.lowercase()) {
                                "hadir" -> SuccessGreen
                                "telat" -> WarningYellow
                                else -> ErrorRed
                            },
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = attendance.status.capitalize(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = when (attendance.status.lowercase()) {
                                "hadir" -> SuccessGreen
                                "telat" -> WarningYellow
                                else -> ErrorRed
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.width(Spacing.md))

                // Show 'Gantikan' button when status is tidak_hadir
                if (attendance.status.lowercase() == "tidak_hadir" || attendance.status.lowercase() == "tidak-hadir" || attendance.status.lowercase() == "tidak hadir") {
                    var showDialog by remember { mutableStateOf(false) }

                    if (showDialog) {
                        ReplacementDialog(
                            attendance = attendance,
                            repository = repository,
                            token = token,
                            onDismiss = { showDialog = false },
                            onSuccess = {
                                showDialog = false
                                onReplaced()
                            }
                        )
                    }

                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SMKPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null)
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(text = "Gantikan", color = SMKOnPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun ReplacementDialog(
    attendance: TeacherAttendance,
    repository: AppRepositoryNew,
    token: String?,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var availableCandidates by remember { mutableStateOf<List<CandidateItem>>(emptyList()) }
    var selectedCandidate by remember { mutableStateOf<CandidateItem?>(null) }
    var keterangan by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (token != null) {
            isLoading = true
            repository.getReplacementForm(token, attendance.id)
                .onSuccess { resp ->
                    val data = resp.data
                    if (data != null) {
                        availableCandidates = data.candidates.available
                    }
                }
                .onFailure { err ->
                    errorMessage = err.message
                }
            isLoading = false
        } else {
            errorMessage = "Token tidak tersedia"
            isLoading = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                // Validation
                if (selectedCandidate == null) {
                    Toast.makeText(context, "Pilih guru pengganti terlebih dahulu", Toast.LENGTH_LONG).show()
                    return@TextButton
                }

                if (token != null) {
                    scope.launch {
                        val req = AssignReplacementRequest(
                            attendance_id = attendance.id,
                            guru_pengganti_id = selectedCandidate!!.id,
                            keterangan = if (keterangan.isBlank()) null else keterangan
                        )
                        val result = repository.replaceTeacher(token, attendance.id, req)
                        result.onSuccess { apiResp ->
                            Toast.makeText(context, apiResp.message, Toast.LENGTH_LONG).show()
                            onSuccess()
                        }.onFailure { err ->
                            Toast.makeText(context, "Gagal: ${err.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }) {
                Text(text = "Simpan Penggantian")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "Batal") }
        },
        title = { Text(text = "Form Penggantian Guru") },
        text = {
            Column {
                if (isLoading) {
                    Text(text = "Memuat daftar guru...")
                } else if (errorMessage != null) {
                    Text(text = "Error: $errorMessage")
                } else {
                    Text(text = "Sesi: ${attendance.schedule?.mata_pelajaran ?: "-"} • ${attendance.tanggal}")
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Guru Asli: ${attendance.guru?.name ?: "-"}")
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Pilih Guru Pengganti:")
                    Spacer(modifier = Modifier.height(4.dp))

                    // Simple list of available candidates
                    Column(modifier = Modifier.heightIn(max = 200.dp)) {
                        if (availableCandidates.isEmpty()) {
                            Text(text = "Tidak ada kandidat tersedia")
                        }
                        availableCandidates.forEach { cand ->
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedCandidate = cand }
                            ) {
                                RadioButton(selected = selectedCandidate?.id == cand.id, onClick = { selectedCandidate = cand })
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = cand.name)
                                    cand.mata_pelajaran?.let { Text(text = it, style = MaterialTheme.typography.bodySmall) }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = keterangan, onValueChange = { keterangan = it }, label = { Text("Catatan Penggantian (opsional)") })
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SiswaScreenPreview() {
    AplikasiMonitoringKelasTheme {
        SiswaScreen()
    }
}