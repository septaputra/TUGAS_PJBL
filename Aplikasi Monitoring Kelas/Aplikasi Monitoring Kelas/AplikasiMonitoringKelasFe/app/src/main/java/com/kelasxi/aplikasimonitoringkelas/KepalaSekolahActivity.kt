package com.kelasxi.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.CompletionHandler
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.*
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*
import com.kelasxi.aplikasimonitoringkelas.ui.components.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class KepalaSekolahActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                KepalaSekolahScreen()
            }
        }
    }
}

// Helper functions for formatting
private fun formatDate(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id", "ID"))
        date.format(formatter)
    } catch (e: Exception) {
        dateString
    }
}

private fun formatTime(timeString: String?): String {
    return try {
        if (timeString == null) return "-"
        // Handle different time formats
        val time = when {
            timeString.contains("T") -> {
                // ISO 8601 format: 2025-10-16T02:30:00.000000Z
                timeString.substringAfter("T").substringBefore(".")
            }
            timeString.length > 5 -> {
                // Format: HH:MM:SS
                timeString.substring(0, 5)
            }
            else -> timeString
        }

        // Parse and format to HH:mm
        val parsed = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"))
        parsed.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        timeString ?: "-"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepalaSekolahScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }

    // Check if user is kepala sekolah
    LaunchedEffect(Unit) {
        val userRole = sharedPrefManager.getUserRole()
        if (userRole != "kepala_sekolah") {
            Toast.makeText(context, "Akses ditolak. Anda bukan kepala sekolah.", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(context, MainActivity::class.java))
            if (context is ComponentActivity) {
                context.finish()
            }
        }
    }

    Scaffold(
        topBar = {
            SchoolTopBar(
                title = "Dashboard Kepala Sekolah",
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        SchoolAvatar(
                            name = sharedPrefManager.getUserName() ?: "Kepala Sekolah",
                            size = AvatarSize.Small,
                            backgroundColor = SMKPrimary
                        )
                        Text(
                            text = sharedPrefManager.getUserName() ?: "Kepala Sekolah",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = SMKOnSurface
                        )
                        IconButton(
                            onClick = {
                                sharedPrefManager.logout()
                                context.startActivity(Intent(context, MainActivity::class.java))
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
            KepalaSekolahBottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "jadwal",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("jadwal") {
                KepalaSekolahJadwalPage()
            }
            composable("guru_pengganti") {
                KepalaSekolahKelasKosongPage2() // Using the updated page for guru_pengganti route
            }
            composable("absensi_guru") {
                KepalaSekolahAbsensiGuruPage()
            }
        }
    }
}

@Composable
fun KepalaSekolahBottomNavigation(navController: NavController) {
    val items = listOf(
        Triple("jadwal", "Jadwal", Icons.Default.Schedule),
        Triple("guru_pengganti", "Kelas Kosong", Icons.Default.EventBusy), // Renamed and updated icon to match
        Triple("absensi_guru", "Absensi Guru", Icons.Default.List)
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

// Kelas Kosong Page - Shows empty classes from teacher_attendances
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepalaSekolahKelasKosongPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var kelasKosongList by remember { mutableStateOf<List<KelasKosong>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load kelas kosong - this gets classes that don't have teachers present based on teacher_attendances
    fun loadKelasKosong() {
        if (token != null) {
            scope.launch {
                isLoading = true
                errorMessage = null
                // Get current date from device
                val currentDate = java.time.LocalDate.now().toString() // Format: YYYY-MM-DD
                try {
                    // Fetch empty classes based on teacher attendance records
                    repository.getKelasKosong(token, currentDate)
                        .onSuccess { response ->
                            kelasKosongList = response.data ?: emptyList()
                        }
                        .onFailure { error ->
                            errorMessage = error.message
                        }
                } catch (e: Exception) {
                    errorMessage = e.message
                } finally {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadKelasKosong()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SMKBackground)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SMKPrimary)
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: $errorMessage",
                    color = SMKError,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else if (kelasKosongList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = NeutralGray400
                    )
                    Text(
                        text = "Tidak ada kelas kosong",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray600
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(kelasKosongList) { kelas ->
                    KepalaSekolahKelasKosongCard(kelasKosong = kelas)
                }
            }
        }
    }
}

// Kelas Kosong Page - Shows empty classes (kelas kosong) from teacher attendance that need attention
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepalaSekolahKelasKosongPage2() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var kelasKosongList by remember { mutableStateOf<List<KelasKosong>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<String?>(null) } // Make date optional
    
    // Load empty classes data directly from teacher attendance records
    fun loadKelasKosongFromAttendance(date: String? = null) {
        if (token != null) {
            scope.launch {
                isLoading = true
                try {
                    // Fetch all records if no date is selected, otherwise filter by date
                    repository.getKelasKosongFromAttendance(token, date)
                        .onSuccess { response ->
                            kelasKosongList = response.data ?: emptyList()
                        }
                        .onFailure { error ->
                            errorMessage = error.message
                        }
                } catch (e: Exception) {
                    errorMessage = e.message
                } finally {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadKelasKosongFromAttendance() // Load all records by default on initial load
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SMKBackground)
            .padding(Spacing.md)
    ) {
        // Date filter section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.md)
        ) {
            Text(
                text = if (selectedDate != null) "Filter Tanggal: $selectedDate" else "Semua Tanggal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SMKOnSurface
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Button(
                    onClick = {
                        selectedDate = java.time.LocalDate.now().toString()
                        loadKelasKosongFromAttendance(selectedDate)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedDate == java.time.LocalDate.now().toString()) SMKPrimary else SMKSurface,
                        contentColor = if (selectedDate == java.time.LocalDate.now().toString()) SMKOnPrimary else SMKOnSurface
                    ),
                    border = BorderStroke(if (selectedDate == java.time.LocalDate.now().toString()) 0.dp else 1.dp, SMKPrimary)
                ) {
                    Text(text = "Hari Ini")
                }
                
                IconButton(
                    onClick = {
                        // In a full implementation, you would show a date picker
                        // For now, we'll just use the current date when clicked
                        selectedDate = java.time.LocalDate.now().toString()
                        loadKelasKosongFromAttendance(selectedDate)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Pilih Tanggal",
                        tint = SMKPrimary
                    )
                }
                
                OutlinedButton(
                    onClick = {
                        selectedDate = null
                        loadKelasKosongFromAttendance(null) // Load all records
                    },
                    enabled = selectedDate != null
                ) {
                    Text(text = "Hapus Filter")
                }
            }
        }
        
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = SMKError,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Error: $errorMessage",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SMKError,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            kelasKosongList.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = NeutralGray400,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (selectedDate != null) "Tidak ada kelas kosong untuk tanggal ini" else "Tidak ada kelas kosong",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeutralGray600
                        )
                    }
                }
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items(kelasKosongList) { kelas ->
                        KepalaSekolahKelasKosongCard(kelasKosong = kelas)
                    }
                }
            }
        }
    }
}



