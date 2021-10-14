package app.surgo.common.compose.utils

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun notifyOffsetAsState(
    visibleItemsInfo: List<LazyListItemInfo>,
    notifyTrigger: Int
): State<Boolean> {
    val visible = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(visibleItemsInfo) {
        visible.value = when {
            visibleItemsInfo.isEmpty() -> false
            notifyTrigger <= 0 -> false
            else -> {
                val firstVisibleItem = visibleItemsInfo[0]
                when {
                    firstVisibleItem.index > 0 -> true
                    else -> firstVisibleItem.size + firstVisibleItem.offset <= notifyTrigger
                }
            }
        }
    }

    return visible
}
