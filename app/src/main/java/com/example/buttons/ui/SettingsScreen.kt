package com.example.buttons.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.buttons.R
import com.example.buttons.data.PreferencesManager
import com.example.buttons.viewmodel.ButtonViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ButtonViewModel,
    onNavigateBack: () -> Unit
) {
    val buttonFontSize by viewModel.buttonFontSize.collectAsState()
    val wallpaperEnabled by viewModel.wallpaperEnabled.collectAsState()
    val backgroundColor by viewModel.backgroundColor.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineMedium
            )
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.button_font_size),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = buttonFontSize.roundToInt().toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Slider(
                        value = buttonFontSize,
                        onValueChange = { viewModel.setButtonFontSize(it) },
                        valueRange = PreferencesManager.MIN_BUTTON_FONT_SIZE..PreferencesManager.MAX_BUTTON_FONT_SIZE,
                        steps = (PreferencesManager.MAX_BUTTON_FONT_SIZE - PreferencesManager.MIN_BUTTON_FONT_SIZE).toInt() - 1
                    )
                    
                    Text(
                        text = stringResource(R.string.button_font_size_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_background),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.home_background_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column(
                        modifier = Modifier.selectableGroup()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = !wallpaperEnabled,
                                    onClick = { viewModel.setWallpaperEnabled(false) },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = !wallpaperEnabled,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.background_color),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = wallpaperEnabled,
                                    onClick = { viewModel.setWallpaperEnabled(true) },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = wallpaperEnabled,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.background_wallpaper),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            if (!wallpaperEnabled) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.button_color_label),
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        BackgroundColorPicker(
                            selectedColor = backgroundColor,
                            onColorSelected = { viewModel.setBackgroundColor(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BackgroundColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit
) {
    val colors = listOf(
        "#FFFFFF" to "White",
        "#F5F5F5" to "Light Gray",
        "#E3F2FD" to "Light Blue",
        "#E8F5E9" to "Light Green",
        "#FFF3E0" to "Light Orange",
        "#FCE4EC" to "Light Pink",
        "#F3E5F5" to "Light Purple",
        "#FFF9C4" to "Light Yellow",
        "#E0F7FA" to "Light Cyan",
        "#FFEBEE" to "Light Red",
        "#E8EAF6" to "Light Indigo",
        "#F1F8E9" to "Light Lime",
        "#FBE9E7" to "Light Deep Orange",
        "#EDE7F6" to "Light Deep Purple",
        "#E0F2F1" to "Light Teal",
        "#EFEBE9" to "Light Brown"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            colors.take(8).forEach { (hex, _) ->
                BackgroundColorCircle(
                    color = hex,
                    isSelected = selectedColor == hex,
                    onClick = { onColorSelected(hex) }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            colors.drop(8).forEach { (hex, _) ->
                BackgroundColorCircle(
                    color = hex,
                    isSelected = selectedColor == hex,
                    onClick = { onColorSelected(hex) }
                )
            }
        }
    }
}

@Composable
fun BackgroundColorCircle(
    color: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val parsedColor = try {
        androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(color))
    } catch (e: Exception) {
        androidx.compose.ui.graphics.Color.Gray
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(parsedColor, androidx.compose.foundation.shape.CircleShape)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Gray,
                shape = androidx.compose.foundation.shape.CircleShape
            )
            .clickable(onClick = onClick)
    )
}
