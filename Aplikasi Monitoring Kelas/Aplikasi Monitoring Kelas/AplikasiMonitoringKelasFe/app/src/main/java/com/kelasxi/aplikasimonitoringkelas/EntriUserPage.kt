package com.kelasxi.aplikasimonitoringkelas

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.CreateUserRequest
import com.kelasxi.aplikasimonitoringkelas.data.model.User
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolButton
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolCard
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolDropdownField
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolTextField
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolTopBar
import com.kelasxi.aplikasimonitoringkelas.ui.components.getRoleIcon
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Dimensions
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Spacing
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntriUserPage(viewModel: UsersViewModel = viewModel()) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()
    
    var selectedRole by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mataPelajaran by remember { mutableStateOf("") }
    var userList by remember { mutableStateOf(listOf<User>()) }
    var isRoleDropdownExpanded by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    // Load the existing users list once
    LaunchedEffect(Unit) {
        token?.let { 
            isLoading = true
            repository.getUsers(it)
                .onSuccess { response ->
                    userList = response.data
                }
                .onFailure { error ->
                    errorMessage = error.message
                }
            isLoading = false
        }
    }
    
    // Updated roles list - removed "guru", added "kurikulum"
    val roles = listOf("siswa", "kurikulum", "kepala_sekolah", "admin")
    
    Scaffold(
        topBar = {
            SchoolTopBar(
                title = "Entri User Baru"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimensions.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
        
        // Role Selection Card
        SchoolCard {
            Text(
                text = "Pilih Role User",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = Spacing.md)
            )
            
            SchoolDropdownField(
                value = when (selectedRole) {
                    "siswa" -> "Siswa"
                    "kurikulum" -> "Kurikulum"
                    "admin" -> "Admin"
                    "kepala_sekolah" -> "Kepala Sekolah"
                    else -> selectedRole
                },
                onValueChange = { 
                    selectedRole = when (it) {
                        "Siswa" -> "siswa"
                        "Kurikulum" -> "kurikulum"
                        "Admin" -> "admin"
                        "Kepala Sekolah" -> "kepala_sekolah"
                        else -> it
                    }
                },
                options = roles.map { role ->
                    when (role) {
                        "siswa" -> "Siswa"
                        "kurikulum" -> "Kurikulum"
                        "admin" -> "Admin"
                        "kepala_sekolah" -> "Kepala Sekolah"
                        else -> role
                    }
                },
                label = "Role Pengguna",
                placeholder = "Pilih role untuk pengguna baru",
                leadingIcon = getRoleIcon(selectedRole)
            )
        }

        // Personal Information Card
        SchoolCard {
            Text(
                text = "Informasi Personal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = Spacing.md)
            )
            
            SchoolTextField(
                value = nama,
                onValueChange = { nama = it },
                label = "Nama Lengkap",
                placeholder = "Masukkan nama lengkap pengguna",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            SchoolTextField(
                value = email,
                onValueChange = { 
                    email = it
                    emailError = !Patterns.EMAIL_ADDRESS.matcher(it).matches() && it.isNotEmpty()
                },
                label = "Email",
                placeholder = "contoh@sekolah.edu",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError,
                errorMessage = if (emailError) "Format email tidak valid" else null,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            SchoolTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Minimal 6 karakter",
                leadingIcon = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Conditional field for Guru - Mata Pelajaran (NOT USED NOW - no more guru role)
            // Mata pelajaran field removed as guru role is removed from system
        }
        
        // Action Buttons Card
        SchoolCard {
            SchoolButton(
                onClick = {
                    if (selectedRole.isNotEmpty() && nama.isNotEmpty() && 
                        email.isNotEmpty() && password.isNotEmpty() && !emailError) {
                        if (token != null) {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                successMessage = null
                                
                                val request = CreateUserRequest(
                                    name = nama,
                                    email = email,
                                    password = password,
                                    role = selectedRole
                                )
                                
                                repository.createUser(token, request)
                                    .onSuccess { response ->
                                        successMessage = "User berhasil ditambahkan: ${response.name}"
                                        // Clear form
                                        selectedRole = ""
                                        nama = ""
                                        email = ""
                                        password = ""
                                        mataPelajaran = ""
                                        
                                        // Reload user list
                                        repository.getUsers(token)
                                            .onSuccess { usersResponse ->
                                                userList = usersResponse.data
                                            }
                                            .onFailure { error ->
                                                errorMessage = "Gagal memuat ulang daftar user: ${error.message}"
                                            }
                                    }
                                    .onFailure { error ->
                                        errorMessage = "Gagal menambahkan user: ${error.message}"
                                    }
                                
                                isLoading = false
                            }
                        } else {
                            errorMessage = "Token tidak ditemukan, harap login terlebih dahulu"
                        }
                    }
                },
                text = "Tambah User",
                loading = isLoading,
                enabled = selectedRole.isNotEmpty() && nama.isNotEmpty() && 
                         email.isNotEmpty() && password.isNotEmpty() && !emailError && !isLoading,
                leadingIcon = Icons.Default.PersonAdd,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Success Message
            if (successMessage != null) {
                Spacer(modifier = Modifier.height(Spacing.md))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(Dimensions.surfaceCornerRadius)
                        )
                        .padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    
                    Text(
                        text = successMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            // Error Message
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(Spacing.md))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.errorContainer,
                            RoundedCornerShape(Dimensions.surfaceCornerRadius)
                        )
                        .padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
            
            // Users List Section
            SchoolCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Pengguna",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "${userList.size} pengguna",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                if (userList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.xl),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(Spacing.md))
                            Text(
                                text = "Belum ada pengguna",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        items(userList) { user ->
                            UserCard(user = user)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.elevationSmall),
        shape = RoundedCornerShape(Dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Role Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        RoundedCornerShape(Dimensions.surfaceCornerRadius)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getRoleIcon(user.role),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimensions.iconSize)
                )
            }
            
            Spacer(modifier = Modifier.width(Spacing.md))
            
            // User Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Display mata_pelajaran for guru (legacy support)
                if (user.role.lowercase() == "guru" && !user.mata_pelajaran.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    
                    Text(
                        text = "📚 ${user.mata_pelajaran}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Show banned status
                if (user.is_banned) {
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "🚫 BANNED",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                // Role Chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when (user.role.lowercase()) {
                        "admin", "kepala_sekolah" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        "kurikulum" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        else -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        text = when (user.role) {
                            "siswa" -> "Siswa"
                            "kurikulum" -> "Kurikulum"
                            "admin" -> "Admin"
                            "kepala_sekolah" -> "Kepala Sekolah"
                            else -> user.role
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = when (user.role.lowercase()) {
                            "admin", "kepala_sekolah" -> MaterialTheme.colorScheme.primary
                            "kurikulum" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.tertiary
                        },
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            label()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EntriUserPagePreview() {
    AplikasiMonitoringKelasTheme {
        EntriUserPage()
    }
}