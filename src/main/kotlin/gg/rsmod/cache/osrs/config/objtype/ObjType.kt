package gg.rsmod.cache.osrs.config.objtype

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getError
import gg.rsmod.cache.io.ReadOnlyPacket
import gg.rsmod.cache.osrs.DecodeMessage
import gg.rsmod.cache.osrs.EMPTY_STRING
import gg.rsmod.cache.osrs.InstructionMap
import gg.rsmod.cache.osrs.NULL_STRING
import gg.rsmod.cache.osrs.config.ConfigType

open class ObjType : ConfigType {

    var name: String = NULL_STRING
    var members: Boolean = false
    var stackable: Boolean = false
    var stockmarket: Boolean = false
    var cost = 1
    var certlink = -1
    var certtemplate = -1
    var boughtlink = -1
    var boughttemplate = -1
    var placeholderlink = -1
    var placeholdertemplate = -1
    lateinit var iops: Array<String?>
    lateinit var ops: Array<String?>
    var team = 0
    var model = 0
    var xof2d = 0
    var yof2d = 0
    var yan2d = 0
    var xan2d = 0
    var zan2d = 0
    var zoom2d = 2000
    var resizex = 128
    var resizey = 128
    var resizez = 128
    var ambient = 0
    var contrast = 0
    var manwearoff = 0
    var womanwearoff = 0
    var manwear = -1
    var manwear2 = -1
    var manwear3 = -1
    var manhead = -1
    var manhead2 = -1
    var womanwear = -1
    var womanwear2 = -1
    var womanwear3 = -1
    var womanhead = -1
    var womanhead2 = -1
    lateinit var recol_s: ShortArray
    lateinit var recol_d: ShortArray
    lateinit var retex_s: ShortArray
    lateinit var retex_d: ShortArray
    lateinit var countco: IntArray
    lateinit var countobj: IntArray
    lateinit var stringParams: MutableMap<Int, String>
    lateinit var intParams: MutableMap<Int, Int>

    val hasOps: Boolean
        get() = ::ops.isInitialized

    val hasIOps: Boolean
        get() = ::iops.isInitialized

    val hasRecol: Boolean
        get() = ::recol_s.isInitialized

    val hasRetex: Boolean
        get() = ::retex_s.isInitialized

    val hasCountCo: Boolean
        get() = ::countobj.isInitialized

    val hasStrParams: Boolean
        get() = ::stringParams.isInitialized

    val hasIntParams: Boolean
        get() = ::intParams.isInitialized

    val isNoted: Boolean
        get() = certtemplate != -1

    val getDenotedId: Int
        get() = certlink

    val isBought: Boolean
        get() = boughttemplate != -1

    val getNonBoughtId: Int
        get() = boughtlink

    val isPlaceholder: Boolean
        get() = placeholdertemplate != -1

    val getNonPlaceholderId: Int
        get() = placeholderlink

    override fun decodeType(packet: ReadOnlyPacket): Result<ObjType, DecodeMessage> {
        while (packet.isReadable) {
            val instruction = packet.g1
            if (instruction == 0) {
                break
            }
            val result = decode(packet, instruction)
            val err = result.getError()
            if (err != null) {
                return Err(err)
            }
        }
        return Ok(this)
    }

    override fun postDecode() {
    }

    private fun decode(packet: ReadOnlyPacket, instruction: Int): Result<ObjType, DecodeMessage> {
        val onRead = instructions.get(instruction) ?: return Err(DecodeMessage.UnhandledInstruction)
        onRead.invoke(this, packet)
        return Ok(this)
    }

    companion object {
        val instructions = InstructionMap<ObjType>()

        init {
            instructions.apply {
                register(1) { model = it.g2 }
                register(2) { name = it.gjstr }
                register(4) { zoom2d = it.g2 }
                register(5) { xan2d = it.g2 }
                register(6) { yan2d = it.g2 }
                register(7) { xof2d = it.g2s }
                register(8) { yof2d = it.g2s }
                register(11) { stackable = true }
                register(12) { cost = it.g4 }
                register(16) { members = true }
                register(23) {
                    manwear = it.g2
                    manwearoff = it.g1
                }
                register(24) { manwear2 = it.g2 }
                register(25) {
                    womanwear = it.g2
                    womanwearoff = it.g1
                }
                register(26) { womanwear2 = it.g2 }
                for (i in 30 until 35) {
                    register(i) {
                        if (!::ops.isInitialized) {
                            ops = Array(5) { EMPTY_STRING }
                        }
                        val option = it.gjstr
                        ops[i - 30] =
                            if (option.equals("Hidden", ignoreCase = true)) {
                                null
                            } else {
                                option
                            }
                    }
                }
                for (i in 35 until 40) {
                    register(i) {
                        if (!::iops.isInitialized) {
                            iops = Array(5) { EMPTY_STRING }
                        }
                        iops[i - 35] = it.gjstr
                    }
                }
                register(40) {
                    val recolourCount = it.g1
                    recol_s = ShortArray(recolourCount)
                    recol_d = ShortArray(recolourCount)

                    for (i in 0 until recolourCount) {
                        recol_s[i] = it.g2.toShort()
                        recol_d[i] = it.g2.toShort()
                    }
                }
                register(41) {
                    val retextureCount = it.g1
                    retex_s = ShortArray(retextureCount)
                    retex_d = ShortArray(retextureCount)

                    for (i in 0 until retextureCount) {
                        retex_s[i] = it.g2.toShort()
                        retex_d[i] = it.g2.toShort()
                    }
                }
                register(42) { it.g1s } // TODO. RL labels this as 'shiftClickDropIndex'
                register(65) { stockmarket = true }
                register(78) { manwear3 = it.g2 }
                register(79) { womanwear3 = it.g2 }
                register(90) { manhead = it.g2 }
                register(91) { womanhead = it.g2 }
                register(92) { manhead2 = it.g2 }
                register(93) { womanhead2 = it.g2 }
                register(95) { zan2d = it.g2 }
                register(97) { certlink = it.g2 }
                register(98) { certtemplate = it.g2 }
                for (i in 100 until 110) {
                    register(i) {
                        if (!::countobj.isInitialized) {
                            countobj = IntArray(10)
                            countco = IntArray(10)
                        }
                        countobj[i - 100] = it.g2
                        countco[i - 100] = it.g2
                    }
                }
                register(110) { resizex = it.g2 }
                register(111) { resizey = it.g2 }
                register(112) { resizez = it.g2 }
                register(113) { ambient = it.g1s }
                register(114) { contrast = it.g1s }
                register(115) { team = it.g1 }
                register(139) { boughtlink = it.g2 }
                register(140) { boughttemplate = it.g2 }
                register(148) { placeholderlink = it.g2 }
                register(149) { placeholdertemplate = it.g2 }
                register(249) {
                    intParams = mutableMapOf()
                    stringParams = mutableMapOf()

                    val paramCount = it.g1
                    repeat(paramCount) { _ ->
                        val isString = it.g1 == 1
                        val id = it.g3
                        if (isString) {
                            stringParams[id] = it.gjstr
                        } else {
                            intParams[id] = it.g4
                        }
                    }
                }
            }
        }
    }
}