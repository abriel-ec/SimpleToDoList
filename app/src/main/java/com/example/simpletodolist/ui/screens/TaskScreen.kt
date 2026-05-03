package com.example.simpletodolist.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.core.graphics.toColorInt
import com.example.simpletodolist.R
import com.example.simpletodolist.data.model.Task
import com.example.simpletodolist.viewmodel.TaskViewModel

/*
 * Pantalla principal de tareas al estilo Microsoft To Do.
 * Muestra tareas pendientes e importantes arriba,
 * y tareas completadas colapsables abajo.
 * Incluye barra de entrada rápida en la parte inferior.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    navController: NavController,
    listId: String,
    listTitle: String,
    listColor: String,
    viewModel: TaskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var newTaskTitle by remember { mutableStateOf("") }
    var showCompleted by remember { mutableStateOf(false) }

    val headerColor = runCatching {
        Color(listColor.toColorInt())
    }.getOrDefault(MaterialTheme.colorScheme.primary)

    LaunchedEffect(listId) {
        viewModel.loadTasks(listId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = listTitle,
                        fontWeight = FontWeight.Bold,
                        color = headerColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = headerColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Barra de entrada rápida estilo Microsoft To Do
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(headerColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            color = headerColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        placeholder = {
                            Text(
                                stringResource(R.string.add_task_hint),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = headerColor,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (newTaskTitle.isNotBlank()) {
                                    viewModel.createTask(listId, newTaskTitle)
                                    newTaskTitle = ""
                                }
                            }
                        )
                    )
                }
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
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = headerColor
                    )
                }
                uiState.tasks.isEmpty() && uiState.completedTasks.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.displayLarge,
                            color = headerColor.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.no_tasks),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Tareas pendientes
                        items(
                            uiState.tasks.sortedByDescending { it.isImportant },
                            key = { it.id ?: it.title }
                        ) { task ->
                            TaskRow(
                                task = task,
                                accentColor = headerColor,
                                onToggleComplete = {
                                    viewModel.toggleComplete(task, listId)
                                },
                                onToggleImportant = {
                                    viewModel.toggleImportant(task, listId)
                                },
                                onDelete = {
                                    task.id?.let { viewModel.deleteTask(it, listId) }
                                }
                            )
                        }

                        // Sección de completadas colapsable
                        if (uiState.completedTasks.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(
                                    onClick = { showCompleted = !showCompleted },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = if (showCompleted)
                                            Icons.Default.KeyboardArrowUp
                                        else
                                            Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(
                                            R.string.completed_count,
                                            uiState.completedTasks.size
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (showCompleted) {
                                items(
                                    uiState.completedTasks,
                                    key = { it.id ?: it.title }
                                ) { task ->
                                    AnimatedVisibility(
                                        visible = showCompleted,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        TaskRow(
                                            task = task,
                                            accentColor = headerColor,
                                            onToggleComplete = {
                                                viewModel.toggleComplete(task, listId)
                                            },
                                            onToggleImportant = {
                                                viewModel.toggleImportant(task, listId)
                                            },
                                            onDelete = {
                                                task.id?.let { viewModel.deleteTask(it, listId) }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            uiState.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                )
            }
        }
    }
}

/*
 * Fila individual de tarea al estilo Microsoft To Do.
 * Muestra checkbox, título, estrella de importancia y botón eliminar.
 */
@Composable
fun TaskRow(
    task: Task,
    accentColor: Color,
    onToggleComplete: () -> Unit,
    onToggleImportant: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(tween(200)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox circular estilo To Do
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleComplete() },
                colors = CheckboxDefaults.colors(
                    checkedColor = accentColor,
                    uncheckedColor = accentColor.copy(alpha = 0.6f),
                    checkmarkColor = Color.White
                )
            )

            Text(
                text = task.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.isCompleted)
                    TextDecoration.LineThrough
                else
                    TextDecoration.None,
                color = if (task.isCompleted)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface
            )

            // Estrella de importancia
            IconButton(onClick = onToggleImportant) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Importante",
                    tint = if (task.isImportant) accentColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }

            // Eliminar
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}