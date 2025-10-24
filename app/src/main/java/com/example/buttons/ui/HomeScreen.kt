package com.example.buttons.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buttons.R
import com.example.buttons.data.ButtonEntity
import com.example.buttons.data.ButtonSize
import com.example.buttons.data.PaymentType
import com.example.buttons.viewmodel.ButtonViewModel
import org.burnoutcrew.reorderable.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ButtonViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onEditButton: (ButtonEntity?) -> Unit
) {
    val buttons by viewModel.buttons.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val buttonFontSize by viewModel.buttonFontSize.collectAsState()
    val wallpaperEnabled by viewModel.wallpaperEnabled.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var pendingButton by remember { mutableStateOf<ButtonEntity?>(null) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        if (wallpaperEnabled) {
            Image(
                painter = painterResource(id = R.drawable.stone_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

    Scaffold(
        containerColor = if (wallpaperEnabled) Color.Transparent else MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.menu))
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(if (isEditMode) R.string.exit_edit_mode else R.string.edit_buttons)) },
                            onClick = {
                                viewModel.toggleEditMode()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    if (isEditMode) Icons.Default.Check else Icons.Default.Edit,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.settings)) },
                            onClick = {
                                showMenu = false
                                onNavigateToSettings()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Settings, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.about)) },
                            onClick = {
                                showMenu = false
                                onNavigateToAbout()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Info, contentDescription = null)
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (isEditMode) {
                FloatingActionButton(onClick = { onEditButton(null) }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_button))
                }
            }
        }
    ) { padding ->
        if (buttons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.no_buttons_yet), style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    if (isEditMode) {
                        Text(stringResource(R.string.tap_plus_to_add), style = MaterialTheme.typography.bodyMedium)
                    } else {
                        Text(stringResource(R.string.enable_edit_mode), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        } else {
            if (isEditMode) {
                EditableButtonGrid(
                    buttons = buttons,
                    viewModel = viewModel,
                    onEditButton = onEditButton,
                    modifier = Modifier.padding(padding)
                )
            } else {
                ButtonGrid(
                    buttons = buttons,
                    onButtonClick = { button ->
                        if (button.paymentType == PaymentType.USER_CHOICE) {
                            pendingButton = button
                            showPaymentDialog = true
                        } else {
                            val deeplink = buildDeeplink(button, null)
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink))
                            context.startActivity(intent)
                        }
                    },
                    fontSize = buttonFontSize,
                    modifier = Modifier.padding(padding)
                )
            }
        }
        
        if (showPaymentDialog && pendingButton != null) {
            PaymentTypeDialog(
                onDismiss = { 
                    showPaymentDialog = false
                    pendingButton = null
                },
                onSelectPaymentType = { selectedType ->
                    pendingButton?.let { button ->
                        val deeplink = buildDeeplink(button, selectedType)
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink))
                        context.startActivity(intent)
                    }
                    showPaymentDialog = false
                    pendingButton = null
                }
            )
        }
    }
    }
}

@Composable
fun ButtonGrid(
    buttons: List<ButtonEntity>,
    onButtonClick: (ButtonEntity) -> Unit,
    fontSize: Float,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(buttons) { button ->
            ButtonItem(button = button, onClick = { onButtonClick(button) }, fontSize = fontSize)
        }
    }
}

