@file:JvmName("BfKotlin")

import java.io.File
import java.io.IOException
import java.io.PrintStream

sealed class Op {
    class Inc(val v: Int) : Op()
    class Move(val v: Int) : Op()
    class Loop(val loop: Array<Op>) : Op()
    class Print(val out: PrintStream) : Op()
}

class KtTape {
    private var tape: IntArray = IntArray(1)
    private var pos: Int = 0

    val state: IntArray get() = tape.copyOf()

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

class KtProgram(code: String, out: PrintStream) {
    private val ops: Array<Op>
    private val printer = Op.Print(out)

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
                '.' -> res.add(printer)
                '[' -> res.add(Op.Loop(parse(it)))
                ']' -> return res.toTypedArray()
            }
        }
        return res.toTypedArray()
    }

    fun run(): KtTape {
        val tape = KtTape()
        _run(ops, tape)
        return tape
    }

    private fun _run(program: Array<Op>, tape: KtTape) {
        for (op in program) {
            when (op) {
                is Op.Inc -> tape.inc(op.v)
                is Op.Move -> tape.move(op.v)
                is Op.Loop -> while (tape.get() > 0) _run(op.loop, tape)
                is Op.Print -> op.out.print(tape.get().toChar())
            }
        }
    }
}

fun runWithTiming(runnable: () -> Any?) {
    val startTime = System.currentTimeMillis()
    runnable()
    System.err.printf("time: %.3fs\n", (System.currentTimeMillis() - startTime) / 1e3)
}

@Throws(IOException::class)
fun main(args: Array<String>) {
    val code = File(args[0]).readText(Charsets.US_ASCII)

    val runs = if (args.size > 1) args[1].toInt() else 1

    repeat(runs) {
        runWithTiming(KtProgram(code, System.out)::run)
    }
}