@Composable
fun JadwalCard(
    scheduleWithAttendance: TodayScheduleWithAttendance
) {
    val schedule = scheduleWithAttendance.schedule
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SMKSurface,
            contentColor = SMKOnSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = SMKPrimary
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = schedule.hari ?: "-",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SMKOnSurface
                        )
                        Text(
                            text = schedule.kelas ?: "-",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeutralGray600
                        )
                    }
                }
                
                // Only show status badge for "Diganti", otherwise show "TIDAK ADA" if no attendance
                // Don't show any status badge for regular attendance statuses like "hadir", "telat", etc.
                if (scheduleWithAttendance.status?.lowercase() == "diganti") {
                    // Show "DIGANTI" badge when there's a teacher replacement
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF673AB7) // Purple for replacement
                    ) {
                        Text(
                            text = "DIGANTI",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs)
                        )
                    }
                } else if (!scheduleWithAttendance.hasAttendance) {
                    // Show "TIDAK ADA" badge when there's no attendance recorded (instead of "Kosong")
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NeutralGray400 // Gray for no status
                    ) {
                        Text(
                            text = "TIDAK ADA",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs)
                        )
                    }
                }
                // For "hadir", "telat", and other normal statuses, no badge is shown
            }

            Divider(
                color = NeutralGray200,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = NeutralGray50,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = SMKPrimaryContainer
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(3.dp),
                        tint = SMKPrimary
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        text = schedule.mata_pelajaran ?: "-",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = SMKOnSurface
                    )

                    Text(
                        text = "${formatTime(schedule.jam_mulai ?: "")} - ${formatTime(schedule.jam_selesai ?: "")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeutralGray600
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF1F8E9),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFC8E6C9)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(3.dp),
                        tint = Color(0xFF2E7D32)
                    )
                }
                Text(
                    text = schedule.guru?.name ?: "-",
                    style = MaterialTheme.typography.bodySmall,
                    color = SMKOnSurface
                )
            }
        }
    }
}

