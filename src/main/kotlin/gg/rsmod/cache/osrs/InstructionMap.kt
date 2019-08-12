package gg.rsmod.cache.osrs

import gg.rsmod.cache.io.ReadOnlyPacket
import gg.rsmod.cache.osrs.config.ConfigType
import gnu.trove.map.hash.TIntObjectHashMap

class InstructionMap<T : ConfigType> : TIntObjectHashMap<(T).(ReadOnlyPacket) -> Unit>() {

    fun register(instruction: Int, read: (T).(ReadOnlyPacket) -> Unit): RegisterMessage {
        val message = if (containsKey(instruction)) {
            RegisterMessage.Replace
        } else {
            RegisterMessage.Put
        }
        put(instruction, read)
        return message
    }

    sealed class RegisterMessage {
        object Put : RegisterMessage()
        object Replace : RegisterMessage()
    }
}