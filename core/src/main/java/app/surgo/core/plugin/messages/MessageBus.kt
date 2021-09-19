package app.surgo.core.plugin.messages

import kotlinx.coroutines.flow.MutableSharedFlow

class MessageBus {
    val subscribeDataSource = MutableSharedFlow<Long>()
}
