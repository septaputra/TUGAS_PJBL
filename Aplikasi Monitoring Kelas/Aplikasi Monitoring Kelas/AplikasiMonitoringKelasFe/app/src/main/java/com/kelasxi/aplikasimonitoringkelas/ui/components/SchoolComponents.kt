package com.kelasxi.aplikasimonitoringkelas.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ripple
import androidx.compose.material3.*
import androidx.compose.ui.graphics.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AnimationDuration
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Dimensions
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Spacing
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolDropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    label: String,
    leadingIcon: ImageVector? = null,
    placeholder: String = "Pilih $label",
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        // Custom dropdown trigger
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            colors = CardDefaults.cardColors(
                containerColor = if (isError) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            shape = RoundedCornerShape(Dimensions.surfaceCornerRadius)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(Dimensions.iconSizeMedium)
                            .padding(end = Spacing.md)
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (value.isEmpty()) placeholder else value,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (value.isEmpty()) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = if (value.isEmpty()) FontWeight.Normal else FontWeight.Medium
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(Dimensions.iconSizeMedium)
                        .rotate(if (expanded) 180f else 0f)
                )
            }
        }
        
        // Error message
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        
        // Dropdown options
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                animationSpec = tween(AnimationDuration.medium)
            ) + fadeIn(animationSpec = tween(AnimationDuration.medium)),
            exit = shrinkVertically(
                animationSpec = tween(AnimationDuration.medium)
            ) + fadeOut(animationSpec = tween(AnimationDuration.medium))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.xs),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.cardElevation),
                shape = RoundedCornerShape(Dimensions.surfaceCornerRadius)
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.xs)
                ) {
                    options.forEach { option ->
                        DropdownOption(
                            text = option,
                            isSelected = option == value,
                            onClick = {
                                onValueChange(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DropdownOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Spacing.sm))
            .clickable { onClick() }
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(Dimensions.iconSizeSmall)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    supportingText: String? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { 
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                ) 
            },
            placeholder = { 
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = NeutralGray400
                ) 
            },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (isError) SMKError else SMKPrimary,
                        modifier = Modifier.size(Dimensions.iconSizeMedium)
                    )
                }
            },
            trailingIcon = trailingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (isError) SMKError else NeutralGray500,
                        modifier = Modifier.size(Dimensions.iconSizeMedium)
                    )
                }
            },
            supportingText = if (isError && errorMessage != null) {
                {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = SMKError
                    )
                }
            } else if (supportingText != null) {
                {
                    Text(
                        text = supportingText,
                        style = MaterialTheme.typography.bodySmall,
                        color = NeutralGray500
                    )
                }
            } else null,
            isError = isError,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            shape = RoundedCornerShape(Dimensions.surfaceCornerRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SMKPrimary,
                unfocusedBorderColor = NeutralGray300,
                errorBorderColor = SMKError,
                focusedLabelColor = SMKPrimary,
                unfocusedLabelColor = NeutralGray500,
                cursorColor = SMKPrimary,
                errorCursorColor = SMKError,
                focusedTextColor = SMKOnSurface,
                unfocusedTextColor = SMKOnSurface,
                disabledTextColor = NeutralGray400,
                errorTextColor = SMKError,
                unfocusedContainerColor = SMKSurface,
                focusedContainerColor = SMKSurface,
                disabledContainerColor = NeutralGray200
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    variant: ButtonVariant = ButtonVariant.Primary
) {
    when (variant) {
        ButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                enabled = enabled && !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SMKPrimary,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    disabledContainerColor = NeutralGray300,
                    disabledContentColor = NeutralGray500
                ),
                shape = RoundedCornerShape(Dimensions.surfaceCornerRadius),
                modifier = modifier.height(Dimensions.buttonHeight)
            ) {
                ButtonContent(text = text, loading = loading, leadingIcon = leadingIcon)
            }
        }
        
        ButtonVariant.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled && !loading,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = SMKPrimary,
                    disabledContentColor = NeutralGray500
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = SMKPrimary
                ),
                shape = RoundedCornerShape(Dimensions.surfaceCornerRadius),
                modifier = modifier.height(Dimensions.buttonHeight)
            ) {
                ButtonContent(text = text, loading = loading, leadingIcon = leadingIcon)
            }
        }
        
        ButtonVariant.Success -> {
            Button(
                onClick = onClick,
                enabled = enabled && !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SMKSuccess,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    disabledContainerColor = NeutralGray300,
                    disabledContentColor = NeutralGray500
                ),
                shape = RoundedCornerShape(Dimensions.surfaceCornerRadius),
                modifier = modifier.height(Dimensions.buttonHeight)
            ) {
                ButtonContent(text = text, loading = loading, leadingIcon = leadingIcon)
            }
        }
        
        ButtonVariant.Warning -> {
            Button(
                onClick = onClick,
                enabled = enabled && !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SMKWarning,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    disabledContainerColor = NeutralGray300,
                    disabledContentColor = NeutralGray500
                ),
                shape = RoundedCornerShape(Dimensions.surfaceCornerRadius),
                modifier = modifier.height(Dimensions.buttonHeight)
            ) {
                ButtonContent(text = text, loading = loading, leadingIcon = leadingIcon)
            }
        }
        
        ButtonVariant.Danger -> {
            Button(
                onClick = onClick,
                enabled = enabled && !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SMKError,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    disabledContainerColor = NeutralGray300,
                    disabledContentColor = NeutralGray500
                ),
                shape = RoundedCornerShape(Dimensions.surfaceCornerRadius),
                modifier = modifier.height(Dimensions.buttonHeight)
            ) {
                ButtonContent(text = text, loading = loading, leadingIcon = leadingIcon)
            }
        }
        
        ButtonVariant.Ghost -> {
            TextButton(
                onClick = onClick,
                enabled = enabled && !loading,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = SMKPrimary,
                    disabledContentColor = NeutralGray500
                ),
                shape = RoundedCornerShape(Dimensions.surfaceCornerRadius),
                modifier = modifier.height(Dimensions.buttonHeight)
            ) {
                ButtonContent(text = text, loading = loading, leadingIcon = leadingIcon)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    loading: Boolean,
    leadingIcon: ImageVector?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimensions.iconSizeSmall),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
        } else if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(Dimensions.iconSizeSmall)
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
        }
        
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

