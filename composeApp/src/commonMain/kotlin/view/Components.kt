package view

import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.util.getTagList
import api.mangadex.util.getTitle
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.option.SizeResolver
import com.seiko.imageloader.rememberImagePainter
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox
import mangarealm.composeapp.generated.resources.Res
import mangarealm.composeapp.generated.resources.no_image
import model.session.Session
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import util.session_handler.SessionHandler

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
                    modifier = Modifier.size(18.dp).align(Alignment.Center)
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



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Action(
    onClick: () -> Unit,
    fill: Boolean = true,
    color: Color = MaterialTheme.colors.secondary,
    verticalPadding: Dp = 2.dp,
    horizontalPadding: Dp = 0.dp,
    enabled: Boolean = true,
    borderWidth: Dp = 2.dp,
    corner: Shape = RoundedCornerShape(8.dp),
    onDoubleClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val outer: Modifier = if (fill) Modifier.background(
        color = if (enabled) color else Color.Gray
    )
    else Modifier.border(
        width = borderWidth,
        color = color,
        shape = corner
    )
    Box(
        modifier = modifier
            .clip(corner)
            .combinedClickable(
                enabled = enabled,
                onClick = onClick,
                onDoubleClick = onDoubleClick
            )
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
    enableLoadNew: Boolean = true,
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
    var finished by remember { mutableStateOf(false) }
    var triggerLoad by remember { mutableStateOf(false) }
    if (enableLoadNew && !finished) {
        val passThreshold by remember {
            derivedStateOf {
                state.firstVisibleItemIndex + state.layoutInfo.visibleItemsInfo.size >= (session.data.size / thresholdFactor)
            }
        }
        if (session.response != ListResponse<ATTR>()) LaunchedEffect(
            keys = arrayOf(passThreshold, triggerLoad)
        ) {
            if (passThreshold) {
                handler.updateSession { done, newSession ->
                    finished = done
                    triggerLoad = !triggerLoad
                    newSession?.let { onSessionLoaded(it) }
                }
            }
        }
    } else finished = true
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
        items(
            count = session.data.size,
            key = { it }
        ) { content(it) }
        if (!finished) item {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T, ATTR> SessionPagerVerticalPager(
    session: Session<T, ATTR>,
    handler: SessionHandler<T, ATTR>,
    state: PagerState,
    subtrahend: Int = if (session.data.size / 2 >= 10) 5 else 2,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    pageSize: PageSize = PageSize.Fill,
    beyondBoundsPageCount: Int = PagerDefaults.BeyondBoundsPageCount,
    pageSpacing: Dp = 0.dp,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    flingBehavior: SnapFlingBehavior = PagerDefaults.flingBehavior(state = state),
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    key: ((index: Int) -> Any)? = null,
    pageNestedScrollConnection: NestedScrollConnection = remember(state) {
        PagerDefaults.pageNestedScrollConnection(state, Orientation.Vertical)
    },
    onSessionLoaded: (Session<T, ATTR>) -> Unit,
    modifier: Modifier = Modifier,
    pageContent: @Composable PagerScope.(page: Int) -> Unit
) {
    var finished by remember { mutableStateOf(false) }
    if (!finished) {
        val loadNew by remember {
            derivedStateOf {
                state.settledPage >= ((session.data.size - 1) - subtrahend)
            }
        }
        var triggerLoad by remember { mutableStateOf(false) }
        if (loadNew) LaunchedEffect(triggerLoad) {
            handler.updateSession { isFinished, newSession ->
                finished = isFinished
                triggerLoad = !triggerLoad
                newSession?.let(onSessionLoaded)
            }
        }
    }
    VerticalPager(
        state = state,
        contentPadding = contentPadding,
        pageSize = pageSize,
        beyondBoundsPageCount = beyondBoundsPageCount,
        pageSpacing = pageSpacing,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        key = key,
        pageNestedScrollConnection = pageNestedScrollConnection,
        modifier = modifier,
        pageContent = pageContent
    )
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

@Composable
fun PaintersLoader(
    urls: List<String>,
    modifier: Modifier = Modifier,
    onPainterLoaded: (Int, Painter) -> Unit
) {
    for ((i, url) in urls.withIndex()) {
        PainterLoader(url) {
            onPainterLoaded(i, it)
        }
    }
}

@Composable
fun Title(
    manga: MangaAttributes,
    textAlign: TextAlign? = null,
    titleLines: Int = 0,
    textLines: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var fontSize by remember { mutableStateOf(28) }
    var maxLines by remember { mutableStateOf(2) }
    Text(
        getTitle(manga.title),
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        fontSize = fontSize.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        textAlign = textAlign,
        onTextLayout = {
            if (it.hasVisualOverflow) {
                fontSize--
                if (fontSize <= 24) {
                    if (titleLines > 0) maxLines = titleLines else maxLines++
                }
            }
            textLines(it.lineCount)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Tags(
    data: MangaAttributes,
    horizontalAlignment: Alignment.Horizontal? = null,
    verticalAlignment: Alignment.Vertical? = null,
    fontColor: Color = Color.Black,
    fontSize: TextUnit = 10.sp,
    horizontalPadding: Dp = 2.dp,
    verticalPadding: Dp = 1.dp,
    cornerRadius: Dp = 3.dp,
    modifier: Modifier = Modifier
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            space = 2.dp,
            alignment = horizontalAlignment ?: Alignment.Start
        ),
        verticalArrangement = Arrangement.spacedBy(
            space = 2.dp,
            alignment = verticalAlignment ?: Alignment.Top
        ),
    ) {
        getTagList(data).forEach {
            TagBar(
                tag = it,
                fontColor = fontColor,
                fontSize = fontSize,
                horizontalPadding = horizontalPadding,
                verticalPadding = verticalPadding,
                cornerRadius = cornerRadius
            )
        }
    }
}

@Composable
fun TagBar(
    tag: String,
    fontColor: Color = Color.Black,
    fontSize: TextUnit = 10.sp,
    horizontalPadding: Dp = 2.dp,
    verticalPadding: Dp = 1.dp,
    cornerRadius: Dp = 3.dp,
    modifier: Modifier = Modifier
) {
    Text(
        tag,
        fontSize = fontSize,
        color = fontColor,
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color(229, 228, 226, 100))
            .padding(vertical = verticalPadding, horizontal = horizontalPadding)
    )
}

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    message: @Composable (ColumnScope.() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
        ) {
            CircularProgressIndicator(color = Color.White)
            message?.invoke(this)
        }
    }
}