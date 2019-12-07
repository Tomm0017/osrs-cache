package gg.rsmod.cache.osrs.config.objtype

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import gg.rsmod.cache.io.ReadOnlyPacket
import gg.rsmod.cache.io.WriteOnlyPacket
import gg.rsmod.cache.osrs.*
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
    var iops = DEFAULT_IOPS
    var ops = DEFAULT_OPS
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
    var shiftClickDropIndex = -1 // TODO get canon name. RL labels this as 'shiftClickDropIndex'
    var recol_s = EMPTY_SHORT_ARRAY
    var recol_d = EMPTY_SHORT_ARRAY
    var retex_s = EMPTY_SHORT_ARRAY
    var retex_d = EMPTY_SHORT_ARRAY
    var countco = EMPTY_INT_ARRAY
    var countobj = EMPTY_INT_ARRAY
    var stringParams: MutableMap<Int, String> = EMPTY_INT_STRING_MAP
    var intParams: MutableMap<Int, Int> = EMPTY_INT_INT_MAP

    override fun encodeType(packet: WriteOnlyPacket) {
        instructions.forEach { (instruction, operation) ->
            val allowWrite = operation.writeCondition(this)
            if (allowWrite) {
                val encodeOp = operation.write
                packet.p1(instruction)
                encodeOp(this, packet)
            }
        }
        packet.p1(0)
    }

    override fun decodeType(packet: ReadOnlyPacket): Result<ObjType, DecodeMessage> {
        while (packet.isReadable) {
            val instruction = packet.g1
            if (instruction == 0) {
                break
            }
            val readOp = instructions.get(instruction)?.read ?: return Err(DecodeMessage.UnhandledInstruction)
            readOp(this, packet)
        }
        return Ok(this)
    }

    override fun postDecode() {
    }

    companion object {

        private val DEFAULT_IOPS = arrayOf(
            null, null, null, null, "Drop"
        )

        private val DEFAULT_OPS = arrayOf(
            null, null, "Take", null, null
        )

        // TODO: get rid of magic numbers for default values.
        val instructions = InstructionMap<ObjType>().apply {

            register(1) {
                read { model = it.g2 }
                write { it.p2(model) }
                    .onlyIf { model != 0 }
            }

            register(2) {
                read { name = it.gjstr }
                write { it.pjstr(name) }
            }

            register(4) {
                read { zoom2d = it.g2 }
                write { it.p2(zoom2d) }
                    .onlyIf { zoom2d != 2000 }
            }

            register(5) {
                read { xan2d = it.g2 }
                write { it.p2(xan2d) }
                    .onlyIf { xan2d != 0 }
            }

            register(6) {
                read { yan2d = it.g2 }
                write { it.p2(yan2d) }
                    .onlyIf { yan2d != 0 }
            }

            register(7) {
                read { xof2d = it.g2s }
                write { it.p2(xof2d) }
                    .onlyIf { xof2d != 0 }
            }

            register(8) {
                read { yof2d = it.g2s }
                write { it.p2(yof2d) }
                    .onlyIf { yof2d != 0 }
            }

            register(11) {
                read { stackable = true }
                writeOnlyWhen { stackable }
            }

            register(12) {
                read { cost = it.g4 }
                write { it.p4(cost) }
                    .onlyIf { cost != 1 }
            }

            register(16) {
                read { members = true }
                writeOnlyWhen { members }
            }

            register(23) {
                read {
                    manwear = it.g2
                    manwearoff = it.g1
                }

                write {
                    it.p2(manwear)
                    it.p1(manwearoff)
                }.onlyIf { manwear != -1 || manwearoff != 0 }
            }

            register(24) {
                read { manwear2 = it.g2 }
                write { it.p2(manwear2) }
                    .onlyIf { manwear2 != -1 }
            }

            register(25) {
                read {
                    womanwear = it.g2
                    womanwearoff = it.g1
                }

                write {
                    it.p2(womanwear)
                    it.p1(womanwearoff)
                }.onlyIf { womanwear != -1 || womanwearoff != 0 }
            }

            register(26) {
                read { womanwear2 = it.g2 }
                write { it.p2(womanwear2) }
                    .onlyIf { womanwear2 != -1 }
            }

            for (i in 30 until 35) {
                register(i) {
                    read {
                        if (ops == DEFAULT_OPS) {
                            ops = Array(5) { null }
                        }
                        val option = it.gjstr
                        ops[i - 30] =
                            if (option.equals("Hidden", ignoreCase = true)) {
                                null
                            } else {
                                option
                            }
                    }

                    write {
                        val option = ops[i - 30] ?: "Hidden"
                        it.pjstr(option)
                    }
                }
            }

            for (i in 35 until 40) {
                register(i) {
                    read {
                        if (iops == DEFAULT_IOPS) {
                            iops = Array(5) { null }
                        }
                        val option = it.gjstr
                        iops[i - 35] =
                            if (option.isBlank()) {
                                null
                            } else {
                                option
                            }
                    }

                    write {
                        val option = iops[i - 35] ?: ""
                        it.pjstr(option)
                    }
                }
            }

            register(40) {
                read {
                    val recolourCount = it.g1
                    recol_s = ShortArray(recolourCount)
                    recol_d = ShortArray(recolourCount)

                    for (i in 0 until recolourCount) {
                        recol_s[i] = it.g2.toShort()
                        recol_d[i] = it.g2.toShort()
                    }
                }

                write {
                    check(recol_s.size == recol_d.size)
                    val count = recol_s.size
                    it.p1(count)
                    for (i in 0 until count) {
                        it.p2(recol_s[i].toInt())
                        it.p2(recol_d[i].toInt())
                    }
                }.onlyIf { recol_s.isNotEmpty() || recol_d.isNotEmpty() }
            }

            register(41) {
                read {
                    val retextureCount = it.g1
                    retex_s = ShortArray(retextureCount)
                    retex_d = ShortArray(retextureCount)

                    for (i in 0 until retextureCount) {
                        retex_s[i] = it.g2.toShort()
                        retex_d[i] = it.g2.toShort()
                    }
                }

                write {
                    check(retex_s.size == retex_d.size)
                    val count = retex_s.size
                    it.p1(count)
                    for (i in 0 until count) {
                        it.p2(retex_s[i].toInt())
                        it.p2(retex_d[i].toInt())
                    }
                }.onlyIf { retex_s.isNotEmpty() || retex_d.isNotEmpty() }
            }

            register(42) {
                read { shiftClickDropIndex = it.g1s }
                write { it.p1(shiftClickDropIndex) }
                    .onlyIf { shiftClickDropIndex != -1 }
            }

            register(65) {
                read { stockmarket = true }
                writeOnlyWhen { stockmarket }
            }

            register(78) {
                read { manwear3 = it.g2 }
                write { it.p2(manwear3) }
                    .onlyIf { manwear3 != -1 }
            }

            register(79) {
                read { womanwear3 = it.g2 }
                write { it.p2(womanwear3) }
                    .onlyIf { womanwear3 != -1 }
            }

            register(90) {
                read { manhead = it.g2 }
                write { it.p2(manhead) }
                    .onlyIf { manhead != -1 }
            }

            register(91) {
                read { womanhead = it.g2 }
                write { it.p2(womanhead) }
                    .onlyIf { womanhead != -1 }
            }

            register(92) {
                read { manhead2 = it.g2 }
                write { it.p2(manhead2) }
                    .onlyIf { manhead2 != -1 }
            }

            register(93) {
                read { womanhead2 = it.g2 }
                write { it.p2(womanhead2) }
                    .onlyIf { womanhead2 != -1 }
            }

            register(95) {
                read { zan2d = it.g2 }
                write { it.p2(zan2d) }
                    .onlyIf { zan2d != 0 }
            }

            register(97) {
                read { certlink = it.g2 }
                write { it.p2(certlink) }
                    .onlyIf { certlink != -1 }
            }

            register(98) {
                read { certtemplate = it.g2 }
                write { it.p2(certtemplate) }
                    .onlyIf { certtemplate != -1 }
            }

            for (i in 100 until 110) {
                register(i) {
                    read {
                        if (countobj.isEmpty()) {
                            countobj = IntArray(10)
                            countco = IntArray(10)
                        }
                        countobj[i - 100] = it.g2
                        countco[i - 100] = it.g2
                    }

                    write {
                        it.p2(countobj[i - 100])
                        it.p2(countco[i - 100])
                    }.onlyIf { countobj.size >= (i - 100) }
                }
            }

            register(110) {
                read { resizex = it.g2 }
                write { it.p2(resizex) }
                    .onlyIf { resizex != 128 }
            }

            register(111) {
                read { resizey = it.g2 }
                write { it.p2(resizey) }
                    .onlyIf { resizey != 128 }
            }

            register(112) {
                read { resizez = it.g2 }
                write { it.p2(resizez) }
                    .onlyIf { resizez != 128 }
            }

            register(113) {
                read { ambient = it.g1s }
                write { it.p1(ambient) }
                    .onlyIf { ambient != 0 }
            }

            register(114) {
                read { contrast = it.g1s }
                write { it.p1(contrast) }
                    .onlyIf { contrast != 0}
            }

            register(115) {
                read { team = it.g1 }
                write { it.p1(team) }
                    .onlyIf { team != 0 }
            }

            register(139) {
                read { boughtlink = it.g2 }
                write { it.p2(boughtlink) }
                    .onlyIf { boughtlink != -1 }
            }

            register(140) {
                read { boughttemplate = it.g2 }
                write { it.p2(boughttemplate) }
                    .onlyIf { boughttemplate != -1 }
            }

            register(148) {
                read { placeholderlink = it.g2 }
                write { it.p2(placeholderlink) }
                    .onlyIf { placeholderlink != -1 }
            }

            register(149) {
                read { placeholdertemplate = it.g2 }
                write { it.p2(placeholdertemplate) }
                    .onlyIf { placeholdertemplate != -1 }
            }

            register(249) {
                read {
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

                write {
                    it.p1(intParams.size + stringParams.size)
                    intParams.forEach { (id, value) ->
                        it.p1(0) // specify that it's not a string
                        it.p3(id)
                        it.p4(value)
                    }
                    stringParams.forEach { (id, value) ->
                        it.p1(1) // specify that it's a string
                        it.p3(id)
                        it.pjstr(value)
                    }
                }.onlyIf { intParams.isNotEmpty() || stringParams.isNotEmpty() }
            }
        }
    }
}