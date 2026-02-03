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
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lip.syncoding.the.frame.ui.theme.ComicBackground
import com.lip.syncoding.the.frame.ui.theme.ComicBlack
import com.lip.syncoding.the.frame.ui.theme.ComicCoral
import com.lip.syncoding.the.frame.ui.theme.ComicMint
import com.lip.syncoding.the.frame.ui.theme.ComicPink
import com.lip.syncoding.the.frame.ui.theme.ComicSkyBlue
import com.lip.syncoding.the.frame.ui.theme.ComicYellow
import com.lip.syncoding.the.frame.ui.theme.TheFrameTheme
import com.lip.syncoding.the.frame.ui.theme.comicShadow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheFrameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ComicBackground
                ) {
                    SettingsScreen(
                        onStartFrame = { effect, duration, interval, startIndex ->
                            val intent = Intent(this, PhotoViewActivity::class.java).apply {
                                putExtra(PhotoViewActivity.EXTRA_EFFECT, effect.name)
                                putExtra(PhotoViewActivity.EXTRA_EFFECT_DURATION, duration)
                                putExtra(PhotoViewActivity.EXTRA_PHOTO_INTERVAL, interval)
                                putExtra(PhotoViewActivity.EXTRA_START_INDEX, startIndex)
                            }
                            startActivity(intent)
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        }
                    )
                }
            }
        }
    }
}

private data class EffectOption(
    val label: String,
    val effect: PreviewEffect
)

@Composable
private fun SettingsScreen(
    onStartFrame: (PreviewEffect, Float, Float, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var photoUris by remember { mutableStateOf(emptyList<Uri>()) }
    var previewIndex by remember { mutableIntStateOf(0) }
    val effectOptions = listOf(
        EffectOption(stringResource(id = R.string.settings_effect_fade), PreviewEffect.Fade),
        EffectOption(stringResource(id = R.string.settings_effect_slide), PreviewEffect.Slide),
        EffectOption(stringResource(id = R.string.settings_effect_zoom), PreviewEffect.Zoom)
    )
    var selectedEffect by remember { mutableStateOf(effectOptions.first()) }
    var effectDuration by remember { mutableFloatStateOf(1.2f) }
    var photoInterval by remember { mutableFloatStateOf(4f) }
    var showPow by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        photoUris = loadPhotoUris(context, maxCount = 24)
        previewIndex = 0
    }

    LaunchedEffect(photoInterval, photoUris) {
        if (photoUris.isEmpty()) return@LaunchedEffect
        while (true) {
            delay((photoInterval * 1000).toLong())
            previewIndex = (previewIndex + 1) % photoUris.size
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = stringResource(id = R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = ComicBlack
        )

        GooglePhotosCard()
        LocalPhotosCard(
            photoUris = photoUris,
            onSelectPhoto = { uri ->
                val index = photoUris.indexOf(uri)
                if (index >= 0) {
                    previewIndex = index
                }
            }
        )
        SettingsEffectsCard(
            options = effectOptions,
            selectedEffect = selectedEffect,
            onSelectEffect = { selectedEffect = it },
            effectDuration = effectDuration,
            onEffectDurationChange = { effectDuration = it },
            photoInterval = photoInterval,
            onPhotoIntervalChange = { photoInterval = it }
        )
        PreviewCard(
            previewUris = photoUris,
            previewIndex = previewIndex,
            effect = selectedEffect.effect,
            effectDuration = effectDuration
        )
        ApplySection(
            showPow = showPow,
            onApply = {
                showPow = true
                onStartFrame(selectedEffect.effect, effectDuration, photoInterval, previewIndex)
            }
        )
    }

    LaunchedEffect(showPow) {
        if (showPow) {
            delay(1200)
            showPow = false
        }
    }
}

@Composable
private fun GooglePhotosCard() {
    var isLinked by remember { mutableStateOf(false) }
    val statusText = if (isLinked) {
        stringResource(id = R.string.settings_google_connected)
    } else {
        stringResource(id = R.string.settings_google_not_connected)
    }

    ComicCard(
        backgroundColor = ComicSkyBlue,
        title = stringResource(id = R.string.settings_google_title)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isLinked) Icons.Rounded.Cloud else Icons.Rounded.CloudOff,
                contentDescription = null,
                tint = ComicBlack
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = statusText, color = ComicBlack)
        }
        Spacer(modifier = Modifier.height(12.dp))
        ComicOutlineButton(
            text = if (isLinked) {
                stringResource(id = R.string.settings_google_unlink)
            } else {
                stringResource(id = R.string.settings_google_link)
            },
            onClick = { isLinked = !isLinked }
        )
    }
}

@Composable
private fun LocalPhotosCard(
    photoUris: List<Uri>,
    onSelectPhoto: (Uri) -> Unit
) {
    ComicCard(
        backgroundColor = ComicMint,
        title = stringResource(id = R.string.settings_local_photos_title)
    ) {
        if (photoUris.isEmpty()) {
            Text(
                text = stringResource(id = R.string.settings_local_photos_empty),
                style = MaterialTheme.typography.bodySmall,
                color = ComicBlack
            )
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(photoUris) { uri ->
                    ThumbnailItem(uri = uri, onClick = { onSelectPhoto(uri) })
                }
            }
        }
    }
}

