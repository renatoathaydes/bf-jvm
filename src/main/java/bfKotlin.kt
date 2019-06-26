@file:JvmName("BfKotlin")

import java.io.File
import java.io.IOException

sealed class Op {
    class Inc(val v: Int) : Op()
    class Move(val v: Int) : Op()
    class Loop(val loop: Array<Op>) : Op()
    object Print : Op()
}

class Tape {
    private var tape: IntArray = IntArray(1)
    private var pos: Int = 0

    fun get(): Int {
        return tape[pos]
    }

    fun inc(x: Int) {
        tape[pos] += x
    }

    fun move(x: Int) {
        pos += x
        while (pos >= tape.size) {
            val tape = IntArray(this.tape.size * 2)
            System.arraycopy(this.tape, 0, tape, 0, this.tape.size)
            this.tape = tape
        }
    }
}

class Program(code: String) {
    private val ops: Array<Op>

    init {
        val it = code.iterator()
        ops = parse(it)
    }

    private fun parse(it: CharIterator): Array<Op> {
        val res = arrayListOf<Op>()
        while (it.hasNext()) {
            when (it.next()) {
                '+' -> res.add(Op.Inc(1))
                '-' -> res.add(Op.Inc(-1))
                '>' -> res.add(Op.Move(1))
                '<' -> res.add(Op.Move(-1))
                '.' -> res.add(Op.Print)
                '[' -> res.add(Op.Loop(parse(it)))
                ']' -> return res.toTypedArray()
            }
        }
        return res.toTypedArray()
    }

    fun run() {
        val tape = Tape()
        _run(ops, tape)
    }

    private fun _run(program: Array<Op>, tape: Tape) {
        for (op in program) {
            when (op) {
                is Op.Inc -> tape.inc(op.v)
                is Op.Move -> tape.move(op.v)
                is Op.Loop -> while (tape.get() > 0) {
                    _run(op.loop, tape)
                }
                is Op.Print -> print(tape.get().toChar())
            }
        }
    }
}

@Throws(IOException::class)
fun main(args: Array<String>) {
    val code = File(args[0]).readText()

    val startTime = System.currentTimeMillis()
    val program = Program(code)
    program.run()
    System.err.printf("time: %.3fs\n", (System.currentTimeMillis() - startTime) / 1e3)
}
