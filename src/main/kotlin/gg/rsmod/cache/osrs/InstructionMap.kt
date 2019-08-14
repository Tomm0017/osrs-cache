package gg.rsmod.cache.osrs

import gg.rsmod.cache.io.ReadOnlyPacket
import gg.rsmod.cache.osrs.config.ConfigType
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

class InstructionMap<T : ConfigType> : Int2ObjectOpenHashMap<(T).(ReadOnlyPacket) -> Unit>() {

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