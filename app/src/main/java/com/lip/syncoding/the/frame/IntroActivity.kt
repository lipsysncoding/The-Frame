package com.lip.syncoding.the.frame

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lip.syncoding.the.frame.ui.theme.ComicBackground
import com.lip.syncoding.the.frame.ui.theme.ComicBlack
import com.lip.syncoding.the.frame.ui.theme.ComicCoral
import com.lip.syncoding.the.frame.ui.theme.ComicMint
import com.lip.syncoding.the.frame.ui.theme.ComicPink
import com.lip.syncoding.the.frame.ui.theme.ComicSkyBlue
import com.lip.syncoding.the.frame.ui.theme.ComicYellow
import com.lip.syncoding.the.frame.ui.theme.TheFrameTheme
import com.lip.syncoding.the.frame.ui.theme.comicShadow

class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheFrameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ComicBackground
                ) {
                    IntroScreen(
                        onPermissionGranted = {
                            startActivity(Intent(this, SettingsActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun IntroScreen(
    onPermissionGranted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var permissionDenied by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted || hasPhotoPermission(context)) {
            permissionDenied = false
            onPermissionGranted()
        } else {
            permissionDenied = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        IntroTopSection()
        IntroInfoSection()
        if (shouldShowPhotoPermissionRationale(context)) {
            PermissionRationaleCard()
        }
        if (permissionDenied) {
            PermissionDeniedCard(
                onOpenSettings = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }
            )
        }
        IntroActionSection(
            onGetStarted = {
                if (hasPhotoPermission(context)) {
                    onPermissionGranted()
                } else {
                    permissionDenied = false
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
        )
    }
}

@Composable
private fun PermissionRationaleCard() {
    ComicCard(
        backgroundColor = ComicSkyBlue,
        title = stringResource(id = R.string.intro_permission_rationale_title),
        body = stringResource(id = R.string.intro_permission_rationale_body)
    )
}

@Composable
private fun PermissionDeniedCard(onOpenSettings: () -> Unit) {
    ComicCard(
        backgroundColor = ComicYellow,
        title = stringResource(id = R.string.intro_permission_denied_title),
        body = stringResource(id = R.string.intro_permission_denied_body)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        ComicOutlineButton(
            text = stringResource(id = R.string.intro_permission_denied_action),
            onClick = onOpenSettings
        )
    }
}

private fun hasPhotoPermission(context: android.content.Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_MEDIA_IMAGES
    ) == PackageManager.PERMISSION_GRANTED
}

private fun shouldShowPhotoPermissionRationale(context: android.content.Context): Boolean {
    val activity = context as? Activity ?: return false
    return ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.READ_MEDIA_IMAGES
    )
}

@Composable
private fun IntroTopSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(148.dp)
                .comicShadow(8.dp, 22.dp)
                .background(ComicMint, RoundedCornerShape(28.dp))
                .border(3.dp, ComicBlack, RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Photo,
                contentDescription = stringResource(id = R.string.intro_logo_description),
                tint = ComicBlack,
                modifier = Modifier.size(72.dp)
            )
        }

        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            ),
            color = ComicBlack
        )

        Text(
            text = stringResource(id = R.string.intro_tagline),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = ComicBlack
        )
    }
}

@Composable
private fun IntroInfoSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ComicCard(
            backgroundColor = ComicSkyBlue,
            title = stringResource(id = R.string.intro_feature_title),
            body = stringResource(id = R.string.intro_feature_body)
        )

        ComicCard(
            backgroundColor = ComicPink,
            title = stringResource(id = R.string.intro_permission_title),
            body = stringResource(id = R.string.intro_permission_body)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            PermissionRow(
                icon = Icons.Rounded.Collections,
                label = stringResource(id = R.string.intro_permission_local)
            )
            Spacer(modifier = Modifier.height(8.dp))
            PermissionRow(
                icon = Icons.Rounded.Cloud,
                label = stringResource(id = R.string.intro_permission_google)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.intro_permission_allow_hint),
                style = MaterialTheme.typography.bodySmall,
                color = ComicBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            PermissionBadge(text = stringResource(id = R.string.intro_permission_safety_badge))
        }
    }
}

@Composable
private fun PermissionBadge(text: String) {
    Box(
        modifier = Modifier
            .background(ComicYellow, RoundedCornerShape(14.dp))
            .border(2.dp, ComicBlack, RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = ComicBlack, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun IntroActionSection(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ComicPrimaryButton(
            text = stringResource(id = R.string.intro_get_started),
            onClick = onGetStarted
        )
        Text(
            text = stringResource(id = R.string.intro_footer_hint),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = ComicBlack
        )
    }
}

@Composable
private fun ComicCard(
    backgroundColor: androidx.compose.ui.graphics.Color,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    extraContent: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .comicShadow(6.dp, 20.dp)
            .background(backgroundColor, RoundedCornerShape(22.dp))
            .border(2.dp, ComicBlack, RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = ComicBlack
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = ComicBlack
        )
        extraContent()
    }
}

@Composable
private fun PermissionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(ComicYellow, RoundedCornerShape(12.dp))
                .border(2.dp, ComicBlack, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ComicBlack
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = ComicBlack
        )
    }
}

@Composable
private fun ComicPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = MutableInteractionSource()
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (pressed) 0.96f else 1f, label = "pressScale")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .comicShadow(6.dp, 22.dp)
            .background(ComicCoral, RoundedCornerShape(24.dp))
            .border(3.dp, ComicBlack, RoundedCornerShape(24.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .graphicsLayer(scaleX = scale, scaleY = scale),
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

@Preview(showBackground = true)
@Composable
private fun IntroScreenPreview() {
    TheFrameTheme {
        Surface(color = ComicBackground) {
            IntroScreen(onPermissionGranted = {})
        }
    }
}
