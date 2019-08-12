package gg.rsmod.cache.osrs

sealed class DecodeMessage(val reason: String) {
    object UnhandledInstruction : DecodeMessage("Unhandled instruction")
}