package com.lip.syncoding.the.frame.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

fun Modifier.comicShadow(
    offset: Dp,
    cornerRadius: Dp,
    color: Color = ComicBlack.copy(alpha = 0.25f)
): Modifier = this.then(
    Modifier.drawBehind {
        val offsetPx = offset.toPx()
        val radiusPx = cornerRadius.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(offsetPx, offsetPx),
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(radiusPx, radiusPx)
        )
    }
)
