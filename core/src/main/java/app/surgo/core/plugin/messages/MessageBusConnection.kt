package app.surgo.core.plugin.messages

interface MessageBusConnection {
    fun <L> subscribe(topic: Topic<L>)
}
