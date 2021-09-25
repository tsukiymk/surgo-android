package app.surgo.shared.plugin

import com.tsukiymk.surgo.openapi.DataSourceFactory

interface DataSourceManager {
    val key: Long
    val factory: DataSourceFactory

    val map: Map<Long, DataSourceFactory>
}
