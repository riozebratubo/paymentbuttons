package com.example.buttons.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.buttons.data.ButtonEntity
import com.example.buttons.data.ButtonSize
import com.example.buttons.data.PaymentType
import com.example.buttons.viewmodel.ButtonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditButtonScreen(
    viewModel: ButtonViewModel,
    buttonId: Long?,
    onNavigateBack: () -> Unit
) {
    val buttons by viewModel.buttons.collectAsState()
    val existingButton = buttonId?.let { id -> buttons.find { it.id == id } }

    var title by remember { mutableStateOf(existingButton?.title ?: "") }
    var subtitle by remember { mutableStateOf(existingButton?.subtitle ?: "") }
    var parcels by remember { mutableStateOf(existingButton?.parcels ?: 1) }
    var paymentType by remember { mutableStateOf(existingButton?.paymentType ?: PaymentType.CREDIT) }
    var selectedColor by remember { mutableStateOf(existingButton?.color ?: "#6200EE") }
    var buttonSize by remember { mutableStateOf(existingButton?.size ?: ButtonSize.NORMAL) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingButton != null) "Edit Button" else "Add Button") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                supportingText = { Text("Main text shown on button") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("Subtitle (Optional)") },
                supportingText = { Text("Smaller text below title") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Number of Parcels: $parcels", style = MaterialTheme.typography.titleSmall)
            Slider(
                value = parcels.toFloat(),
                onValueChange = { parcels = it.toInt() },
                valueRange = 1f..12f,
                steps = 10,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Payment Type", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = paymentType == PaymentType.CREDIT,
                    onClick = { paymentType = PaymentType.CREDIT },
                    label = { Text("Credit") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = paymentType == PaymentType.DEBIT,
                    onClick = { paymentType = PaymentType.DEBIT },
                    label = { Text("Debit") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = paymentType == PaymentType.USER_CHOICE,
                    onClick = { paymentType = PaymentType.USER_CHOICE },
                    label = { Text("User Choice") },
                    modifier = Modifier.weight(1f)
                )
            }

            Text("Button Size", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = buttonSize == ButtonSize.SMALL,
                    onClick = { buttonSize = ButtonSize.SMALL },
                    label = { Text("Small") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = buttonSize == ButtonSize.NORMAL,
                    onClick = { buttonSize = ButtonSize.NORMAL },
                    label = { Text("Normal") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = buttonSize == ButtonSize.BIG,
                    onClick = { buttonSize = ButtonSize.BIG },
                    label = { Text("Big") },
                    modifier = Modifier.weight(1f)
                )
            }

            Text("Button Color", style = MaterialTheme.typography.titleSmall)
            ColorPicker(
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val button = ButtonEntity(
                            id = existingButton?.id ?: 0,
                            title = title,
                            subtitle = subtitle,
                            parcels = parcels,
                            paymentType = paymentType,
                            color = selectedColor,
                            size = buttonSize,
                            position = existingButton?.position ?: 0
                        )

                        if (existingButton != null) {
                            viewModel.updateButton(button)
                        } else {
                            viewModel.addButton(button)
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text(if (existingButton != null) "Update Button" else "Add Button")
            }
        }
    }
}

@Composable
fun ColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit
) {
    val colors = listOf(
        "#F44336" to "Red",
        "#E91E63" to "Pink",
        "#9C27B0" to "Purple",
        "#673AB7" to "Deep Purple",
        "#3F51B5" to "Indigo",
        "#2196F3" to "Blue",
        "#03A9F4" to "Light Blue",
        "#00BCD4" to "Cyan",
        "#009688" to "Teal",
        "#4CAF50" to "Green",
        "#8BC34A" to "Light Green",
        "#CDDC39" to "Lime",
        "#FFEB3B" to "Yellow",
        "#FFC107" to "Amber",
        "#FF9800" to "Orange",
        "#FF5722" to "Deep Orange"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            colors.take(8).forEach { (hex, _) ->
                ColorCircle(
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
                ColorCircle(
                    color = hex,
                    isSelected = selectedColor == hex,
                    onClick = { onColorSelected(hex) }
                )
            }
        }
    }
}

@Composable
fun ColorCircle(
    color: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val parsedColor = try {
        Color(android.graphics.Color.parseColor(color))
    } catch (e: Exception) {
        Color.Gray
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(parsedColor, CircleShape)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color.Black else Color.Gray,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    )
}