@Composable
fun KepalaSekolahKelasKosongCard(
    kelasKosong: KelasKosong
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        shape = RoundedCornerShape(12.dp), // Use standard dp instead of Dimensions.cornerRadiusMedium
        colors = CardDefaults.cardColors(
            containerColor = SMKSurface,
            contentColor = SMKOnSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp // Use standard dp instead of Elevation.small
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = SMKPrimary
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = formatDate(kelasKosong.tanggal ?: ""),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SMKOnSurface
                        )
                        Text(
                            text = kelasKosong.kelas ?: "-", // Use kelas instead of nama_kelas
                            style = MaterialTheme.typography.labelSmall,
                            color = NeutralGray600
                        )
                    }
                }
            }

            Divider(
                color = NeutralGray200,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = NeutralGray50,
                        shape = RoundedCornerShape(8.dp) // Use dp instead of CornerRadius.small
                    )
                    .padding(Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = SMKPrimaryContainer
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(3.dp),
                        tint = SMKPrimary
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        text = kelasKosong.mata_pelajaran ?: "-",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = SMKOnSurface
                    )

                    Text(
                        text = "${formatTime(kelasKosong.jam_mulai ?: "")} - ${formatTime(kelasKosong.jam_selesai ?: "")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeutralGray600
                    )
                }
            }

            if (!kelasKosong.keterangan.isNullOrEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(8.dp) // Use dp instead of CornerRadius.small
                        )
                        .padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFFE0B2)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(3.dp),
                            tint = Color(0xFFF57C00)
                        )
                    }
                    Text(
                        text = kelasKosong.keterangan ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = SMKOnSurface
                    )
                }
            }
        }
    }
}

@Composable
fun KepalaSekolahJadwalPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var jadwalList by remember { mutableStateOf<List<TodayScheduleWithAttendance>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load all schedules for the headmaster
    fun loadAllSchedules() {
        if (token != null) {
            scope.launch {
                isLoading = true
                errorMessage = null
                try {
                    repository.getAllSchedules(token, tanggal = null, kelas = null)
                        .onSuccess { response ->
                            jadwalList = response.data ?: emptyList()
                        }
                        .onFailure { error ->
                            errorMessage = error.message
                        }
                } catch (e: Exception) {
                    errorMessage = e.message
                } finally {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadAllSchedules()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SMKBackground)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SMKPrimary)
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: $errorMessage",
                    color = SMKError,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else if (jadwalList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = NeutralGray400
                    )
                    Text(
                        text = "Tidak ada jadwal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray600
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(jadwalList) { scheduleWithAttendance ->
                    JadwalCard(scheduleWithAttendance = scheduleWithAttendance)
                }
            }
        }
    }
}

