package com.example.buttons.ui

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.buttons.R
import com.example.buttons.data.ButtonEntity
import com.example.buttons.data.ButtonSize
import com.example.buttons.data.PageEntity
import com.example.buttons.data.PaymentType
import com.example.buttons.viewmodel.ButtonViewModel
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: ButtonViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onEditButton: (ButtonEntity?) -> Unit
) {
    val buttonsByPage by viewModel.buttonsByPage.collectAsState()
    val pages by viewModel.pages.collectAsState()
    val currentPageId by viewModel.currentPageId.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val buttonFontSize by viewModel.buttonFontSize.collectAsState()
    val wallpaperEnabled by viewModel.wallpaperEnabled.collectAsState()
    val backgroundColor by viewModel.backgroundColor.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showPageDialog by remember { mutableStateOf(false) }
    var showDeletePageDialog by remember { mutableStateOf(false) }
    var pageToDelete by remember { mutableStateOf<PageEntity?>(null) }
    var pendingButton by remember { mutableStateOf<ButtonEntity?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size.coerceAtLeast(1) }
    )

    LaunchedEffect(pagerState.currentPage, pages) {
        if (pages.isNotEmpty() && pagerState.currentPage < pages.size) {
            val selectedPage = pages[pagerState.currentPage]
            if (selectedPage.id != currentPageId) {
                viewModel.setCurrentPage(selectedPage.id)
            }
        }
    }

    LaunchedEffect(currentPageId, pages) {
        if (pages.isNotEmpty()) {
            val targetIndex = pages.indexOfFirst { it.id == currentPageId }
            if (targetIndex >= 0 && targetIndex != pagerState.currentPage) {
                pagerState.scrollToPage(targetIndex)
            }
        }
    }

    val bgColor = try {
        Color(android.graphics.Color.parseColor(backgroundColor))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.background
    }

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
        containerColor = if (wallpaperEnabled) Color.Transparent else bgColor,
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
                        if (isEditMode) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.manage_pages)) },
                                onClick = {
                                    showMenu = false
                                    showPageDialog = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Menu, contentDescription = null)
                                }
                            )
                        }
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
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            if (pages.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { pageIndex ->
                    val currentPage = pages.getOrNull(pageIndex)
                    val pageButtons = currentPage?.let { buttonsByPage[it.id] } ?: emptyList()
                    
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (currentPage != null) {
                            Text(
                                text = currentPage.name,
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        
                        if (pageButtons.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
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
                                    buttons = pageButtons,
                                    viewModel = viewModel,
                                    onEditButton = onEditButton,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                ButtonGrid(
                                    buttons = pageButtons,
                                    onButtonClick = { button ->
                                        if (button.paymentType == PaymentType.USER_CHOICE) {
                                            pendingButton = button
                                            showPaymentDialog = true
                                        } else {
                                            makePayment(button, context)
                                        }
                                    },
                                    fontSize = buttonFontSize,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
                
                if (pages.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.page_indicator, pagerState.currentPage + 1, pages.size),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
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
                        makePayment(button, context, selectedType)
                    }
                    showPaymentDialog = false
                    pendingButton = null
                }
            )
        }

        if (showPageDialog) {
            PageManagementDialog(
                pages = pages,
                currentPageId = currentPageId,
                onDismiss = { showPageDialog = false },
                onAddPage = { viewModel.addPage(it) },
                onDeletePage = { page ->
                    pageToDelete = page
                    showDeletePageDialog = true
                },
                onUpdatePage = { page ->
                    viewModel.updatePage(page)
                },
                onSelectPage = { page ->
                    scope.launch {
                        val index = pages.indexOfFirst { it.id == page.id }
                        if (index >= 0) {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                    showPageDialog = false
                }
            )
        }

        if (showDeletePageDialog && pageToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeletePageDialog = false },
                title = { Text(stringResource(R.string.delete_page)) },
                text = { Text(stringResource(R.string.delete_page_confirm)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            pageToDelete?.let { viewModel.deletePage(it) }
                            showDeletePageDialog = false
                            pageToDelete = null
                        }
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showDeletePageDialog = false 
                        pageToDelete = null
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
    }
}

private fun makePayment(
    button: ButtonEntity,
    context: Context,
    selectedType: PaymentType? = null
) {
    val deeplinkUri = button.getDeeplinkUri(selectedType)
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.data = deeplinkUri
    startActivity(context, intent, null)
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
        ButtonSize.BIG -> 180.dp
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
                        text = button.amount.replace(".", ","),
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
                        PaymentType.CREDIT -> "C"
                        PaymentType.DEBIT -> "D"
                        PaymentType.USER_CHOICE -> ""
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

    val infiniteTransition = rememberInfiniteTransition(label = "shake")
    val shake by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shakeAnimation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer {
                if (!isDragging) {
                    rotationZ = shake * 0.5f
                    translationX = shake
                }
            }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageManagementDialog(
    pages: List<PageEntity>,
    currentPageId: Long,
    onDismiss: () -> Unit,
    onAddPage: (String) -> Unit,
    onDeletePage: (PageEntity) -> Unit,
    onUpdatePage: (PageEntity) -> Unit,
    onSelectPage: (PageEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var pageToEdit by remember { mutableStateOf<PageEntity?>(null) }
    var editPageName by remember { mutableStateOf("") }
    var newPageName by remember { mutableStateOf("") }
    var reorderablePages by remember(pages) { mutableStateOf(pages) }

    LaunchedEffect(pages) {
        reorderablePages = pages
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.manage_pages)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                reorderablePages.forEachIndexed { index, page ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (page.id == currentPageId) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else 
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(R.string.drag_to_reorder),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = page.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Row {
                                if (index > 0) {
                                    IconButton(onClick = { 
                                        val newList = reorderablePages.toMutableList()
                                        val temp = newList[index]
                                        newList[index] = newList[index - 1]
                                        newList[index - 1] = temp
                                        reorderablePages = newList
                                        // Update positions in database
                                        newList.forEachIndexed { idx, p ->
                                            onUpdatePage(p.copy(position = idx))
                                        }
                                    }) {
                                        Icon(Icons.Default.ArrowUpward, contentDescription = stringResource(R.string.move_up))
                                    }
                                }
                                if (index < reorderablePages.size - 1) {
                                    IconButton(onClick = { 
                                        val newList = reorderablePages.toMutableList()
                                        val temp = newList[index]
                                        newList[index] = newList[index + 1]
                                        newList[index + 1] = temp
                                        reorderablePages = newList
                                        // Update positions in database
                                        newList.forEachIndexed { idx, p ->
                                            onUpdatePage(p.copy(position = idx))
                                        }
                                    }) {
                                        Icon(Icons.Default.ArrowDownward, contentDescription = stringResource(R.string.move_down))
                                    }
                                }
                                IconButton(onClick = { onSelectPage(page) }) {
                                    Icon(Icons.Default.Check, contentDescription = stringResource(R.string.edit))
                                }
                                IconButton(onClick = { 
                                    pageToEdit = page
                                    editPageName = page.name
                                    showEditDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_page))
                                }
                                if (pages.size > 1) {
                                    IconButton(onClick = { onDeletePage(page) }) {
                                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { showAddDialog = true }) {
                Text(stringResource(R.string.add_page))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                newPageName = ""
            },
            title = { Text(stringResource(R.string.add_page)) },
            text = {
                OutlinedTextField(
                    value = newPageName,
                    onValueChange = { newPageName = it },
                    label = { Text(stringResource(R.string.page_name)) },
                    placeholder = { Text(stringResource(R.string.page_name_hint)) },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPageName.isNotBlank()) {
                            onAddPage(newPageName)
                            showAddDialog = false
                            newPageName = ""
                        }
                    },
                    enabled = newPageName.isNotBlank()
                ) {
                    Text(stringResource(R.string.create_page))
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddDialog = false
                    newPageName = ""
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showEditDialog && pageToEdit != null) {
        AlertDialog(
            onDismissRequest = { 
                showEditDialog = false
                pageToEdit = null
                editPageName = ""
            },
            title = { Text(stringResource(R.string.edit_page)) },
            text = {
                OutlinedTextField(
                    value = editPageName,
                    onValueChange = { editPageName = it },
                    label = { Text(stringResource(R.string.page_name)) },
                    placeholder = { Text(stringResource(R.string.page_name_hint)) },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editPageName.isNotBlank()) {
                            pageToEdit?.let { page ->
                                onUpdatePage(page.copy(name = editPageName))
                            }
                            showEditDialog = false
                            pageToEdit = null
                            editPageName = ""
                        }
                    },
                    enabled = editPageName.isNotBlank()
                ) {
                    Text(stringResource(R.string.update_page))
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showEditDialog = false
                    pageToEdit = null
                    editPageName = ""
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
