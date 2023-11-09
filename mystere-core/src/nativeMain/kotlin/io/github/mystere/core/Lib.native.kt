package io.github.mystere.core

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

@OptIn(ExperimentalNativeApi::class)
actual val isDebugBinary: Boolean = Platform.isDebugBinary