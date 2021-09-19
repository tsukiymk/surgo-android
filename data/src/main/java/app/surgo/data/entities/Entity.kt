package app.surgo.data.entities

interface AppEntity

interface IndexedEntity : AppEntity {
    val id: Long
}
