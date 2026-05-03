package com.example.simpletodolist.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.simpletodolist.R
import com.example.simpletodolist.data.model.Section
import com.example.simpletodolist.ui.components.AppTextField
import com.example.simpletodolist.ui.components.PrimaryButton
import com.example.simpletodolist.viewmodel.SectionViewModel

/*
 * Pantalla que muestra las secciones de un cuaderno.
 * Permite crear, renombrar y eliminar secciones con color.
 * Al tocar una sección navega a la lista de páginas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionScreen(
    navController: NavController,
    notebookId: String,
    notebookTitle: String,
    viewModel: SectionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var sectionToRename by remember { mutableStateOf<Section?>(null) }
    var sectionToDelete by remember { mutableStateOf<Section?>(null) }

    LaunchedEffect(notebookId) {
        viewModel.loadSections(notebookId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(notebookTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.create_section))
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.sections.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.no_sections),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.sections, key = { it.id ?: it.title }) { section ->
                            SectionCard(
                                section = section,
                                onClick = {
                                    navController.navigate(
                                        "pages/${section.id}/${section.title}"
                                    )
                                },
                                onRename = { sectionToRename = section },
                                onDelete = { sectionToDelete = section }
                            )
                        }
                    }
                }
            }

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                )
            }
        }
    }

    if (showCreateDialog) {
        SectionDialog(
            titleText = stringResource(R.string.create_section),
            initialValue = "",
            initialColor = "#6650A4",
            onConfirm = { title, color ->
                viewModel.createSection(notebookId, title, color)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    sectionToRename?.let { section ->
        SectionDialog(
            titleText = stringResource(R.string.rename_section),
            initialValue = section.title,
            initialColor = section.color,
            onConfirm = { newTitle, _ ->
                section.id?.let { viewModel.renameSection(it, newTitle, notebookId) }
                sectionToRename = null
            },
            onDismiss = { sectionToRename = null }
        )
    }

    sectionToDelete?.let { section ->
        AlertDialog(
            onDismissRequest = { sectionToDelete = null },
            title = { Text(stringResource(R.string.delete_section)) },
            text = { Text(stringResource(R.string.delete_section_confirm, section.title)) },
            confirmButton = {
                TextButton(onClick = {
                    section.id?.let { viewModel.deleteSection(it, notebookId) }
                    sectionToDelete = null
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { sectionToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

/*
 * Tarjeta visual de una sección con color lateral distintivo.
 */
@Composable
fun SectionCard(
    section: Section,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val sectionColor = runCatching {
        Color(android.graphics.Color.parseColor(section.color))
    }.getOrDefault(MaterialTheme.colorScheme.primary)

    val animatedColor by animateColorAsState(
        targetValue = sectionColor,
        animationSpec = tween(300),
        label = "sectionColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(64.dp)
                    .background(animatedColor)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = section.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.rename)) },
                        onClick = { showMenu = false; onRename() }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete)) },
                        onClick = { showMenu = false; onDelete() }
                    )
                }
            }
        }
    }
}

/*
 * Diálogo para crear o renombrar una sección con selector de color.
 */
@Composable
fun SectionDialog(
    titleText: String,
    initialValue: String,
    initialColor: String,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val colorOptions = listOf(
        "#6650A4", "#E53935", "#1E88E5",
        "#43A047", "#FB8C00", "#8E24AA"
    )
    var text by remember { mutableStateOf(initialValue) }
    var selectedColor by remember { mutableStateOf(initialColor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titleText) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AppTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = stringResource(R.string.section_title_label)
                )
                Text(
                    text = stringResource(R.string.pick_color),
                    style = MaterialTheme.typography.labelMedium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorOptions.forEach { hex ->
                        val color = runCatching {
                            Color(android.graphics.Color.parseColor(hex))
                        }.getOrDefault(Color.Gray)

                        Box(
                            modifier = Modifier
                                .size(if (selectedColor == hex) 36.dp else 28.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColor = hex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            PrimaryButton(
                text = stringResource(R.string.save),
                onClick = { onConfirm(text, selectedColor) },
                enabled = text.isNotBlank()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}