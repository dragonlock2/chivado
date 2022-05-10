package test

import chisel3._
import chisel3.util._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec

import scala.util.{Random => rand}

class UARTTest extends AnyFreeSpec with ChiselScalatestTester {
  val NUM_BYTES = 3
  val NUM_STOP = 5

  "UART should output at maximum speed" in {
    for (ratio <- 1 to 8) { // various clock to baud rate ratios
      test(new UART(UARTConfig(clock_freq=ratio, baud_rate=1))) { dut =>
        val num = rand.nextInt() & 0xFF
        val expect_bits = Seq.fill(NUM_BYTES)(
          (Seq(0) ++ Seq.tabulate(8)(i => (num >> i) & 0x1) ++ Seq(1)).flatMap(i => Seq.fill(ratio)(i))
        ).flatten ++ Seq.fill(NUM_STOP)(1)

        dut.io.tx_data.ready.expect(true.B)
        dut.io.tx_data.bits.poke(num.U)
        dut.io.tx_data.valid.poke(true.B)

        var num_sent = 0
        val actual_bits = Seq.tabulate(ratio * NUM_BYTES * 10 + NUM_STOP) { _ => // 8-N-1 = 10 bits/byte
          if (dut.io.tx_data.ready.peek().litValue == 1) { // always ready, equivalent to fire()
            num_sent += 1;
          }
          dut.clock.step()
          if (num_sent == NUM_BYTES) {
            dut.io.tx_data.valid.poke(false.B)
          }
          dut.io.tx.peek().litValue.toInt
        }

        assert(expect_bits == actual_bits)
      }
    }
  }
}
