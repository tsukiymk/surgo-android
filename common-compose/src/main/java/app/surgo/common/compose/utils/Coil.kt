package app.surgo.common.compose.utils

import coil.annotation.ExperimentalCoilApi
import coil.intercept.Interceptor
import coil.request.ImageResult
import coil.size.PixelSize
import okhttp3.HttpUrl

@ExperimentalCoilApi
object FormatTextInterceptor : Interceptor {
    private fun format(text: String, args: Map<String, String>): String {
        val builder = StringBuilder()

        var pointer = 0
        val length = text.length
        while (pointer < length) {
            var nextPercent = text.indexOf('{', pointer)
            if (text[pointer] != '{') {
                val plainTextStart = pointer
                val plainTextEnd = if (nextPercent == -1) length else nextPercent
                builder.append(text.substring(plainTextStart, plainTextEnd))
                pointer = plainTextEnd
            } else {
                nextPercent = text.indexOf('}', pointer)
                if (nextPercent != -1) {
                    val specifierTextStart = pointer
                    val specifierTextEnd = nextPercent + 1
                    val key = text.substring(specifierTextStart + 1, specifierTextEnd - 1)
                    args[key]?.let { builder.append(it) }
                    pointer = specifierTextEnd
                } else {
                    pointer++
                }
            }
        }

        return builder.toString()
    }

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val data = chain.request.data
        val size = chain.size

        if (data is String && size is PixelSize) {
            val args = mapOf(
                Pair("w", size.width.toString()),
                Pair("h", size.height.toString()),
                Pair("c", "cc"),
                Pair("f", "webp")
            )
            val url = HttpUrl.get(format(data, args))
                .newBuilder()
                .build()
            val request = chain.request.newBuilder().data(url).build()

            return chain.proceed(request)
        }

        return chain.proceed(chain.request)
    }
}
