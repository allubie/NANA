package com.allubie.nana.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleRichTextEditor(
    html: String,
    onTextChange: (String) -> Unit,
    placeholder: String = "Start typing...",
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(html)) }
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isUnderline by remember { mutableStateOf(false) }
    var showLinkDialog by remember { mutableStateOf(false) }
    var showHighlightDropdown by remember { mutableStateOf(false) }
    var showHeadingDropdown by remember { mutableStateOf(false) }
    var selectedHeading by remember { mutableStateOf<HeadingLevel?>(null) }
    var selectedHighlight by remember { mutableStateOf<Color?>(null) }
    var images by remember { mutableStateOf(listOf<String>()) }
    var checkboxItems by remember { mutableStateOf(listOf<CheckboxItem>()) }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            images = images + it.toString()
        }
    }

    Column(modifier = modifier) {
        // Formatting Toolbar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column {
                // Single row toolbar - all features
                LazyRow(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Heading dropdown
                    item {
                        Box {
                            ToolbarButton(
                                icon = Icons.Default.Title,
                                isSelected = selectedHeading != null,
                                onClick = { showHeadingDropdown = !showHeadingDropdown }
                            )
                            
                            DropdownMenu(
                                expanded = showHeadingDropdown,
                                onDismissRequest = { showHeadingDropdown = false },
                                properties = PopupProperties(focusable = true)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Normal Text", style = MaterialTheme.typography.bodyLarge) },
                                    onClick = {
                                        selectedHeading = null
                                        showHeadingDropdown = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Heading 1", style = MaterialTheme.typography.headlineLarge) },
                                    onClick = {
                                        selectedHeading = HeadingLevel.H1
                                        showHeadingDropdown = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Heading 2", style = MaterialTheme.typography.headlineMedium) },
                                    onClick = {
                                        selectedHeading = HeadingLevel.H2
                                        showHeadingDropdown = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Heading 3", style = MaterialTheme.typography.headlineSmall) },
                                    onClick = {
                                        selectedHeading = HeadingLevel.H3
                                        showHeadingDropdown = false
                                    }
                                )
                            }
                        }
                    }
                    
                    item {
                        ToolbarButton(
                            icon = Icons.Default.FormatBold,
                            isSelected = isBold,
                            onClick = { isBold = !isBold }
                        )
                    }
                    item {
                        ToolbarButton(
                            icon = Icons.Default.FormatItalic,
                            isSelected = isItalic,
                            onClick = { isItalic = !isItalic }
                        )
                    }
                    item {
                        ToolbarButton(
                            icon = Icons.Default.FormatUnderlined,
                            isSelected = isUnderline,
                            onClick = { isUnderline = !isUnderline }
                        )
                    }
                    
                    // Highlighting dropdown
                    item {
                        Box {
                            ToolbarButton(
                                icon = Icons.Default.Highlight,
                                isSelected = selectedHighlight != null,
                                onClick = { showHighlightDropdown = !showHighlightDropdown }
                            )
                            
                            DropdownMenu(
                                expanded = showHighlightDropdown,
                                onDismissRequest = { showHighlightDropdown = false },
                                properties = PopupProperties(focusable = true)
                            ) {
                                DropdownMenuItem(
                                    text = { 
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(Color.Yellow, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Yellow Highlight")
                                        }
                                    },
                                    onClick = {
                                        selectedHighlight = if (selectedHighlight == Color.Yellow) null else Color.Yellow
                                        showHighlightDropdown = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { 
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(Color.Green, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Green Highlight")
                                        }
                                    },
                                    onClick = {
                                        selectedHighlight = if (selectedHighlight == Color.Green) null else Color.Green
                                        showHighlightDropdown = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { 
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(Color.Blue, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Blue Highlight")
                                        }
                                    },
                                    onClick = {
                                        selectedHighlight = if (selectedHighlight == Color.Blue) null else Color.Blue
                                        showHighlightDropdown = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { 
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(Color.Magenta, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Pink Highlight")
                                        }
                                    },
                                    onClick = {
                                        selectedHighlight = if (selectedHighlight == Color.Magenta) null else Color.Magenta
                                        showHighlightDropdown = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { 
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Remove Highlight")
                                        }
                                    },
                                    onClick = {
                                        selectedHighlight = null
                                        showHighlightDropdown = false
                                    }
                                )
                            }
                        }
                    }
                    
                    item {
                        ToolbarButton(
                            icon = Icons.Default.CheckBox,
                            onClick = { 
                                checkboxItems = listOf(CheckboxItem(text = "New task", isChecked = false)) + checkboxItems
                            }
                        )
                    }
                    item {
                        ToolbarButton(
                            icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                            onClick = { 
                                val currentPos = textFieldValue.selection.start
                                val currentText = textFieldValue.text
                                val newText = if (currentPos == 0 || currentText[currentPos - 1] == '\n') {
                                    currentText.substring(0, currentPos) + "• " + currentText.substring(currentPos)
                                } else {
                                    currentText.substring(0, currentPos) + "\n• " + currentText.substring(currentPos)
                                }
                                textFieldValue = textFieldValue.copy(
                                    text = newText,
                                    selection = androidx.compose.ui.text.TextRange(currentPos + if (currentPos == 0 || currentText.getOrNull(currentPos - 1) == '\n') 2 else 3)
                                )
                                onTextChange(newText)
                            }
                        )
                    }
                    item {
                        ToolbarButton(
                            icon = Icons.Default.Image,
                            onClick = { imagePickerLauncher.launch("image/*") }
                        )
                    }
                    item {
                        ToolbarButton(
                            icon = Icons.Default.Link,
                            onClick = { showLinkDialog = true }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Text Editor
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Checkbox items - positioned at the top
                checkboxItems.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = item.isChecked,
                            onCheckedChange = { isChecked ->
                                checkboxItems = checkboxItems.toMutableList().apply {
                                    this[index] = item.copy(isChecked = isChecked)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        BasicTextField(
                            value = item.text,
                            onValueChange = { newText ->
                                checkboxItems = checkboxItems.toMutableList().apply {
                                    this[index] = item.copy(text = newText)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                        )
                        IconButton(
                            onClick = {
                                checkboxItems = checkboxItems.toMutableList().apply {
                                    removeAt(index)
                                }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete item",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                
                // Main text field with formatting and heading support
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        textFieldValue = newValue
                        onTextChange(newValue.text)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    textStyle = when (selectedHeading) {
                        HeadingLevel.H1 -> MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
                            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                            textDecoration = if (isUnderline) TextDecoration.Underline else TextDecoration.None,
                            background = selectedHighlight ?: Color.Transparent,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        HeadingLevel.H2 -> MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
                            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                            textDecoration = if (isUnderline) TextDecoration.Underline else TextDecoration.None,
                            background = selectedHighlight ?: Color.Transparent,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        HeadingLevel.H3 -> MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
                            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                            textDecoration = if (isUnderline) TextDecoration.Underline else TextDecoration.None,
                            background = selectedHighlight ?: Color.Transparent,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        null -> MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                            textDecoration = if (isUnderline) TextDecoration.Underline else TextDecoration.None,
                            background = selectedHighlight ?: Color.Transparent,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    },
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        if (textFieldValue.text.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                )
                
                // Display images
                images.forEach { imageUri ->
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Inserted image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
    
    // Link Dialog
    if (showLinkDialog) {
        var linkText by remember { mutableStateOf(
            if (textFieldValue.selection.start != textFieldValue.selection.end) {
                textFieldValue.text.substring(textFieldValue.selection.start, textFieldValue.selection.end)
            } else ""
        ) }
        var linkUrl by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showLinkDialog = false },
            title = { Text("Insert Link") },
            text = {
                Column {
                    OutlinedTextField(
                        value = linkText,
                        onValueChange = { linkText = it },
                        label = { Text("Link Text") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = linkUrl,
                        onValueChange = { linkUrl = it },
                        label = { Text("URL") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (linkUrl.isNotBlank() && linkText.isNotBlank()) {
                            val currentText = textFieldValue.text
                            val selection = textFieldValue.selection
                            
                            val newText = if (selection.start != selection.end) {
                                // Replace selected text with link
                                currentText.replaceRange(
                                    selection.start,
                                    selection.end,
                                    "[$linkText]($linkUrl)"
                                )
                            } else {
                                // Insert link at cursor position
                                currentText.substring(0, selection.start) + 
                                "[$linkText]($linkUrl)" + 
                                currentText.substring(selection.start)
                            }
                            
                            textFieldValue = textFieldValue.copy(
                                text = newText,
                                selection = androidx.compose.ui.text.TextRange(
                                    selection.start + "[$linkText]($linkUrl)".length
                                )
                            )
                            onTextChange(newText)
                        }
                        showLinkDialog = false
                    }
                ) {
                    Text("Insert")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLinkDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ToolbarButton(
    icon: ImageVector,
    onClick: () -> Unit,
    isSelected: Boolean = false
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                Color.Transparent,
            contentColor = if (isSelected) 
                MaterialTheme.colorScheme.onPrimaryContainer 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}

data class CheckboxItem(
    val text: String,
    val isChecked: Boolean
)

enum class HeadingLevel {
    H1, H2, H3
}
