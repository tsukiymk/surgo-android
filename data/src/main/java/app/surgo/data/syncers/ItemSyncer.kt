package app.surgo.data.syncers

import app.surgo.data.entities.IndexedEntity

class ItemSyncer<LocalType : IndexedEntity, RemoteType, Key>(
    private val insert: suspend (LocalType) -> Long,
    private val update: suspend (LocalType) -> Unit,
    private val delete: suspend (LocalType) -> Int,
    private val localEntityToKey: suspend (LocalType) -> Key?,
    private val remoteEntityToKey: suspend (RemoteType) -> Key?,
    private val remoteToLocalEntity: suspend (RemoteType, Long?) -> LocalType
) {
    suspend fun sync(
        currentValues: Collection<LocalType>,
        remoteValues: Collection<RemoteType>,
        removeNotMatched: Boolean = true
    ): ItemSyncerResult<LocalType> {
        val databaseEntities = ArrayList(currentValues)

        val added = ArrayList<LocalType>()
        val deleted = ArrayList<LocalType>()
        val updated = ArrayList<LocalType>()

        remoteValues.forEach { remoteEntity ->
            val remoteKey = remoteEntityToKey(remoteEntity) ?: return@forEach
            val databaseEntityForId = databaseEntities.find {
                localEntityToKey(it) == remoteKey
            }

            if (databaseEntityForId != null) {
                val entity = remoteToLocalEntity(remoteEntity, databaseEntityForId.id)
                if (databaseEntities != entity) {
                    update(entity)
                }
                databaseEntities.remove(databaseEntityForId)
                updated += entity
            } else {
                added += remoteToLocalEntity(remoteEntity, null)
            }
        }

        if (removeNotMatched) {
            databaseEntities.forEach {
                delete(it)
                deleted += it
            }
        }

        added.forEach { insert(it) }

        return ItemSyncerResult(added, deleted, updated)
    }
}

data class ItemSyncerResult<T>(
    val added: List<T> = emptyList(),
    val deleted: List<T> = emptyList(),
    val updated: List<T> = emptyList()
)
