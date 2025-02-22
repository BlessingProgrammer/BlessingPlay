package com.blessingsoftware.blessingplay.home.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun HomeDialog(
    title: String,
    buttonBackgroundColor: Color,
    buttonTextColor: Color,
    modifier: Modifier = Modifier,
    onVisible: Boolean,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    if (onVisible) {
        Dialog(onDismissRequest = {}) {
            Surface(
                modifier = modifier,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Box {
                    content()
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        HomeDialogButton(
                            title = "Cancel",
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.DarkGray),
                            textColor = Color.White,
                            onClick = onDismiss
                        )
                        HomeDialogButton(
                            title = title,
                            modifier = Modifier
                                .weight(1f)
                                .background(buttonBackgroundColor),
                            textColor = buttonTextColor,
                            onClick = onSubmit
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeDialogButton(
    title: String,
    modifier: Modifier = Modifier,
    textColor: Color,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text = title, color = textColor)
    }
}