@Composable
fun EditableButtonGrid(
    buttons: List<ButtonEntity>,
    viewModel: ButtonViewModel,
    onEditButton: (ButtonEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberReorderableLazyGridState(
        onMove = { from, to ->
            viewModel.moveButton(from.index, to.index)
        }
    )

    Column(modifier = modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = stringResource(R.string.rearrange_buttons_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(12.dp)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = state.gridState,
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .reorderable(state)
        ) {
            items(buttons, key = { it.id }) { button ->
                ReorderableItem(state, key = button.id) { isDragging ->
                    EditableButtonItem(
                        button = button,
                        onEdit = { onEditButton(button) },
                        onDelete = { viewModel.deleteButton(button) },
                        isDragging = isDragging,
                        modifier = Modifier.detectReorderAfterLongPress(state)
                    )
                }
            }
        }
    }
}

@Composable
fun ButtonItem(
    button: ButtonEntity,
    onClick: () -> Unit,
    fontSize: Float,
    modifier: Modifier = Modifier
) {
    val buttonColor = try {
        Color(android.graphics.Color.parseColor(button.color))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    val height = when (button.size) {
        ButtonSize.SMALL -> 80.dp
        ButtonSize.NORMAL -> 120.dp
        ButtonSize.BIG -> 160.dp
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        contentPadding = PaddingValues(16.dp),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                if (button.amount != null) {
                    Text(
                        text = button.amount,
                        style = when (button.size) {
                            ButtonSize.SMALL -> MaterialTheme.typography.titleLarge
                            ButtonSize.NORMAL -> MaterialTheme.typography.headlineMedium
                            ButtonSize.BIG -> MaterialTheme.typography.headlineLarge
                        },
                        fontSize = (fontSize * 1.5f).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = button.title,
                    style = when (button.size) {
                        ButtonSize.SMALL -> MaterialTheme.typography.bodyMedium
                        ButtonSize.NORMAL -> MaterialTheme.typography.titleMedium
                        ButtonSize.BIG -> MaterialTheme.typography.titleLarge
                    },
                    fontSize = fontSize.sp
                )
                if (button.subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = button.subtitle,
                        style = when (button.size) {
                            ButtonSize.SMALL -> MaterialTheme.typography.bodySmall
                            ButtonSize.NORMAL -> MaterialTheme.typography.bodySmall
                            ButtonSize.BIG -> MaterialTheme.typography.bodyMedium
                        },
                        fontSize = (fontSize * 0.85f).sp
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${button.parcels}x",
                    style = when (button.size) {
                        ButtonSize.SMALL -> MaterialTheme.typography.titleSmall
                        ButtonSize.NORMAL -> MaterialTheme.typography.titleMedium
                        ButtonSize.BIG -> MaterialTheme.typography.titleLarge
                    },
                    fontSize = fontSize.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (button.paymentType) {
                        PaymentType.CREDIT -> "Credit"
                        PaymentType.DEBIT -> "Debit"
                        PaymentType.USER_CHOICE -> "User Choice"
                    },
                    style = when (button.size) {
                        ButtonSize.SMALL -> MaterialTheme.typography.bodySmall
                        ButtonSize.NORMAL -> MaterialTheme.typography.bodySmall
                        ButtonSize.BIG -> MaterialTheme.typography.bodyMedium
                    },
                    fontSize = (fontSize * 0.85f).sp
                )
            }
        }
    }
}

@Composable
fun EditableButtonItem(
    button: ButtonEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isDragging: Boolean,
    modifier: Modifier = Modifier
) {
    val buttonColor = try {
        Color(android.graphics.Color.parseColor(button.color))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    val height = when (button.size) {
        ButtonSize.SMALL -> 80.dp
        ButtonSize.NORMAL -> 120.dp
        ButtonSize.BIG -> 160.dp
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(if (isDragging) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent),
        colors = CardDefaults.cardColors(containerColor = buttonColor),
        shape = RectangleShape
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (button.amount != null) {
                        Text(
                            text = button.amount,
                            color = Color.White,
                            style = when (button.size) {
                                ButtonSize.SMALL -> MaterialTheme.typography.titleLarge
                                ButtonSize.NORMAL -> MaterialTheme.typography.headlineMedium
                                ButtonSize.BIG -> MaterialTheme.typography.headlineLarge
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        text = button.title,
                        color = Color.White,
                        style = when (button.size) {
                            ButtonSize.SMALL -> MaterialTheme.typography.bodyMedium
                            ButtonSize.NORMAL -> MaterialTheme.typography.titleMedium
                            ButtonSize.BIG -> MaterialTheme.typography.titleLarge
                        }
                    )
                    if (button.subtitle.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = button.subtitle,
                            color = Color.White,
                            style = when (button.size) {
                                ButtonSize.SMALL -> MaterialTheme.typography.bodySmall
                                ButtonSize.NORMAL -> MaterialTheme.typography.bodySmall
                                ButtonSize.BIG -> MaterialTheme.typography.bodyMedium
                            }
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${button.parcels}x",
                        color = Color.White,
                        style = when (button.size) {
                            ButtonSize.SMALL -> MaterialTheme.typography.titleSmall
                            ButtonSize.NORMAL -> MaterialTheme.typography.titleMedium
                            ButtonSize.BIG -> MaterialTheme.typography.titleLarge
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (button.paymentType) {
                            PaymentType.CREDIT -> stringResource(R.string.payment_type_credit)
                            PaymentType.DEBIT -> stringResource(R.string.payment_type_debit)
                            PaymentType.USER_CHOICE -> stringResource(R.string.payment_type_user_choice)
                        },
                        color = Color.White,
                        style = when (button.size) {
                            ButtonSize.SMALL -> MaterialTheme.typography.bodySmall
                            ButtonSize.NORMAL -> MaterialTheme.typography.bodySmall
                            ButtonSize.BIG -> MaterialTheme.typography.bodyMedium
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit),
                        tint = Color.White
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentTypeDialog(
    onDismiss: () -> Unit,
    onSelectPaymentType: (PaymentType) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_payment_type)) },
        text = { Text(stringResource(R.string.select_payment_type_subtitle)) },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onSelectPaymentType(PaymentType.CREDIT) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.payment_type_credit))
                }
                Button(
                    onClick = { onSelectPaymentType(PaymentType.DEBIT) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.payment_type_debit))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

private fun buildDeeplink(button: ButtonEntity, userSelectedType: PaymentType?): String {
    val baseUrl = "https://example.com/payment"
    val typeParam = when (userSelectedType ?: button.paymentType) {
        PaymentType.CREDIT -> "credit"
        PaymentType.DEBIT -> "debit"
        PaymentType.USER_CHOICE -> "user_choice"
    }
    val amountParam = button.amount?.let { "&amount=${Uri.encode(it)}" } ?: ""
    return "$baseUrl?title=${Uri.encode(button.title)}" +
            "&parcels=${button.parcels}" +
            "&type=$typeParam" +
            amountParam
}
