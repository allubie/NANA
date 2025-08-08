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
import kotlin.math.abs

data class SwipeAction(
    val icon: ImageVector,
    val backgroundColor: androidx.compose.ui.graphics.Color,
    val onClick: () -> Unit
)

@Composable
fun SwipeableCard(
    modifier: Modifier = Modifier,
    leftActions: List<SwipeAction> = emptyList(),
    rightActions: List<SwipeAction> = emptyList(),
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val offsetX = remember { Animatable(0f) }
    val maxSwipeDistance = with(density) { 120.dp.toPx() }
    var isDragging by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Background actions row
        Row(
            modifier = Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side actions (shown when swiping right)
            leftActions.forEachIndexed { index, action ->
                if (index > 0) {
                    Box(modifier = Modifier.width(8.dp))
                }
                ActionButton(
                    icon = action.icon,
                    backgroundColor = action.backgroundColor,
                    onClick = {
                        scope.launch {
                            offsetX.animateTo(0f)
                            action.onClick()
                        }
                    }
                )
            }
            
            // Spacer to push right actions to the end
            Box(modifier = Modifier.weight(1f))
            
            // Right side actions (shown when swiping left)
            rightActions.forEachIndexed { index, action ->
                if (index > 0) {
                    Box(modifier = Modifier.width(2.dp))
                }
                ActionButton(
                    icon = action.icon,
                    backgroundColor = action.backgroundColor,
                    onClick = {
                        scope.launch {
                            offsetX.animateTo(0f)
                            action.onClick()
                        }
                    }
                )
            }
        }
        
        // Main content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
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
                                isDragging = false
                            }
                        }
                    ) { _, dragAmount ->
                        if (kotlin.math.abs(dragAmount) > 5f) { // Only register as drag if significant movement
                            scope.launch {
                                // Calculate the proposed new value
                                val proposedValue = offsetX.value + dragAmount
                                
                                // Determine the constrained value based on available actions
                                val constrainedValue = when {
                                    // If no left actions, don't allow positive offset (right swipe)
                                    leftActions.isEmpty() && proposedValue > 0 -> 0f
                                    // If no right actions, don't allow negative offset (left swipe)
                                    rightActions.isEmpty() && proposedValue < 0 -> 0f
                                    // Otherwise, constrain to max swipe distance in both directions
                                    else -> proposedValue.coerceIn(-maxSwipeDistance, maxSwipeDistance)
                                }
                                
                                offsetX.snapTo(constrainedValue)
                            }
                        }
                    }
                }
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

@Composable
fun SwipeableItemCard(
    modifier: Modifier = Modifier,
    isPinned: Boolean = false,
    showPin: Boolean = true,
    showArchive: Boolean = true,
    onPin: () -> Unit = {},
    onArchive: () -> Unit = {},
    onDelete: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val leftActions = if (showPin) {
        listOf(
            SwipeAction(
                icon = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                backgroundColor = MaterialTheme.colorScheme.secondary,
                onClick = onPin
            )
        )
    } else {
        emptyList()
    }
    
    val rightActions = buildList {
        if (showArchive) {
            add(SwipeAction(
                icon = Icons.Default.Archive,
                backgroundColor = MaterialTheme.colorScheme.tertiary,
                onClick = onArchive
            ))
        }
        add(SwipeAction(
            icon = Icons.Default.Delete,
            backgroundColor = MaterialTheme.colorScheme.error,
            onClick = onDelete
        ))
    }
    
    SwipeableCard(
        modifier = modifier,
        leftActions = leftActions,
        rightActions = rightActions,
        content = content
    )
}
