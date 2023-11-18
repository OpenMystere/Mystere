import org.gradle.api.Project
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*


val Project.MYSTERE_LIB: String get() {
    return findProperty("mystere.lib.version")!!.toString()
}
val Project.MYSTERE_APP: String get() {
    return findProperty("mystere.app.version")!!.toString()
}
/**
 * git commit id，若当前不为 git 仓库，则返回 TIME_MD5，可用于版本名后缀
 * @see TIME_MD5
 */
val GIT_HEAD: String get() {
    return withRuntime("git rev-parse --short HEAD") {
        inputStream.useFirstLineIfNotBlack() ?: TIME_MD5
    }
}

/**
 * 按照 yyMMdd 的格式，根据当前日期返回一个整数，可用于版本号
 */
val DATED_VERSION: Int @Suppress("SimpleDateFormat") get() {
    return Integer.parseInt(SimpleDateFormat("yyMMdd").format(Date()))
}

/**
 * 按照 yyMMdd 的格式，根据最近一条 git commit 日期返回一个整数，若当前不为 git 仓库，则返回 DATED_VERSION，可用于版本号
 * @see DATED_VERSION
 */
val COMMIT_DATE_VERSION: Int get() {
    return withRuntime("git log -n 1 --pretty=format:%cd --date=format:%y%m%d") {
        inputStream.useFirstLineIfNotBlack()?.toInt() ?: DATED_VERSION
    }
}

/**
 * 返回当前日期的 MD5，截取其中十位，与 git commit id 位数相等，可用于版本名后缀
 */
val TIME_MD5: String get() {
    val digest = MessageDigest.getInstance("MD5")
        .digest(System.currentTimeMillis().toString().toByteArray())
    val pre = BigInteger(1, digest)
    return pre.toString(16).padStart(32, '0').substring(8, 18)
}



inline fun <T> withRuntime(command: String, crossinline block: Process.() -> T): T {
    val process = Runtime.getRuntime().exec(command)
    return block.invoke(process)
}

fun InputStream.useFirstLineIf(block: (String) -> Boolean): String? {
    val firstLine = reader().readLines()
        .takeIf { it.isNotEmpty() }
        ?.get(0)
    return firstLine?.takeIf { block.invoke(it) }
}

fun InputStream.useFirstLineIfNotBlack(): String? {
    return useFirstLineIf {
        it.isNotBlank()
    }
}

