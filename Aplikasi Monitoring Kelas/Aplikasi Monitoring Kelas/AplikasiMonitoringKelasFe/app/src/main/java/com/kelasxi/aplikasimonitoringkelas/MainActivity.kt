package com.kelasxi.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolButton
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolCard
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolTextField
import com.kelasxi.aplikasimonitoringkelas.ui.components.ButtonVariant
import com.kelasxi.aplikasimonitoringkelas.ui.components.CardVariant
import com.kelasxi.aplikasimonitoringkelas.viewmodel.AuthViewModel
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(modifier: Modifier = Modifier, viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    
    val isLoading by viewModel.isLoading
    val loginSuccess by viewModel.loginSuccess
    val errorMessage by viewModel.errorMessage
    val user by viewModel.user
    val token by viewModel.token
    
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    
    // Handle login success
    LaunchedEffect(loginSuccess) {
        if (loginSuccess && user != null && token != null) {
            // Simpan data login
            sharedPrefManager.saveLoginData(
                token = token!!,
                userId = user!!.id,
                name = user!!.name,
                email = user!!.email,
                role = user!!.role,
                userClass = user!!.kelas
            )
            
            // Navigate berdasarkan role dari database
            val intent = when (user!!.role.lowercase()) {
                "siswa" -> Intent(context, SiswaActivity::class.java)
                "guru", "kurikulum" -> Intent(context, KurikulumActivity::class.java)
                "admin" -> Intent(context, AdminActivity::class.java)
                "kepala_sekolah" -> Intent(context, KepalaSekolahActivity::class.java)
                else -> {
                    Toast.makeText(context, "Role tidak dikenali: ${user!!.role}", Toast.LENGTH_LONG).show()
                    null
                }
            }
            
            intent?.let { 
                context.startActivity(it)
                if (context is ComponentActivity) {
                    context.finish()
                }
            }
            
            // Reset state
            viewModel.resetLoginState()
        }
    }
    
    // Handle error message
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    // Animation states
    var logoVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        logoVisible = true
        delay(300)
        contentVisible = true
    }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Gradient Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SMKPrimary.copy(alpha = 0.1f),
                            SMKSurface,
                            SMKAccent.copy(alpha = 0.05f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl, vertical = Spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing.xl))
            
            // Animated School Logo
            AnimatedVisibility(
                visible = logoVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it })
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(Dimensions.logoSize)
                            .clip(CircleShape)
                            .background(SMKSurface)
                            .padding(Spacing.md)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_sekolah),
                            contentDescription = "Logo SMKN 2 BUDURAN SIDOARJO",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.lg))
                    
                    Text(
                        text = "SMKN 2 BUDURAN",
                        style = SchoolTypography.schoolName,
                        color = SMKPrimary,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "SIDOARJO",
                        style = SchoolTypography.schoolName,
                        color = SMKSecondary,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.xxxl))
            
            // Animated Content
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it })
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.widthIn(max = Dimensions.contentMaxWidth)
                ) {
                    // Welcome Text
                    Text(
                        text = "Selamat Datang",
                        style = SchoolTypography.welcomeTitle,
                        color = SMKOnSurface,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    
                    Text(
                        text = "Masuk ke Sistem Monitoring Kelas",
                        style = SchoolTypography.welcomeSubtitle,
                        color = NeutralGray600,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(Spacing.xxxl))
                    
                    // Login Form Card
                    SchoolCard(
                        variant = CardVariant.Default,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Email Field
                        SchoolTextField(
                            value = email,
                            onValueChange = { 
                                email = it
                                emailError = it.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(it).matches()
                            },
                            label = "Email",
                            placeholder = "Masukkan email Anda",
                            leadingIcon = Icons.Default.Email,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError = emailError,
                            errorMessage = if (emailError) "Format email tidak valid" else null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.lg))
                        
                        // Password Field
                        SchoolTextField(
                            value = password,
                            onValueChange = { 
                                password = it
                                passwordError = it.isNotEmpty() && it.length < 6
                            },
                            label = "Password",
                            placeholder = "Masukkan password Anda",
                            leadingIcon = Icons.Default.Lock,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            isError = passwordError,
                            errorMessage = if (passwordError) "Password minimal 6 karakter" else null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.xl))
                        
                        // Login Button
                        SchoolButton(
                            onClick = {
                                // Reset error states
                                emailError = false
                                passwordError = false
                                
                                // Validasi input
                                var hasError = false
                                
                                if (email.isEmpty()) {
                                    Toast.makeText(context, "Email harus diisi", Toast.LENGTH_SHORT).show()
                                    hasError = true
                                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                    emailError = true
                                    hasError = true
                                }
                                
                                if (password.isEmpty()) {
                                    Toast.makeText(context, "Password harus diisi", Toast.LENGTH_SHORT).show()
                                    hasError = true
                                } else if (password.length < 6) {
                                    passwordError = true
                                    hasError = true
                                }
                                
                                // Jika tidak ada error, lakukan login ke server Laravel
                                if (!hasError) {
                                    viewModel.login(email.trim(), password)
                                }
                            },
                            text = "MASUK",
                            loading = isLoading,
                            variant = ButtonVariant.Primary,
                            enabled = email.isNotEmpty() && password.isNotEmpty() && !emailError && !passwordError,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        // Status text
                        if (isLoading) {
                            Spacer(modifier = Modifier.height(Spacing.md))
                            Text(
                                text = "Memvalidasi dengan server...",
                                style = MaterialTheme.typography.bodySmall,
                                color = SMKPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.xl))
                    
                    // Test Accounts Info
                    SchoolCard(
                        variant = CardVariant.Primary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Akun untuk Testing",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SMKPrimary
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        
                        val testAccounts = listOf(
                            "Admin: admin@sekolah.com",
                            "Guru: siti.guru@sekolah.com", 
                            "Siswa: andi.siswa@sekolah.com"
                        )
                        
                        testAccounts.forEach { account ->
                            Text(
                                text = account,
                                style = MaterialTheme.typography.bodySmall,
                                color = SMKPrimary.copy(alpha = 0.8f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = "Password: password123",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = SMKPrimary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AplikasiMonitoringKelasTheme {
        LoginScreen()
    }
}