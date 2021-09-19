package app.surgo.shared

import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.fresh
import com.dropbox.android.external.store4.get

suspend fun <Key : Any, Output : Any> Store<Key, Output>.fetch(
    key: Key,
    forceFresh: Boolean = true
): Output = when {
    forceFresh -> fresh(key)
    else -> get(key)
}
