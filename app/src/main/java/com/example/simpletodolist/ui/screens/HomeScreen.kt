package com.example.simpletodolist.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.simpletodolist.R
import com.example.simpletodolist.data.local.SessionManager
import com.example.simpletodolist.data.model.TaskList
import com.example.simpletodolist.ui.components.AppTextField
import com.example.simpletodolist.ui.components.PrimaryButton
import com.example.simpletodolist.ui.components.TaskListCard
import com.example.simpletodolist.viewmodel.AuthViewModel
import com.example.simpletodolist.viewmodel.TaskListViewModel

/*
 * Pantalla principal mostrada tras iniciar sesión.
 * Lista las listas de tareas del usuario al estilo Microsoft To Do.
 * Permite crear, renombrar, eliminar listas y cerrar sesión.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    taskListViewModel: TaskListViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userName = sessionManager.getUserName() ?: stringResource(R.string.default_user)

    val uiState by taskListViewModel.uiState.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var taskListToRename by remember { mutableStateOf<TaskList?>(null) }
    var taskListToDelete by remember { mutableStateOf<TaskList?>(null) }

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
                uiState.isLoading && uiState.taskLists.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.taskLists.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.no_notebooks),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn {
                        items(uiState.taskLists, key = { it.id ?: it.title }) { taskList ->
                            TaskListCard(
                                taskList = taskList,
                                onClick = {
                                    navController.navigate("tasks/${taskList.id}/${taskList.title}")
                                },
                                onRename = { taskListToRename = taskList },
                                onDelete = { taskListToDelete = taskList }
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
        TaskListDialog(
            titleText = stringResource(R.string.create_notebook),
            initialValue = "",
            onConfirm = { title ->
                taskListViewModel.createTaskList(title)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    taskListToRename?.let { taskList ->
        TaskListDialog(
            titleText = stringResource(R.string.rename_notebook),
            initialValue = taskList.title,
            onConfirm = { newTitle ->
                taskList.id?.let { taskListViewModel.renameTaskList(it, newTitle) }
                taskListToRename = null
            },
            onDismiss = { taskListToRename = null }
        )
    }

    taskListToDelete?.let { taskList ->
        AlertDialog(
            onDismissRequest = { taskListToDelete = null },
            title = { Text(stringResource(R.string.delete_notebook)) },
            text = { Text(stringResource(R.string.delete_notebook_confirm, taskList.title)) },
            confirmButton = {
                TextButton(onClick = {
                    taskList.id?.let { taskListViewModel.deleteTaskList(it) }
                    taskListToDelete = null
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { taskListToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

/*
 * Diálogo para crear y renombrar una lista de tareas.
 */
@Composable
private fun TaskListDialog(
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