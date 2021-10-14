package app.surgo.data

import com.tsukiymk.surgo.openapi.DataSourceFactory

interface DataSourceManager {
    val selectedSource: Long

    operator fun get(key: Long): DataSourceFactory
}
