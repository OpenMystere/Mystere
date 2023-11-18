package io.github.mystere.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.newCoroutineContext
import kotlin.coroutines.CoroutineContext

/**
 * @author Madray Haven
 * @Date 2023/10/19 11:07
 */
expect val Platform: PlatformType
enum class PlatformType {
    JVM,
    Linux,
    macOS,
    Windows,
}

expect val isDebugBinary: Boolean

object MystereCore {
    private var _debug = isDebugBinary
    val Debug: Boolean get() = _debug
    fun forceDebug() {
        _debug = true
    }
}

val MystereScope: CoroutineScope by lazy {
    CoroutineScope(Dispatchers.IO)
}

fun MystereScope(context: CoroutineContext = Dispatchers.IO): CoroutineScope {
    return CoroutineScope(MystereScope.newCoroutineContext(context))
}
