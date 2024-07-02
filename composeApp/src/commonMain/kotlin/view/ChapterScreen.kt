package view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.ChapterAttributes
import cafe.adriel.voyager.core.screen.Screen
import util.undoEdgeToEdge
import viewmodel.ChapterViewModel

class ChapterScreen : Screen {
    @Composable
    override fun Content() {
        val vm = remember { ChapterViewModel() }
        undoEdgeToEdge()
        Scaffold {
            LazyColumn(
                modifier = Modifier.padding(8.dp)
            ) {
                items(vm.chapters) {
                    ChapterBar(it)
                }
            }
        }
    }

    @Composable
    private fun ChapterBar(
        chapter: Data<ChapterAttributes>,
        modifier: Modifier = Modifier
    ) {
        Column (
            modifier = modifier
                .padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Text(
                "Chapter ${chapter.attributes.chapter}",
                fontSize = 14.sp
            )
            Spacer(Modifier.fillMaxWidth().height(1.dp).background(Color.Black))
        }
    }
}