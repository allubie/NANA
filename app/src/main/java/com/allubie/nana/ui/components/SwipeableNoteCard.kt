package com.allubie.nana.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeableNoteCard(
    modifier: Modifier = Modifier,
    isPinned: Boolean = false,
    onEdit: () -> Unit = {},
    onPin: () -> Unit = {},
    onArchive: () -> Unit = {},
    onDelete: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val offsetX = remember { Animatable(0f) }
    val maxSwipeDistance = with(density) { 200.dp.toPx() }
    
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Background actions row
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Left side actions (shown when swiping right)
            ActionButton(
                icon = Icons.Default.Edit,
                backgroundColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    scope.launch {
                        offsetX.animateTo(0f)
                        onEdit()
                    }
                }
            )
            
            ActionButton(
                icon = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                backgroundColor = MaterialTheme.colorScheme.secondary,
                onClick = {
                    scope.launch {
                        offsetX.animateTo(0f)
                        onPin()
                    }
                }
            )
            
            // Spacer to push right actions to the end
            Box(modifier = Modifier.weight(1f))
            
            // Right side actions (shown when swiping left)
            ActionButton(
                icon = Icons.Default.Archive,
                backgroundColor = MaterialTheme.colorScheme.tertiary,
                onClick = {
                    scope.launch {
                        offsetX.animateTo(0f)
                        onArchive()
                    }
                }
            )
            
            ActionButton(
                icon = Icons.Default.Delete,
                backgroundColor = MaterialTheme.colorScheme.error,
                onClick = {
                    scope.launch {
                        offsetX.animateTo(0f)
                        onDelete()
                    }
                }
            )
        }
        
        // Main content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                when {
                                    offsetX.value > maxSwipeDistance / 3 -> {
                                        offsetX.animateTo(maxSwipeDistance)
                                    }
                                    offsetX.value < -maxSwipeDistance / 3 -> {
                                        offsetX.animateTo(-maxSwipeDistance)
                                    }
                                    else -> {
                                        offsetX.animateTo(0f)
                                    }
                                }
                            }
                        }
                    ) { _, dragAmount ->
                        scope.launch {
                            val newValue = (offsetX.value + dragAmount).coerceIn(
                                -maxSwipeDistance, maxSwipeDistance
                            )
                            offsetX.snapTo(newValue)
                        }
                    }
                }
                .background(MaterialTheme.colorScheme.surface)
        ) {
            content()
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    backgroundColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
