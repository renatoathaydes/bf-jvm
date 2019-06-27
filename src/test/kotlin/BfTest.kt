import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

private fun assertTapeContains(array: IntArray, tape: IntArray) {
    if (tape.size < array.size) {
        Assert.fail("Tape state is too small: ${tape.size} < ${array.size}")
    }
    Assert.assertArrayEquals(array, tape.take(array.size).toIntArray())
    if (tape.drop(array.size).any { it != 0 }) {
        Assert.fail("Tape contains non-zero items after index ${array.size}: ")
    }
}

abstract class BfTest {
    abstract fun runProgram(code: String, printer: PrintStream): IntArray

    @Test
    fun basicBfTest() {
        val tape = runProgram("+++>++>--<<+", System.out)
        assertTapeContains(intArrayOf(4, 2, -2), tape)
    }

    @Test
    fun loopTest() {
        val tape = runProgram("+++[>+<-]", System.out)
        assertTapeContains(intArrayOf(0, 3), tape)
    }

    @Test
    fun helloWorldTest() {
        val output = ByteArrayOutputStream()
        val printer = PrintStream(output, true)

        runProgram(javaClass.getResource("hello.bf").readText(), printer)

        Assert.assertEquals("Hello World!\n", output.toString("US-ASCII"))
    }
}

class BfJavaTest : BfTest() {
    override fun runProgram(code: String, printer: PrintStream): IntArray {
        return Program(code, printer).run().state
    }
}

class BfKotlinTest : BfTest() {
    override fun runProgram(code: String, printer: PrintStream): IntArray {
        return KtProgram(code, printer).run().state
    }
}