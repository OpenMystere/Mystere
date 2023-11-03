package io.github.mystere

import dev.whyoleg.cryptography.algorithms.asymmetric.RSA

/**
 * @author Madray Haven
 * @Date 2023/10/19 11:07
 */
actual val Platform: PlatformType = PlatformType.JVM

fun main() {
    RSA.OAEP
}