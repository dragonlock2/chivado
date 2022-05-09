package test

import chisel3._
import chisel3.util._

class Test extends Module {
  val io = IO(new Bundle {
    val led = Output(UInt(8.W))
    val btn = Input(UInt(8.W))
    val tx  = Output(UInt(3.W))
    val rx  = Input(UInt(3.W))
  })

  private val FREQ = 100e6
  val pll = Module(new PLL)
  pll.io.in := clock.asBool

  withClock(pll.io.out.asClock) {
    io.tx := Reverse(io.rx)

    val (ctrVal, ctrWrap) = Counter(true.B, (0.1 * FREQ).toInt)
    val shift = RegInit(1.U(8.W))
    when (ctrWrap) {
      shift := Cat(shift(0), shift(7,1))
    }
    io.led := shift | io.btn
  }
}

class PLL extends BlackBox with HasBlackBoxInline {
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Bool())
  })

  setInline("PLL.v",
    """module PLL(
      |    input in,
      |    output out
      |);
      |clk_wiz_0 pll(
      |  .clk_in1(in),
      |  .clk_out1(out),
      |  .reset(false),
      |  .locked(false)
      |);
      |endmodule
    """.stripMargin)
}
