package com.example.simpletodolist.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.simpletodolist.R
import com.example.simpletodolist.data.model.Page
import com.example.simpletodolist.ui.components.AppTextField
import com.example.simpletodolist.ui.components.PrimaryButton
import com.example.simpletodolist.viewmodel.PageViewModel

/*
 * Pantalla que lista las páginas de una sección.
 * Permite crear y eliminar páginas, y navegar al editor.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageListScreen(
    navController: NavController,
    sectionId: String,
    sectionTitle: String,
    viewModel: PageViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var pageToDelete by remember { mutableStateOf<Page?>(null) }

    LaunchedEffect(sectionId) {
        viewModel.loadPages(sectionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sectionTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.create_page))
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
                uiState.pages.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.no_pages),
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
                        items(uiState.pages, key = { it.id ?: it.title }) { page ->
                            PageCard(
                                page = page,
                                onClick = {
                                    viewModel.selectPage(page)
                                    navController.navigate("editor/${page.id}")
                                },
                                onDelete = { pageToDelete = page }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        PageTitleDialog(
            onConfirm = { title ->
                viewModel.createPage(sectionId, title)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    pageToDelete?.let { page ->
        AlertDialog(
            onDismissRequest = { pageToDelete = null },
            title = { Text(stringResource(R.string.delete_page)) },
            text = { Text(stringResource(R.string.delete_page_confirm, page.title)) },
            confirmButton = {
                TextButton(onClick = {
                    page.id?.let { viewModel.deletePage(it, sectionId) }
                    pageToDelete = null
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { pageToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

/*
 * Tarjeta de página con título, preview del contenido y fecha.
 */
@Composable
fun PageCard(
    page: Page,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (page.content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = page.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (page.lastModified.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = page.lastModified,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
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
 * Diálogo para crear una nueva página con título.
 */
@Composable
fun PageTitleDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.create_page)) },
        text = {
            AppTextField(
                value = text,
                onValueChange = { text = it },
                label = stringResource(R.string.page_title_label)
            )
        },
        confirmButton = {
            PrimaryButton(
                text = stringResource(R.string.save),
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}