@Composable
fun KepalaSekolahTeacherReplacementCard(
    replacement: TeacherReplacement,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SMKSurface,
            contentColor = SMKOnSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = SMKPrimary
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = formatDate(replacement.tanggal ?: ""),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SMKOnSurface
                        )
                        Text(
                            text = replacement.kelas ?: "-",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeutralGray600
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .padding(Spacing.xs),
                    shape = RoundedCornerShape(8.dp),
                    color = when (replacement.keterangan?.lowercase()) {
                        // Using keterangan field to infer status, or default to Diganti if not cancelled
                        // If there's a cancellation note, we can consider it cancelled
                        "dibatalkan", "cancelled", "canceled" -> Color(0xFFEF5350)
                        else -> Color(0xFF4CAF50) // Assume it's replaced if not cancelled
                    },
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = when (replacement.keterangan?.lowercase()) {
                            "dibatalkan", "cancelled", "canceled" -> "DIBATALKAN"
                            else -> "DIGANTI"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(
                            horizontal = Spacing.md,
                            vertical = Spacing.sm
                        )
                    )
                }
            }

            Divider(
                color = NeutralGray200,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = NeutralGray50,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = SMKPrimaryContainer
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(3.dp),
                            tint = SMKPrimary
                        )
                    }
                    Text(
                        text = "Guru Asli",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = SMKOnSurface
                    )
                }

                Text(
                    text = replacement.guru_asli?.name ?: "-",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SMKOnSurface
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.xs),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(32.dp),
                    shape = RoundedCornerShape(50),
                    color = SMKPrimaryContainer
                ) {
                    Icon(
                        Icons.Default.ArrowDownward,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp),
                        tint = SMKPrimary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF1F8E9),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFC8E6C9)
                    ) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(3.dp),
                            tint = Color(0xFF2E7D32)
                        )
                    }
                    Text(
                        text = "Guru Pengganti",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E7D32)
                    )
                }

                Text(
                    text = replacement.guru_pengganti.name ?: "-",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SMKOnSurface
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF3E5F5),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE1BEE7)
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(3.dp),
                        tint = Color(0xFF7B1FA2)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        text = replacement.mata_pelajaran ?: "-",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = SMKOnSurface
                    )

                    Text(
                        text = "${formatTime(replacement.jam_mulai ?: "")} - ${formatTime(replacement.jam_selesai ?: "")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeutralGray600
                    )
                }
            }

            if (!replacement.keterangan.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Surface(
                            modifier = Modifier.size(24.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFE0B2)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(3.dp),
                                tint = Color(0xFFF57C00)
                            )
                        }
                        Text(
                            text = "Keterangan",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE65100)
                        )
                    }

                    Text(
                        text = replacement.keterangan ?: "-",
                        style = MaterialTheme.typography.bodySmall,
                        color = SMKOnSurface
                    )
                }
            }

            Button(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF5350),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = Spacing.sm)
                )
                Text(
                    text = "Batalkan Penggantian",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepalaSekolahAbsensiGuruPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()
    
    var teacherAttendanceList by remember { mutableStateOf<List<TeacherAttendance>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<String?>(null) } // Make date optional
    var selectedStatus by remember { mutableStateOf<String?>(null) } // For status filtering
    
    // Load teacher attendance data
    fun loadTeacherAttendance(date: String? = null, status: String? = null) {
        if (token != null) {
            scope.launch {
                isLoading = true
                // Fetch all records if no date is selected, otherwise filter by date
                repository.getTeacherAttendances(
                    token = token, 
                    tanggal = date,
                    status = status
                )
                    .onSuccess { response ->
                        teacherAttendanceList = response.data
                        errorMessage = null
                    }
                    .onFailure { error ->
                        errorMessage = error.message
                        teacherAttendanceList = emptyList()
                        Toast.makeText(context, "Gagal memuat data absensi: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(Unit) { // Load all records by default on initial load
        loadTeacherAttendance()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SMKBackground)
            .padding(Spacing.md)
    ) {
        // Date and status filter section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.md)
        ) {
            Text(
                text = "Filter Data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SMKOnSurface
            )
            
            // Date filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Button(
                    onClick = {
                        selectedDate = java.time.LocalDate.now().toString()
                        loadTeacherAttendance(date = selectedDate, status = selectedStatus)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedDate == java.time.LocalDate.now().toString()) SMKPrimary else SMKSurface,
                        contentColor = if (selectedDate == java.time.LocalDate.now().toString()) SMKOnPrimary else SMKOnSurface
                    ),
                    border = BorderStroke(if (selectedDate == java.time.LocalDate.now().toString()) 0.dp else 1.dp, SMKPrimary)
                ) {
                    Text(text = "Hari Ini")
                }
                
                IconButton(
                    onClick = {
                        // In a full implementation, you would show a date picker
                        // For now, we'll just use the current date when clicked
                        selectedDate = java.time.LocalDate.now().toString()
                        loadTeacherAttendance(date = selectedDate, status = selectedStatus)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Pilih Tanggal",
                        tint = SMKPrimary
                    )
                }

                OutlinedButton(
                    onClick = {
                        selectedDate = null
                        loadTeacherAttendance(date = null, status = selectedStatus) // Load all records
                    },
                    enabled = selectedDate != null
                ) {
                    Text(text = "Hapus Filter Tanggal")
                }
            }
            
            // Status filter
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.sm)
            ) {
                Text(
                    text = "Status:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = SMKOnSurface,
                    modifier = Modifier.padding(bottom = Spacing.xs)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    OutlinedButton(
                        onClick = { 
                            selectedStatus = null
                            loadTeacherAttendance(date = selectedDate, status = null)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedStatus == null) SMKPrimary else SMKSurface,
                            contentColor = if (selectedStatus == null) SMKOnPrimary else SMKOnSurface
                        )
                    ) {
                        Text(
                            text = "Semua",
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                    
                    OutlinedButton(
                        onClick = { 
                            selectedStatus = "hadir"
                            loadTeacherAttendance(date = selectedDate, status = "hadir")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedStatus == "hadir") SuccessGreen else SMKSurface,
                            contentColor = if (selectedStatus == "hadir") Color.White else SMKOnSurface
                        )
                    ) {
                        Text(
                            text = "Hadir",
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                    
                    OutlinedButton(
                        onClick = { 
                            selectedStatus = "telat"
                            loadTeacherAttendance(date = selectedDate, status = "telat")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedStatus == "telat") WarningYellow else SMKSurface,
                            contentColor = if (selectedStatus == "telat") Color.White else SMKOnSurface
                        )
                    ) {
                        Text(
                            text = "Telat",
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                    
                    OutlinedButton(
                        onClick = { 
                            selectedStatus = "tidak_hadir"
                            loadTeacherAttendance(date = selectedDate, status = "tidak_hadir")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedStatus == "tidak_hadir") ErrorRed else SMKSurface,
                            contentColor = if (selectedStatus == "tidak_hadir") Color.White else SMKOnSurface
                        )
                    ) {
                        Text(
                            text = "Tidak\nHadir",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
            
            // Display current filters
            Text(
                text = buildString {
                    append("Filter: ")
                    if (selectedDate != null) append("Tanggal: $selectedDate")
                    if (selectedDate != null && selectedStatus != null) append(", ")
                    if (selectedStatus != null) append("Status: ${selectedStatus?.uppercase()}")
                    if (selectedDate == null && selectedStatus == null) append("Tidak ada filter")
                },
                style = MaterialTheme.typography.bodySmall,
                color = NeutralGray600,
                modifier = Modifier.padding(top = Spacing.sm)
            )
        }
        
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = SMKError,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Error: $errorMessage",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SMKError,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            teacherAttendanceList.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = NeutralGray400,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (selectedDate != null || selectedStatus != null) 
                                "Tidak ada data absensi sesuai filter" 
                                else "Tidak ada data absensi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeutralGray600
                        )
                    }
                }
            }
            
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items(teacherAttendanceList) { attendance ->
                        KepalaSekolahTeacherAttendanceCard(attendance = attendance)
                    }
                }
            }
        }
    }
}

