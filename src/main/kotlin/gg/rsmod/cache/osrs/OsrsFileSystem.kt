package gg.rsmod.cache.osrs

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import gg.rsmod.cache.FileSystem
import gg.rsmod.cache.domain.DomainMessage
import gg.rsmod.cache.osrs.config.Js5Archive

internal const val EMPTY_STRING = ""
internal const val NULL_STRING = "null"

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