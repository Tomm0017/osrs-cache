package gg.rsmod.cache.osrs

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import gg.rsmod.cache.io.ReadOnlyPacket
import gg.rsmod.cache.io.WriteOnlyPacket
import gg.rsmod.cache.osrs.config.ConfigType
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

class InstructionMap<T : ConfigType> :
    Int2ObjectOpenHashMap<InstructionMap.Operation<T>>() {

    fun register(
        instruction: Int,
        init: OperationBuilder<T>.() -> Unit
    ): RegisterMessage {
        val message = if (containsKey(instruction)) {
            RegisterMessage.Replace
        } else {
            RegisterMessage.Put
        }
        put(instruction, OperationBuilder<T>().apply(init).build())
        return message
    }

    fun deregister(
        instruction: Int
    ): Result<Operation<T>, RegisterMessage> {
        val op = remove(instruction)
        return if (op != null) {
            Ok(op)
        } else {
            Err(RegisterMessage.NotFound)
        }
    }

    sealed class RegisterMessage {
        object Put : RegisterMessage()
        object Replace : RegisterMessage()
        object NotFound : RegisterMessage()
    }

    data class Operation<T : ConfigType>(
        val read: (T).(ReadOnlyPacket) -> Unit,
        val write: (T).(WriteOnlyPacket) -> Unit,
        val writeCondition: (T).() -> Boolean
    )

    @DslMarker
    private annotation class OperationDsl

    @OperationDsl
    class OperationBuilder<T : ConfigType> {

        private lateinit var read: (T).(ReadOnlyPacket) -> Unit

        private lateinit var write: (T).(WriteOnlyPacket) -> Unit

        private val writeCondition = ConditionBuilder<T>()

        fun read(read: (T).(ReadOnlyPacket) -> Unit) {
            this.read = read
        }

        fun write(write: (T).(WriteOnlyPacket) -> Unit): ConditionBuilder<T> {
            this.write = write
            return writeCondition
        }

        fun writeOnlyWhen(condition: (T).() -> Boolean) {
            write = { }
            writeCondition.condition = condition
        }

        fun build(): Operation<T> = Operation(read, write, writeCondition.condition)
    }

    @OperationDsl
    class ConditionBuilder<T : ConfigType> {

        internal var condition: (T).() -> Boolean = { true }

        fun onlyIf(condition: (T).() -> Boolean) {
            this.condition = condition
        }
    }
}