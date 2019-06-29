@file:JvmName("BfKotlin")

import java.io.File
import java.io.IOException
import java.io.PrintStream

sealed class Op {
    abstract fun run(tape: KtTape)

    object Incr : Op() {
        override fun run(tape: KtTape) = tape.inc(1)
    }

    object Decr : Op() {
        override fun run(tape: KtTape) = tape.inc(-1)
    }

    object MoveUp : Op() {
        override fun run(tape: KtTape) = tape.move(1)
    }

    object MoveDown : Op() {
        override fun run(tape: KtTape) = tape.move(-1)
    }

    class Print(private val out: PrintStream) : Op() {
        override fun run(tape: KtTape) {
            out.print(tape.get().toChar())
        }
    }

    class Loop(private val ops: Array<Op>) : Op() {
        override fun run(tape: KtTape) {
            while (tape.get() > 0) {
                runProgram(ops, tape)
            }
        }
    }
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

private fun runProgram(program: Array<Op>, tape: KtTape) {
    for (op in program) {
        op.run(tape)
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
                '+' -> res.add(Op.Incr)
                '-' -> res.add(Op.Decr)
                '>' -> res.add(Op.MoveUp)
                '<' -> res.add(Op.MoveDown)
                '.' -> res.add(printer)
                '[' -> res.add(Op.Loop(parse(it)))
                ']' -> return res.toTypedArray()
            }
        }
        return res.toTypedArray()
    }

    fun run(): KtTape {
        val tape = KtTape()
        runProgram(ops, tape)
        return tape
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
