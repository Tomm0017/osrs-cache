package gg.rsmod.cache.osrs

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import gg.rsmod.cache.FileSystem
import gg.rsmod.cache.domain.DomainMessage
import gg.rsmod.cache.osrs.config.Js5Archive
import java.nio.file.Path

internal const val NULL_STRING = "null"
internal val EMPTY_SHORT_ARRAY = ShortArray(0)
internal val EMPTY_INT_ARRAY = IntArray(0)
internal val EMPTY_INT_INT_MAP = mutableMapOf<Int, Int>()
internal val EMPTY_INT_STRING_MAP = mutableMapOf<Int, String>()

fun FileSystem.Companion.buildOsrs(path: Path) = buildOsrs(path.toAbsolutePath().toString())

fun FileSystem.Companion.preloadOsrs(path: Path) = preloadOsrs(path.toAbsolutePath().toString())

fun FileSystem.Companion.buildOsrs(directory: String): Result<FileSystem, DomainMessage> =
    FileSystem.of {
        this.directory = directory
        cipheredArchives {
            set(Js5Archive.MAPS)
        }
    }.build()

fun FileSystem.Companion.preloadOsrs(directory: String): Result<FileSystem, DomainMessage> =
    FileSystem.of {
        this.directory = directory
        cipheredArchives {
            set(Js5Archive.MAPS)
        }
    }.build().andThen { it.loadFully() }