enum class ButtonVariant {
    Primary,           // Solid dengan warna utama
    Secondary,         // Outline dengan border
    Success,           // Hijau untuk aksi positif  
    Warning,           // Orange untuk peringatan
    Danger,            // Merah untuk aksi berbahaya
    Ghost              // Transparant untuk aksi subtle
}

// Enhanced Card Variants
enum class CardVariant {
    Default,           // Default white card
    Primary,           // Primary color accent
    Success,           // Success green accent
    Warning,           // Warning orange accent
    Danger,            // Danger red accent
    Gradient           // Gradient background
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = Dimensions.elevationSmall),
    variant: CardVariant = CardVariant.Default,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = when (variant) {
        CardVariant.Default -> CardDefaults.cardColors(
            containerColor = SMKSurface,
            contentColor = SMKOnSurface
        )
        CardVariant.Primary -> CardDefaults.cardColors(
            containerColor = SMKPrimaryContainer,
            contentColor = SMKPrimary
        )
        CardVariant.Success -> CardDefaults.cardColors(
            containerColor = SMKSuccessContainer,
            contentColor = SMKSuccess
        )
        CardVariant.Warning -> CardDefaults.cardColors(
            containerColor = SMKWarningContainer,
            contentColor = SMKWarning
        )
        CardVariant.Danger -> CardDefaults.cardColors(
            containerColor = SMKErrorContainer,
            contentColor = SMKError
        )
        CardVariant.Gradient -> CardDefaults.cardColors(
            containerColor = SMKSurface,
            contentColor = SMKOnSurface
        )
    }
    
    val clickableModifier = if (onClick != null) {
        modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple()
        ) { onClick() }
    } else {
        modifier
    }

    Card(
        modifier = clickableModifier,
        elevation = elevation,
        colors = colors,
        shape = RoundedCornerShape(Dimensions.cardCornerRadius)
    ) {
        if (variant == CardVariant.Gradient) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                SMKPrimary.copy(alpha = 0.1f),
                                SMKAccent.copy(alpha = 0.05f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.lg),
                    content = content
                )
            }
        } else {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                content = content
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolLoadingCard(
    modifier: Modifier = Modifier
) {
    SchoolCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimensions.iconSize),
                strokeWidth = 3.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            Text(
                text = "Memuat data...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolEmptyState(
    title: String,
    subtitle: String,
    icon: ImageVector,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        
        Spacer(modifier = Modifier.height(Spacing.lg))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Spacing.sm))
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(Spacing.xl))
            SchoolButton(
                onClick = onActionClick,
                text = actionText,
                variant = ButtonVariant.Secondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolTopBar(
    title: String,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (onNavigationClick != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Kembali"
                    )
                }
            }
        },
        actions = actions ?: {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
    )
}

