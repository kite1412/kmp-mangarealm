package view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.rememberImagePainter
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox
import mangarealm.composeapp.generated.resources.Res
import mangarealm.composeapp.generated.resources.no_image
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

// painter with default error handler.
@OptIn(ExperimentalResourceApi::class)
@Composable
fun defaultRememberImagePainter(url: String): Painter {
    return rememberImagePainter(
        url = url,
        errorPainter = { painterResource(Res.drawable.no_image) }
    )
}

@Composable
fun BrowseImage(
    painter: Painter,
    contentDescription: String = "image",
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        Image(
            painter = painter,
            contentScale = contentScale,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BrowseImage(
    imageUrl: String,
    contentDescription: String = "image",
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        BrowseImage(
            painter = rememberImagePainter(
                url = imageUrl,
                errorPainter = { painterResource(Res.drawable.no_image) }
            ),
            contentScale = contentScale,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun BrowseImageNullable(
    painter: Painter?,
    contentDescription: String = "image",
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier = Modifier,
    loading: (@Composable () -> Unit)? = null
) {
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        if (painter == null) {
            if (loading == null)
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            else loading()
        }
        else Image(
            painter = painter,
            contentScale = contentScale,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
}



@Composable
fun Action(
    onClick: () -> Unit,
    fill: Boolean = true,
    verticalPadding: Dp = 2.dp,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val corner = RoundedCornerShape(8.dp)
    val outer: Modifier = if (fill) Modifier.background(color = MaterialTheme.colors.secondary)
    else Modifier.border(
        width = 2.dp,
        color = MaterialTheme.colors.secondary,
        shape = corner
    )
    Box(
        modifier = modifier
            .clip(corner)
            .clickable(onClick = onClick)
            .then(outer)
            .padding(vertical = verticalPadding),
        content = content
    )
}

@Composable
fun PainterLoader(
    url: String,
    onImageLoaded: (Painter) -> Unit,
    modifier: Modifier = Modifier
) {
    AutoSizeBox(url) { action ->
        when (action) {
            is ImageAction.Success -> onImageLoaded(rememberImageSuccessPainter(action))
            else -> Unit
        }
    }
}