@Composable
fun KepalaSekolahTeacherAttendanceCard(attendance: TeacherAttendance) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SMKSurface,
            contentColor = SMKOnSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Header with teacher name and class
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        // Avatar/Initials for the reported teacher
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = SMKPrimary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = attendance.guru?.name?.firstOrNull()?.toString()?.uppercase() ?: "?",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Column {
                            Text(
                                text = attendance.guru?.name ?: "Guru Tidak Diketahui",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SMKOnSurface
                            )
                            Text(
                                text = attendance.schedule?.kelas ?: attendance.schedule?.mata_pelajaran ?: "Kelas/Mapel Tidak Diketahui",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NeutralGray700
                            )
                        }
                    }
                }
                
                // Status badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when (attendance.status?.lowercase()) {
                        "hadir" -> SuccessGreen
                        "telat" -> WarningYellow
                        "tidak_hadir" -> ErrorRed
                        else -> NeutralGray300
                    }
                ) {
                    Text(
                        text = attendance.status?.uppercase() ?: "STATUS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs)
                    )
                }
            }
            
            Divider(
                color = NeutralGray200,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Schedule and time info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                            color = when (attendance.status?.lowercase()) {
                                "hadir" -> SuccessLight
                                "telat" -> WarningLight
                                "tidak_hadir" -> ErrorLight
                                else -> NeutralGray50
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                // Schedule time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = SMKPrimary
                    )
                    Column {
                        Text(
                            text = "Jam Jadwal: ${formatTime(attendance.schedule?.jam_mulai)} - ${formatTime(attendance.schedule?.jam_selesai)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray700
                        )
                        if (attendance.jamMasuk != null) {
                            Text(
                                text = "Jam Masuk: ${formatTime(attendance.jamMasuk)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = when (attendance.status?.lowercase()) {
                                    "hadir" -> SuccessGreen
                                    "telat" -> WarningYellow
                                    else -> ErrorRed
                                }
                            )
                        }
                    }
                }
                
                // Teacher name (show name if available, fallback to unknown)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Icon(
                        Icons.Default.Badge,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = SMKPrimary
                    )
                    Text(
                        text = attendance.guru?.name ?: "Guru Tidak Diketahui",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeutralGray700
                    )
                }
                
                // Date information
                if (attendance.tanggal != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = SMKPrimary
                        )
                        Text(
                            text = "Tanggal: ${formatDate(attendance.tanggal)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray700
                        )
                    }
                }
                
                // Keterangan if available
                if (!attendance.keterangan.isNullOrEmpty()) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = SMKInfo
                        )
                        Text(
                            text = "Keterangan: ${attendance.keterangan}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray700
                        )
                    }
                }
            }
        }
    }
}
