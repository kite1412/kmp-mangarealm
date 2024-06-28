package view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.seiko.imageloader.rememberImagePainter
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
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        if (painter == null) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        else Image(
            painter = painter,
            contentScale = contentScale,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
}