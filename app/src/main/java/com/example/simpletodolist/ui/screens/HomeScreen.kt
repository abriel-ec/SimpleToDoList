package com.example.simpletodolist.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.simpletodolist.R
import com.example.simpletodolist.data.local.SessionManager
import com.example.simpletodolist.data.model.Notebook
import com.example.simpletodolist.ui.components.AppTextField
import com.example.simpletodolist.ui.components.NotebookCard
import com.example.simpletodolist.ui.components.PrimaryButton
import com.example.simpletodolist.viewmodel.AuthViewModel
import com.example.simpletodolist.viewmodel.NotebookViewModel

/*
 * Pantalla principal mostrada tras iniciar sesión.
 *
 * Lista los cuadernos del usuario actual y permite:
 *  - Crear uno nuevo (botón flotante + diálogo).
 *  - Renombrar uno existente (menú contextual + diálogo).
 *  - Eliminar uno (menú contextual + diálogo de confirmación).
 *  - Cerrar sesión (acción en la TopAppBar).
 *
 * Recibe el AuthViewModel para gestionar el cierre de sesión y crea
 * internamente su propio NotebookViewModel mediante viewModel(),
 * cumpliendo el requisito MVVM de tener un ViewModel por pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    notebookViewModel: NotebookViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userName = sessionManager.getUserName() ?: stringResource(R.string.default_user)

    val uiState by notebookViewModel.uiState.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var notebookToRename by remember { mutableStateOf<Notebook?>(null) }
    var notebookToDelete by remember { mutableStateOf<Notebook?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.welcome_user, userName)) },
                actions = {
                    TextButton(onClick = {
                        viewModel.logout()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Text(stringResource(R.string.logout))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.create_notebook)
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            when {
                uiState.isLoading && uiState.notebooks.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.notebooks.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.no_notebooks),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn {
                        items(uiState.notebooks, key = { it.id ?: it.title }) { notebook ->
                            NotebookCard(
                                notebook = notebook,
                                onClick = {
                                    // TODO: navegación a secciones (Fase 4)
                                },
                                onRename = { notebookToRename = notebook },
                                onDelete = { notebookToDelete = notebook }
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
        NotebookTitleDialog(
            titleText = stringResource(R.string.create_notebook),
            initialValue = "",
            onConfirm = { title ->
                notebookViewModel.createNotebook(title)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    notebookToRename?.let { notebook ->
        NotebookTitleDialog(
            titleText = stringResource(R.string.rename_notebook),
            initialValue = notebook.title,
            onConfirm = { newTitle ->
                notebook.id?.let { notebookViewModel.renameNotebook(it, newTitle) }
                notebookToRename = null
            },
            onDismiss = { notebookToRename = null }
        )
    }

    notebookToDelete?.let { notebook ->
        AlertDialog(
            onDismissRequest = { notebookToDelete = null },
            title = { Text(stringResource(R.string.delete_notebook)) },
            text = { Text(stringResource(R.string.delete_notebook_confirm, notebook.title)) },
            confirmButton = {
                TextButton(onClick = {
                    notebook.id?.let { notebookViewModel.deleteNotebook(it) }
                    notebookToDelete = null
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { notebookToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

/*
 * Diálogo reutilizado para crear y renombrar un cuaderno.
 *
 * Se mantiene en este archivo por simplicidad (es muy específico de la
 * pantalla de cuadernos). Reutiliza los componentes AppTextField y
 * PrimaryButton del paquete ui.components.
 */
@Composable
private fun NotebookTitleDialog(
    titleText: String,
    initialValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titleText) },
        text = {
            Column {
                AppTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = stringResource(R.string.notebook_title_label)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            PrimaryButton(
                text = stringResource(R.string.save),
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
