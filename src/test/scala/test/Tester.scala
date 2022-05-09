package test

import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec

class Tester extends AnyFreeSpec with ChiselScalatestTester {
  "UART should loopback" in {
    test(new Test) { dut =>
      for (i <- 0 until 8) {
        dut.io.rx.poke(i.U)
        dut.io.tx.expect((Integer.reverse(i) >>> 29).U)
      }
    }
  }

  "LEDs should match btn" in {
    test(new Test) { dut =>
      for (i <- 0 until 256) {
        dut.io.btn.poke(i.U)
        assert(dut.io.led.peek().litValue == (i | 1))
      }
    }
  }
}