@Composable
private fun ThumbnailItem(
    uri: Uri,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var bitmap by remember(uri) { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(uri) {
        bitmap = withContext(Dispatchers.IO) {
            context.contentResolver.loadThumbnail(uri, android.util.Size(160, 160), null)
        }
    }

    Box(
        modifier = Modifier
            .size(92.dp)
            .comicShadow(4.dp, 16.dp)
            .background(ComicBackground, RoundedCornerShape(16.dp))
            .border(2.dp, ComicBlack, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.Photo,
                contentDescription = null,
                tint = ComicBlack,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun PreviewCard(
    previewUris: List<Uri>,
    previewIndex: Int,
    effect: PreviewEffect,
    effectDuration: Float
) {
    ComicCard(
        backgroundColor = ComicMint,
        title = stringResource(id = R.string.settings_preview_title)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .comicShadow(4.dp, 20.dp)
                .background(ComicBackground, RoundedCornerShape(18.dp))
                .border(2.dp, ComicBlack, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            PreviewImage(
                uri = previewUris.getOrNull(previewIndex),
                effect = effect,
                durationMillis = (effectDuration * 1000).toInt()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.settings_preview_hint),
            style = MaterialTheme.typography.bodySmall,
            color = ComicBlack
        )
    }
}

@Composable
private fun PreviewImage(
    uri: Uri?,
    effect: PreviewEffect,
    durationMillis: Int
) {
    val context = LocalContext.current
    val bitmapCache = remember { mutableStateMapOf<Uri, android.graphics.Bitmap>() }
    var shownUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(uri) {
        if (uri == null) {
            shownUri = null
            return@LaunchedEffect
        }
        val cached = bitmapCache[uri]
        if (cached != null) {
            shownUri = uri
            return@LaunchedEffect
        }
        val loadedBitmap = withContext(Dispatchers.IO) {
            context.contentResolver.loadThumbnail(uri, android.util.Size(600, 600), null)
        }
        if (loadedBitmap != null) {
            bitmapCache[uri] = loadedBitmap
            shownUri = uri
        }
    }

    val displayUri = shownUri
    val displayBitmap = displayUri?.let { bitmapCache[it] }
    if (displayBitmap == null) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.Photo,
                contentDescription = null,
                tint = ComicBlack,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.settings_preview_empty),
                style = MaterialTheme.typography.bodySmall,
                color = ComicBlack
            )
        }
        return
    }

    AnimatedContent(
        targetState = displayUri,
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
    ) { targetUri ->
        val targetBitmap = targetUri?.let { bitmapCache[it] } ?: return@AnimatedContent
        Image(
            bitmap = targetBitmap.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun SettingsEffectsCard(
    options: List<EffectOption>,
    selectedEffect: EffectOption,
    onSelectEffect: (EffectOption) -> Unit,
    effectDuration: Float,
    onEffectDurationChange: (Float) -> Unit,
    photoInterval: Float,
    onPhotoIntervalChange: (Float) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    ComicCard(
        backgroundColor = ComicPink,
        title = stringResource(id = R.string.settings_effects_title)
    ) {
        Text(text = stringResource(id = R.string.settings_effects_label), color = ComicBlack)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .comicShadow(4.dp, 16.dp)
                .background(ComicBackground, RoundedCornerShape(16.dp))
                .border(2.dp, ComicBlack, RoundedCornerShape(16.dp))
                .clickable { menuExpanded = true }
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = ComicBlack
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = selectedEffect.label, color = ComicBlack)
            }
        }
        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option.label) },
                    onClick = {
                        onSelectEffect(option)
                        menuExpanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(id = R.string.settings_effect_duration), color = ComicBlack)
        SliderWithValue(value = effectDuration, onValueChange = onEffectDurationChange)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.settings_photo_interval), color = ComicBlack)
        SliderWithValue(value = photoInterval, onValueChange = onPhotoIntervalChange)
    }
}

@Composable
private fun ApplySection(
    showPow: Boolean,
    onApply: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        ComicPrimaryButton(
            text = stringResource(id = R.string.settings_apply_changes),
            onClick = onApply
        )
        AnimatedVisibility(
            visible = showPow,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Box(
                modifier = Modifier
                    .offset(x = (-6).dp, y = (-10).dp)
                    .background(ComicYellow, RoundedCornerShape(12.dp))
                    .border(2.dp, ComicBlack, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.settings_pow_label),
                    color = ComicBlack,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SliderWithValue(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.5f..8f,
            colors = SliderDefaults.colors(
                thumbColor = ComicCoral,
                activeTrackColor = ComicBlack,
                inactiveTrackColor = ComicBlack.copy(alpha = 0.2f)
            )
        )
        Text(
            text = stringResource(id = R.string.settings_seconds_format, value),
            style = MaterialTheme.typography.bodySmall,
            color = ComicBlack
        )
    }
}

@Composable
private fun ComicCard(
    backgroundColor: Color,
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .comicShadow(6.dp, 22.dp)
            .background(backgroundColor, RoundedCornerShape(22.dp))
            .border(2.dp, ComicBlack, RoundedCornerShape(22.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = ComicBlack
        )
        content()
    }
}

@Composable
private fun ComicPrimaryButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .comicShadow(6.dp, 22.dp)
            .background(ComicCoral, RoundedCornerShape(24.dp))
            .border(3.dp, ComicBlack, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = ComicBlack
        )
    }
}

@Composable
private fun ComicOutlineButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .comicShadow(4.dp, 18.dp)
            .background(ComicBackground, RoundedCornerShape(20.dp))
            .border(2.dp, ComicBlack, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = ComicBlack, fontWeight = FontWeight.Bold)
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
private fun SettingsScreenPreview() {
    TheFrameTheme {
        Surface(color = ComicBackground) {
            SettingsScreen(onStartFrame = { _, _, _, _ -> })
        }
    }
}
