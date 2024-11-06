package com.razaghimahdi.compose_persian_date.core.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfiniteHorizontalPager(
    pageCount: Int,
    modifier: Modifier = Modifier,
    pagerPositionIndex: Int,
    pagerState: PagerState,
    onPageChanged: ((index: Int) -> Unit)? = null,
    content: @Composable() (PagerScope.(page: Int) -> Unit),
) {
    if (pageCount == 0) return


    LaunchedEffect(pagerPositionIndex, pageCount) {
        pagerState.scrollToPage(pagerPositionIndex)
    }

    if (onPageChanged != null) {
        LaunchedEffect(pagerState, pageCount) {
            snapshotFlow { pagerState.currentPage }.collect { index ->
                onPageChanged(index % pageCount)
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = pageCount > 1,
        modifier = modifier,
    ) { index ->
        content(index % pageCount)
    }
}