package com.example.diaryapp.ui.screens.edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    vm: EditViewModel,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { vm.addImage(it) } }

    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success -> if (success) cameraUri?.let { vm.addImage(it) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.title.isEmpty()) "New Entry" else "Edit Entry") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { vm.save(onSaved) },
                        enabled = !uiState.isSaving
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = vm::onTitleChange,
                label = { Text("Title") },
                isError = uiState.titleError,
                supportingText = if (uiState.titleError) {
                    { Text("Title cannot be empty") }
                } else null,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            OutlinedTextField(
                value = uiState.content,
                onValueChange = vm::onContentChange,
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                maxLines = Int.MAX_VALUE
            )

            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { galleryLauncher.launch("image/*") }) {
                    Text("Gallery")
                }
                OutlinedButton(onClick = {
                    val file = File(context.cacheDir, "camera/capture_${System.currentTimeMillis()}.jpg")
                    file.parentFile?.mkdirs()
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    cameraUri = uri
                    cameraLauncher.launch(uri)
                }) {
                    Text("Camera")
                }
            }

            uiState.imagePaths.forEach { path ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    AsyncImage(
                        model = File(path),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { vm.removeImage(path) },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove image",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
