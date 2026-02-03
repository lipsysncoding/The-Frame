package com.lip.syncoding.the.frame

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lip.syncoding.the.frame.ui.theme.ComicBackground
import com.lip.syncoding.the.frame.ui.theme.ComicBlack
import com.lip.syncoding.the.frame.ui.theme.ComicMint
import com.lip.syncoding.the.frame.ui.theme.ComicYellow
import com.lip.syncoding.the.frame.ui.theme.TheFrameTheme
import com.lip.syncoding.the.frame.ui.theme.comicShadow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class PhotoViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val effectName = intent.getStringExtra(EXTRA_EFFECT) ?: PreviewEffect.Fade.name
        val effect = PreviewEffect.valueOf(effectName)
        val effectDuration = intent.getFloatExtra(EXTRA_EFFECT_DURATION, 1.2f)
        val photoInterval = intent.getFloatExtra(EXTRA_PHOTO_INTERVAL, 4f)
        val startIndex = intent.getIntExtra(EXTRA_START_INDEX, 0)

        setContent {
            TheFrameTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PhotoViewScreen(
                        effect = effect,
                        effectDuration = effectDuration,
                        photoInterval = photoInterval,
                        startIndex = startIndex,
                        onNavigateSettings = {
                            startActivity(Intent(this, SettingsActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        },
                        onExit = { finish() }
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_EFFECT = "photo_effect"
        const val EXTRA_EFFECT_DURATION = "photo_effect_duration"
        const val EXTRA_PHOTO_INTERVAL = "photo_interval"
        const val EXTRA_START_INDEX = "photo_start_index"
    }
}

@Composable
private fun PhotoViewScreen(
    effect: PreviewEffect,
    effectDuration: Float,
    photoInterval: Float,
    startIndex: Int,
    onNavigateSettings: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var photoUris by remember { mutableStateOf(emptyList<Uri>()) }
    var currentIndex by remember { mutableIntStateOf(startIndex) }
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        photoUris = loadPhotoUris(context, maxCount = 48)
        if (photoUris.isNotEmpty()) {
            currentIndex = startIndex.coerceIn(0, photoUris.lastIndex)
        }
    }

    LaunchedEffect(photoInterval, photoUris) {
        if (photoUris.isEmpty()) return@LaunchedEffect
        while (true) {
            delay((photoInterval * 1000).toLong())
            currentIndex = (currentIndex + 1) % photoUris.size
        }
    }

    LaunchedEffect(showMenu) {
        if (showMenu) {
            delay(5000)
            showMenu = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ComicBackground)
            .border(4.dp, ComicBlack)
            .clickable { showMenu = !showMenu }
    ) {
        PhotoFrameContent(
            uri = photoUris.getOrNull(currentIndex),
            effect = effect,
            durationMillis = (effectDuration * 1000).toInt()
        )

        OverlayMenu(
            visible = showMenu,
            onSettings = onNavigateSettings,
            onExit = onExit
        )
    }
}

@Composable
private fun PhotoFrameContent(
    uri: Uri?,
    effect: PreviewEffect,
    durationMillis: Int
) {
    val context = LocalContext.current
    var displayBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var currentUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(uri) {
        if (uri == null) {
            currentUri = null
            displayBitmap = null
            return@LaunchedEffect
        }
        currentUri = uri
        val loadedBitmap = withContext(Dispatchers.IO) {
            context.contentResolver.loadThumbnail(uri, android.util.Size(1000, 1000), null)
        }
        if (currentUri == uri && loadedBitmap != null) {
            displayBitmap = loadedBitmap
        }
    }

    val shownBitmap = displayBitmap ?: return

    AnimatedContent(
        targetState = shownBitmap,
        transitionSpec = {
            when (effect) {
                PreviewEffect.Fade -> {
                    fadeIn(tween(durationMillis)) togetherWith fadeOut(tween(durationMillis))
                }
                PreviewEffect.Slide -> {
                    (slideInHorizontally(tween(durationMillis)) { it } + fadeIn(tween(durationMillis)))
                        .togetherWith(slideOutHorizontally(tween(durationMillis)) { -it } + fadeOut(tween(durationMillis)))
                }
                PreviewEffect.Zoom -> {
                    (scaleIn(tween(durationMillis), initialScale = 0.92f) + fadeIn(tween(durationMillis)))
                        .togetherWith(scaleOut(tween(durationMillis), targetScale = 0.92f) + fadeOut(tween(durationMillis)))
                }
            }
        }
    ) { targetBitmap ->
        Image(
            bitmap = targetBitmap.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun OverlayMenu(
    visible: Boolean,
    onSettings: () -> Unit,
    onExit: () -> Unit
) {
    AnimatedContent(
        targetState = visible,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { show ->
        if (show) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ComicMint.copy(alpha = 0.5f))
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ComicCircleButton(
                        icon = Icons.Rounded.Settings,
                        label = stringResource(id = R.string.photo_overlay_settings),
                        onClick = onSettings
                    )
                    ComicCircleButton(
                        icon = Icons.Rounded.Close,
                        label = stringResource(id = R.string.photo_overlay_exit),
                        onClick = onExit
                    )
                }
            }
        }
    }
}

@Composable
private fun ComicCircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .comicShadow(6.dp, 36.dp)
                .background(ComicYellow, CircleShape)
                .border(3.dp, ComicBlack, CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = ComicBlack
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = ComicBlack
        )
    }
}

private suspend fun loadPhotoUris(
    context: android.content.Context,
    maxCount: Int
): List<Uri> = withContext(Dispatchers.IO) {
    val uris = mutableListOf<Uri>()
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
    context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext() && uris.size < maxCount) {
            val id = cursor.getLong(idColumn)
            val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            uris.add(uri)
        }
    }
    uris
}

@Preview(showBackground = true)
@Composable
private fun PhotoViewPreview() {
    TheFrameTheme {
        Surface {
            PhotoViewScreen(
                effect = PreviewEffect.Fade,
                effectDuration = 1.2f,
                photoInterval = 4f,
                startIndex = 0,
                onNavigateSettings = {},
                onExit = {}
            )
        }
    }
}
