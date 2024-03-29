package gg.rsmod.cache.osrs.config

import com.github.michaelbull.result.Result
import gg.rsmod.cache.io.ReadOnlyPacket
import gg.rsmod.cache.io.WriteOnlyPacket
import gg.rsmod.cache.osrs.DecodeMessage

interface ConfigType {

    fun encodeType(packet: WriteOnlyPacket)

    fun decodeType(packet: ReadOnlyPacket): Result<ConfigType, DecodeMessage>

    fun postDecode()
}