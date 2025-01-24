package com.sarath.gem.presentation.util.extension

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState

/**
 * Scrolls the `LazyListState` to the bottom, ensuring the last item is fully visible if possible.
 *
 * This function handles the complexities of scrolling in a `LazyList` where items may not be
 * fully visible within the viewport. It first checks if the last item is currently visible.
 *
 * - If the last item is visible, it calculates the remaining scroll offset needed to bring
 *   the bottom of the last item to the bottom of the viewport. It then scrolls by this offset.
 *
 * - If the last item is not visible, it uses `animateScrollToItem` to scroll to the last item,
 *   ensuring it becomes visible.
 *
 * - If there are no items in the list, this function does nothing.
 *
 * @receiver The `LazyListState` instance to perform the scrolling on.
 * @throws IllegalStateException If the underlying `LazyLayoutInfo` is not available, which typically means
 * the `LazyList` hasn't been laid out yet.
 */
suspend fun LazyListState.scrollToBottom() {
    val totalItemsCount = layoutInfo.totalItemsCount
    if (totalItemsCount > 0) {
        val lastItemIndex = totalItemsCount - 1
        val lastItem = layoutInfo.visibleItemsInfo.find { it.index == lastItemIndex }

        if (lastItem != null) {
            val scrollOffset = (lastItem.offset + lastItem.size) - layoutInfo.viewportEndOffset
            if (scrollOffset > 0) {
                scrollBy(scrollOffset.toFloat())
            }
        } else {
            animateScrollToItem(lastItemIndex, Int.MAX_VALUE)
        }
    }
}
