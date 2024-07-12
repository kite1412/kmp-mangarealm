package view

import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.response.ListResponse
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.option.SizeResolver
import com.seiko.imageloader.rememberImagePainter
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox
import kotlinx.coroutines.flow.distinctUntilChanged
import mangarealm.composeapp.generated.resources.Res
import mangarealm.composeapp.generated.resources.no_image
import model.session.Session
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import util.session_handler.SessionHandler

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
    loading: (@Composable BoxScope.() -> Unit)? = null
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (painter == null) {
            if (loading == null)
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center).size(18.dp)
                )
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
    color: Color = MaterialTheme.colors.secondary,
    verticalPadding: Dp = 2.dp,
    horizontalPadding: Dp = 0.dp,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val corner = RoundedCornerShape(8.dp)
    val outer: Modifier = if (fill) Modifier.background(
        color = if (enabled) color else Color.Gray
    )
    else Modifier.border(
        width = 2.dp,
        color = MaterialTheme.colors.secondary,
        shape = corner
    )
    Box(
        modifier = modifier
            .clip(corner)
            .clickable(enabled = enabled, onClick = onClick)
            .then(outer)
            .padding(vertical = verticalPadding, horizontal = horizontalPadding),
        content = content
    )
}

@Composable
fun PainterLoader(
    url: String,
    modifier: Modifier = Modifier,
    onPainterLoaded: (Painter) -> Unit
) {
    AutoSizeBox(ImageRequest {
        data(url)
        this.size(SizeResolver(Size.VisibilityThreshold))
    }) { action ->
        when (action) {
            is ImageAction.Success -> {
                onPainterLoaded(rememberImageSuccessPainter(action))
            }
            else -> Unit
        }
    }
}

@Composable
fun ImageLoader(
    url: String,
    painter: Painter? = null,
    contentDescription: String = "image",
    contentScale: ContentScale = ContentScale.Fit,
    loading: (@Composable BoxScope.() -> Unit)? = null,
    modifier: Modifier = Modifier,
    onPainterLoaded: (Painter) -> Unit
) {
    if (painter == null) PainterLoader(
        url = url,
        onPainterLoaded = onPainterLoaded
    )
    BrowseImageNullable(
        painter = painter,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
        loading = loading
    )
}

@Composable
fun <T, ATTR> SessionPagerColumn(
    session: Session<T, ATTR>,
    handler: SessionHandler<T, ATTR>,
    state: LazyListState = rememberLazyListState(),
    thresholdFactor: Float = 1.2f,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    onSessionLoaded: (Session<T, ATTR>) -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable (Int) -> Unit
) {
    var block = remember { false }
    var finished by remember { mutableStateOf(false) }
    val passThreshold by remember {
        snapshotFlow {
            state.firstVisibleItemIndex + state.layoutInfo.visibleItemsInfo.size >= (session.data.size / thresholdFactor)
        }.distinctUntilChanged()
    }.collectAsState(false)
    if (session.response != ListResponse<ATTR>()) LaunchedEffect(
        key1 = passThreshold,
    ) {
        if (!block && passThreshold) {
            block = true
            handler.updateSession { done, newSession ->
                block = done
                finished = done
                newSession?.let { onSessionLoaded(it) }
            }
        }
    }
    if (session.data.isNotEmpty()) LazyColumn(
        state = state,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        modifier = modifier.fillMaxSize()
    ) {
        items(session.data.size) { content(it) }
        if (!finished) item {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun Warning(
    message: String,
    height: Dp,
    show: Boolean,
    modifier: Modifier = Modifier
) {
    val warningOffset by animateDpAsState(if (show) -(height) else height)
    Box(
        modifier = modifier
            .offset(y = warningOffset)
            .clip(CircleShape)
            .background(MaterialTheme.colors.secondary)
    ) {
        Text(
            message,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun InformationBar(
    label: String,
    background: Color,
    fontSize: TextUnit = 12.sp,
    fontWeight: FontWeight = FontWeight.Medium,
    clip: Shape = RoundedCornerShape(5.dp),
    modifier: Modifier = Modifier
) {
    Text(
        label,
        color = Color.White,
        fontSize = fontSize,
        fontWeight = fontWeight,
        modifier = modifier
            .clip(clip)
            .background(background)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    )
}