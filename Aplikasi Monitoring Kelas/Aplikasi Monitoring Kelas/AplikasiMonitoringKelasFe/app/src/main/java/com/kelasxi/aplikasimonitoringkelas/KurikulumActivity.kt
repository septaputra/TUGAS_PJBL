package com.kelasxi.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

class KurikulumActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                KurikulumScreen()
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
fun KurikulumScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    
    // Check if user is kurikulum
    LaunchedEffect(Unit) {
        val userRole = sharedPrefManager.getUserRole()
        if (userRole != "kurikulum") {
            Toast.makeText(context, "Akses ditolak. Anda bukan kurikulum.", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(context, MainActivity::class.java))
            if (context is ComponentActivity) {
                context.finish()
            }
        }
    }
    
    Scaffold(
        topBar = {
            SchoolTopBar(
                title = "Dashboard Kurikulum",
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        SchoolAvatar(
                            name = sharedPrefManager.getUserName() ?: "Kurikulum",
                            size = AvatarSize.Small,
                            backgroundColor = SMKPrimary
                        )
                        Text(
                            text = sharedPrefManager.getUserName() ?: "Kurikulum",
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
            KurikulumBottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "kelas_kosong",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("kelas_kosong") {
                KelasKosongPage()
            }
            composable("guru_pengganti") {
                GuruPenggantiPage()
            }
        }
    }
}

@Composable
fun KurikulumBottomNavigation(navController: NavController) {
    val items = listOf(
        Triple("kelas_kosong", "Kelas Kosong", Icons.Default.EventBusy),
        Triple("guru_pengganti", "Guru Pengganti", Icons.Default.PersonAdd)
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

// Kelas Kosong Page - Shows empty classes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KelasKosongPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()
    
    var kelasKosongList by remember { mutableStateOf<List<KelasKosong>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Load kelas kosong
    fun loadKelasKosong() {
        if (token != null) {
            scope.launch {
                isLoading = true
                // Get current date from device
                val currentDate = java.time.LocalDate.now().toString() // Format: YYYY-MM-DD
                repository.getKelasKosong(token, currentDate)
                    .onSuccess { response ->
                        kelasKosongList = response.data
                        errorMessage = null
                    }
                    .onFailure { error ->
                        errorMessage = error.message
                        Toast.makeText(context, "Gagal memuat kelas kosong: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(Unit) {
        loadKelasKosong()
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
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
                        text = "Kelas Kosong",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                    Text(
                        text = "Daftar kelas yang tidak ada guru",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray600
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.EventBusy,
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
                        subtitle = errorMessage ?: "Tidak dapat memuat data",
                        icon = Icons.Default.Error,
                        actionText = "Coba Lagi",
                        onActionClick = { loadKelasKosong() }
                    )
                }
                
                kelasKosongList.isEmpty() -> {
                    SchoolEmptyState(
                        title = "Semua Kelas Terisi",
                        subtitle = "Tidak ada kelas yang kosong saat ini",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(kelasKosongList) { kelasKosong ->
                            KelasKosongCard(kelasKosong = kelasKosong)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KelasKosongCard(kelasKosong: KelasKosong) {
    SchoolCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Header with warning badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = kelasKosong.mata_pelajaran,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = "Kelas ${kelasKosong.kelas}",
                        style = MaterialTheme.typography.titleMedium,
                        color = SMKSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ErrorRed.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = "KOSONG",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            // Schedule info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hari
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = NeutralGray600,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    Text(
                        text = kelasKosong.hari,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray700,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Jam
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = NeutralGray600,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    Text(
                        text = "${formatTime(kelasKosong.jam_mulai)} - ${formatTime(kelasKosong.jam_selesai)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray700
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            // Tanggal
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = NeutralGray600,
                    modifier = Modifier.size(Dimensions.iconSizeSmall)
                )
                Text(
                    text = formatDate(kelasKosong.tanggal),
                    style = MaterialTheme.typography.bodySmall,
                    color = NeutralGray600
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            // Original teacher info
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
                    text = "Guru Seharusnya: ${kelasKosong.guru.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeutralGray700,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Keterangan if available
            kelasKosong.keterangan?.let { keterangan ->
                if (keterangan.isNotBlank()) {
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SMKInfo,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = keterangan,
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray600,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

// Guru Pengganti Page - CRUD untuk guru pengganti
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruPenggantiPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()
    
    var teacherReplacementList by remember { mutableStateOf<List<TeacherReplacement>>(emptyList()) }
    var kelasKosongList by remember { mutableStateOf<List<KelasKosong>>(emptyList()) }
    var guruList by remember { mutableStateOf<List<User>>(emptyList()) }  // Changed back to guruList for compatibility
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    
    // Form states
    var selectedKelasKosong by remember { mutableStateOf<KelasKosong?>(null) }
    var selectedGuruPengganti by remember { mutableStateOf<User?>(null) }
    var expandedKelasKosong by remember { mutableStateOf(false) }
    var expandedGuruPengganti by remember { mutableStateOf(false) }
    var keterangan by remember { mutableStateOf("") }
    
    // Load data
    fun loadData() {
        if (token != null) {
            println("DEBUG: Starting loadData function")
            scope.launch {
                isLoading = true
                // Get current date from device
                val currentDate = java.time.LocalDate.now().toString() // Format: YYYY-MM-DD
                
                // Load teacher replacements (penggantian yang sudah dilakukan)
                repository.getTeacherReplacements(token)
                    .onSuccess { response ->
                        teacherReplacementList = response.data
                        println("DEBUG: Loaded ${response.data.size} teacher replacements")
                    }
                    .onFailure { error ->
                        errorMessage = error.message
                        println("DEBUG: Failed to load teacher replacements - ${error.message}")
                    }
                    
                repository.getKelasKosong(token, currentDate)
                    .onSuccess { response ->
                        kelasKosongList = response.data
                        println("DEBUG: Loaded ${response.data.size} kelas kosong")
                    }
                    .onFailure { error ->
                        errorMessage = error.message
                        println("DEBUG: Failed to load kelas kosong - ${error.message}")
                    }
                
                // Load daftar guru from teachers table
                repository.getTeachers(token)
                    .onSuccess { response ->
                        guruList = response.data  // Updated to use teachers table
                        println("DEBUG: Successfully loaded ${response.data.size} teachers, updating state")
                        response.data.forEach { teacher ->
                            println("DEBUG: Teacher - ID: ${teacher.id}, Name: ${teacher.name}, Subject: ${teacher.mata_pelajaran}")
                        }
                    }
                    .onFailure { error ->
                        errorMessage = error.message
                        println("DEBUG: Failed to load teachers - ${error.message}")
                    }
                    
                isLoading = false
                println("DEBUG: Finished loadData function, isLoading = false")
            }
        }
    }
    
    LaunchedEffect(Unit) {
        loadData()
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Guru Pengganti",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                    Text(
                        text = "Kelola penugasan guru pengganti",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray600
                    )
                }
                
                IconButton(
                    onClick = { showCreateDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = SMKPrimary,
                        contentColor = SMKOnPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Tambah Guru Pengganti"
                    )
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
                
                teacherReplacementList.isEmpty() -> {
                    SchoolEmptyState(
                        title = "Belum Ada Penugasan",
                        subtitle = "Belum ada guru pengganti yang ditugaskan",
                        icon = Icons.Default.PersonOff,
                        actionText = "Tambah Guru Pengganti",
                        onActionClick = { showCreateDialog = true }
                    )
                }
                
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(teacherReplacementList) { replacement ->
                            TeacherReplacementCard(
                                replacement = replacement,
                                onCancel = {
                                    if (token != null) {
                                        scope.launch {
                                            isLoading = true
                                            repository.cancelReplacement(token, replacement.id)
                                                .onSuccess {
                                                    Toast.makeText(context, "Penggantian dibatalkan", Toast.LENGTH_SHORT).show()
                                                    loadData()
                                                }
                                                .onFailure { error ->
                                                    Toast.makeText(context, "Gagal: ${error.message}", Toast.LENGTH_LONG).show()
                                                }
                                            isLoading = false
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Create Dialog
    if (showCreateDialog) {
        TambahGuruPenggantiDialog(
            kelasKosongList = kelasKosongList,
            guruList = guruList,  // Changed back to guruList for compatibility
            onDismiss = { 
                showCreateDialog = false
                selectedKelasKosong = null
                selectedGuruPengganti = null
                keterangan = ""
            },
            onSubmit = { kelasKosong, guru, keteranganInput ->
                if (token != null) {
                    scope.launch {
                        isLoading = true
                        
                        val request = AssignReplacementRequest(
                            attendance_id = kelasKosong.attendance_id ?: 0,
                            guru_pengganti_id = guru.id,
                            keterangan = keteranganInput.ifBlank { null }
                        )
                        
                        repository.assignReplacement(token, request)
                            .onSuccess {
                                Toast.makeText(context, "Guru pengganti berhasil ditugaskan", Toast.LENGTH_SHORT).show()
                                showCreateDialog = false
                                selectedKelasKosong = null
                                selectedGuruPengganti = null
                                keterangan = ""
                                loadData()
                            }
                            .onFailure { error ->
                                Toast.makeText(context, "Gagal: ${error.message}", Toast.LENGTH_LONG).show()
                            }
                        
                        isLoading = false
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahGuruPenggantiDialog(
    kelasKosongList: List<KelasKosong>,
    guruList: List<User>,  // Keeping original parameter name for compatibility
    onDismiss: () -> Unit,
    onSubmit: (KelasKosong, User, String) -> Unit
) {
    var selectedKelasKosong by remember { mutableStateOf<KelasKosong?>(null) }
    var selectedGuruPengganti by remember { mutableStateOf<User?>(null) }
    var expandedKelasKosong by remember { mutableStateOf(false) }
    var expandedGuruPengganti by remember { mutableStateOf(false) }
    var keterangan by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SMKSurface,
            tonalElevation = Elevation.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.xl)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tambah Guru Pengganti",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SMKOnSurface
                        )
                        Text(
                            text = "Tugaskan guru pengganti untuk kelas kosong",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray600
                        )
                    }
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = NeutralGray600
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.lg))
                
                // Dropdown Kelas Kosong
                Text(
                    text = "Pilih Kelas Kosong",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SMKOnSurface
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                ExposedDropdownMenuBox(
                    expanded = expandedKelasKosong,
                    onExpandedChange = { expandedKelasKosong = !expandedKelasKosong }
                ) {
                    OutlinedTextField(
                        value = selectedKelasKosong?.let { 
                            "${it.kelas} - ${it.mata_pelajaran} (${it.hari}, ${formatTime(it.jam_mulai)})"
                        } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Pilih kelas yang kosong", color = NeutralGray500) },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expandedKelasKosong) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = SMKPrimary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SMKPrimary,
                            unfocusedBorderColor = SMKOutline,
                            focusedContainerColor = SMKSurface,
                            unfocusedContainerColor = SMKSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedKelasKosong,
                        onDismissRequest = { expandedKelasKosong = false }
                    ) {
                        if (kelasKosongList.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Tidak ada kelas kosong") },
                                onClick = { },
                                enabled = false
                            )
                        } else {
                            kelasKosongList.forEach { kelasKosong ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = "${kelasKosong.kelas} - ${kelasKosong.mata_pelajaran}",
                                                fontWeight = FontWeight.SemiBold,
                                                color = SMKOnSurface
                                            )
                                            Text(
                                                text = "${kelasKosong.hari}, ${formatTime(kelasKosong.jam_mulai)} - ${formatTime(kelasKosong.jam_selesai)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = NeutralGray600
                                            )
                                            Text(
                                                text = "Guru asli: ${kelasKosong.guru.name}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ErrorRed
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedKelasKosong = kelasKosong
                                        expandedKelasKosong = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                // Dropdown Guru Pengganti
                Text(
                    text = "Pilih Guru Pengganti",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SMKOnSurface
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                ExposedDropdownMenuBox(
                    expanded = expandedGuruPengganti,
                    onExpandedChange = { expandedGuruPengganti = !expandedGuruPengganti }
                ) {
                    OutlinedTextField(
                        value = selectedGuruPengganti?.let { 
                            "${it.name} (${it.mata_pelajaran ?: "Semua Mapel"})"
                        } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Pilih guru pengganti", color = NeutralGray500) },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expandedGuruPengganti) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = SMKPrimary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SMKPrimary,
                            unfocusedBorderColor = SMKOutline,
                            focusedContainerColor = SMKSurface,
                            unfocusedContainerColor = SMKSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedGuruPengganti,
                        onDismissRequest = { expandedGuruPengganti = false }
                    ) {
                        println("DEBUG: Rendering dropdown with ${guruList.size} teachers")
                        if (guruList.isEmpty()) {
                            println("DEBUG: guruList is empty, showing 'Tidak ada guru tersedia'")
                            DropdownMenuItem(
                                text = { Text("Tidak ada guru tersedia") },
                                onClick = { },
                                enabled = false
                            )
                        } else {
                            println("DEBUG: guruList has ${guruList.size} teachers, rendering options")
                            guruList.forEach { teacher ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = teacher.name,
                                                fontWeight = FontWeight.SemiBold,
                                                color = SMKOnSurface
                                            )
                                            teacher.mata_pelajaran?.let {
                                                Text(
                                                    text = "Mata Pelajaran: $it",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = NeutralGray600
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedGuruPengganti = teacher
                                        expandedGuruPengganti = false
                                        println("DEBUG: Selected teacher: ${teacher.name}")
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                // Keterangan
                Text(
                    text = "Keterangan (Opsional)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SMKOnSurface
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                OutlinedTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    placeholder = { Text("Masukkan keterangan...", color = NeutralGray500) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SMKPrimary,
                        unfocusedBorderColor = SMKOutline,
                        focusedContainerColor = SMKSurface,
                        unfocusedContainerColor = SMKSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(Spacing.xl))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SMKPrimary
                        ),
                        border = BorderStroke(1.dp, SMKPrimary)
                    ) {
                        Text("Batal", fontWeight = FontWeight.SemiBold)
                    }
                    
                    Button(
                        onClick = {
                            if (selectedKelasKosong != null && selectedGuruPengganti != null) {
                                onSubmit(selectedKelasKosong!!, selectedGuruPengganti!!, keterangan)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedKelasKosong != null && selectedGuruPengganti != null,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SMKPrimary,
                            contentColor = SMKOnPrimary,
                            disabledContainerColor = NeutralGray300,
                            disabledContentColor = NeutralGray500
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text("Simpan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherReplacementCard(
    replacement: TeacherReplacement,
    onCancel: () -> Unit
) {
    SchoolCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md)
        ) {
            // Header with class and subject
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = replacement.mata_pelajaran,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                    Text(
                        text = "Kelas ${replacement.kelas}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray700
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SuccessGreen.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = "DIGANTI",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            // Guru Asli (yang tidak hadir)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonOff,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(Dimensions.iconSizeSmall)
                )
                Text(
                    text = "Guru Asli: ${replacement.guru_asli?.name ?: "Tidak ada data"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ErrorRed,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.xs))
            
            // Guru Pengganti
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(Dimensions.iconSizeSmall)
                )
                Text(
                    text = "Guru Pengganti: ${replacement.guru_pengganti.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SuccessGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            // Schedule info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = NeutralGray600,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    Text(
                        text = formatDate(replacement.tanggal),
                        style = MaterialTheme.typography.bodySmall,
                        color = NeutralGray600
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = NeutralGray600,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    Text(
                        text = "${formatTime(replacement.jam_mulai)} - ${formatTime(replacement.jam_selesai)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeutralGray600
                    )
                }
            }
            
            // Keterangan if available
            replacement.keterangan?.let { keterangan ->
                if (keterangan.isNotBlank()) {
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SMKInfo,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = keterangan,
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray600
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            // Cancel button
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ErrorRed
                ),
                border = BorderStroke(1.dp, ErrorRed)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.iconSizeSmall)
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                Text("Batalkan Penggantian", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun GuruPenggantiCard(
    guruPengganti: GuruPengganti,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    SchoolCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = SMKPrimary,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = "Pergantian Guru",
                            style = MaterialTheme.typography.labelMedium,
                            color = SMKPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    
                                        // Original teacher
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonOff,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = "Dari: ${guruPengganti.guruAsli?.name ?: "Tidak ada data"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeutralGray700
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    
                    // Substitute teacher
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = "Ke: ${guruPengganti.guruPengganti.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SuccessGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    
                    // Date
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = NeutralGray600,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = formatDate(guruPengganti.tanggal),
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray600
                        )
                    }
                }
                
                IconButton(
                    onClick = { showDeleteDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = ErrorRed
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus"
                    )
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = ErrorRed
                )
            },
            title = { Text("Hapus Penugasan?") },
            text = { Text("Apakah Anda yakin ingin menghapus penugasan guru pengganti ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed
                    )
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun KurikulumScreenPreview() {
    AplikasiMonitoringKelasTheme {
        KurikulumScreen()
    }
}
