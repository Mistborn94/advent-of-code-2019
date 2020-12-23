package day23

import day5.IntCode
import java.util.concurrent.LinkedBlockingQueue

data class Packet(val x: Long, val y: Long)

class Nat {
    val deliveredYs = mutableSetOf<Long>()

    lateinit var lastPacket: Packet

    fun sendToIntCode(intCode: IntCode): Boolean {
        if (!hasValue()) {
            throw IllegalStateException("Last packed not set")
        }
        intCode.deliver(lastPacket)
        return !deliveredYs.add(lastPacket.y)
    }

    fun receive(x: Long, y: Long) {
        lastPacket = Packet(x, y)
    }

    fun hasValue(): Boolean = ::lastPacket.isInitialized
}


class IntCodeNetwork(program: List<Long>) {

    val nat = Nat()

    val intCodes = (0 until 50).map {
        IntCode(program, LinkedBlockingQueue(listOf(it.toLong())), LinkedBlockingQueue())
    }

    fun solveA(): Int {
        while (!nat.hasValue()) {
            intCodes.forEach { it.runUtilInput() }

            moveOutputs(intCodes)
            fillEmptyInputs(intCodes)
        }
        return nat.lastPacket.y.toInt()
    }

    fun solveB(): Int {
        var redelivered = false
        while (!redelivered) {
            intCodes.forEach { it.runUtilInput() }

            moveOutputs(intCodes)

            if (networkIdle() && nat.hasValue()) {
                redelivered = nat.sendToIntCode(intCodes[0])
            }

            fillEmptyInputs(intCodes)
        }
        return nat.lastPacket.y.toInt()
    }

    private fun moveOutputs(intCodes: List<IntCode>) {
        intCodes.forEach {
            while (it.outputs.isNotEmpty()) {
                val address = it.outputs.take().toInt()
                val x = it.outputs.take()
                val y = it.outputs.take()

                if (address == 255) {
                    nat.receive(x, y)
                } else {
                    intCodes[address].deliver(Packet(x, y))
                }
            }
        }
    }

    fun fillEmptyInputs(intCodes: List<IntCode>) {
        intCodes.forEach {
            if (it.inputs.isEmpty()) {
                it.inputs.add(-1)
            }
        }
    }

    fun networkIdle() = intCodes.all { it.inputs.isEmpty() }
}

private fun IntCode.deliver(packet: Packet) {
    inputs.add(packet.x)
    inputs.add(packet.y)
}