// Enhanced Status Badge Component
@Composable
fun SchoolStatusBadge(
    text: String,
    variant: BadgeVariant = BadgeVariant.Default,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (variant) {
        BadgeVariant.Default -> NeutralGray200 to NeutralGray700
        BadgeVariant.Success -> SMKSuccessContainer to SMKSuccess
        BadgeVariant.Warning -> SMKWarningContainer to SMKWarning
        BadgeVariant.Danger -> SMKErrorContainer to SMKError
        BadgeVariant.Info -> SMKInfoContainer to SMKInfo
        BadgeVariant.Primary -> SMKPrimaryContainer to SMKPrimary
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

enum class BadgeVariant {
    Default, Success, Warning, Danger, Info, Primary
}

// Enhanced Progress Indicator for Grades/Tasks
@Composable
fun SchoolProgressIndicator(
    progress: Float,
    label: String,
    modifier: Modifier = Modifier,
    showPercentage: Boolean = true
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = SMKOnSurface
            )
            if (showPercentage) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = SMKPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(Spacing.xs))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = SMKPrimary,
            trackColor = SMKPrimaryContainer
        )
    }
}

// Enhanced Avatar Component with Initials
@Composable
fun SchoolAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Medium,
    backgroundColor: androidx.compose.ui.graphics.Color = SMKPrimary
) {
    val initials = name.split(" ")
        .take(2)
        .joinToString("") { it.firstOrNull()?.uppercaseChar()?.toString() ?: "" }
    
    val avatarSize = when (size) {
        AvatarSize.Small -> 32.dp
        AvatarSize.Medium -> 40.dp
        AvatarSize.Large -> 56.dp
    }
    
    val textStyle = when (size) {
        AvatarSize.Small -> MaterialTheme.typography.labelMedium
        AvatarSize.Medium -> MaterialTheme.typography.titleMedium
        AvatarSize.Large -> MaterialTheme.typography.titleLarge
    }
    
    Box(
        modifier = modifier
            .size(avatarSize)
            .background(
                backgroundColor,
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = textStyle,
            color = androidx.compose.ui.graphics.Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

enum class AvatarSize {
    Small, Medium, Large
}

// Enhanced Subject Card with Color Coding
@Composable
fun SchoolSubjectCard(
    subjectName: String,
    teacherName: String,
    time: String,
    status: SubjectStatus = SubjectStatus.Scheduled,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (statusColor, statusText) = when (status) {
        SubjectStatus.Scheduled -> SMKInfo to "Terjadwal"
        SubjectStatus.Ongoing -> SMKSuccess to "Berlangsung"
        SubjectStatus.Completed -> NeutralGray500 to "Selesai"
        SubjectStatus.Cancelled -> SMKError to "Dibatalkan"
    }
    
    SchoolCard(
        modifier = modifier,
        onClick = onClick,
        variant = CardVariant.Default
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator line
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .background(statusColor, RoundedCornerShape(2.dp))
            )
            
            Spacer(modifier = Modifier.width(Spacing.md))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subjectName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SMKOnSurface
                )
                Text(
                    text = teacherName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeutralGray600
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodySmall,
                    color = NeutralGray500
                )
            }
            
            SchoolStatusBadge(
                text = statusText,
                variant = when (status) {
                    SubjectStatus.Scheduled -> BadgeVariant.Info
                    SubjectStatus.Ongoing -> BadgeVariant.Success
                    SubjectStatus.Completed -> BadgeVariant.Default
                    SubjectStatus.Cancelled -> BadgeVariant.Danger
                }
            )
        }
    }
}

enum class SubjectStatus {
    Scheduled, Ongoing, Completed, Cancelled
}

// Helper function untuk mendapatkan icon berdasarkan role
fun getRoleIcon(role: String): ImageVector {
    return when (role.lowercase()) {
        "admin", "kepala_sekolah" -> Icons.Default.School
        else -> Icons.Default.Person
    }
}