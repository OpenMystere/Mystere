package io.github.mystere.core

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
