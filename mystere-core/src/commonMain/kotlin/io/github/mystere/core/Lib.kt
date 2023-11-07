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