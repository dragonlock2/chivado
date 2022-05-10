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

  val FREQ = 100000000 // 100MHz
  val pll = Module(new PLL)
  pll.io.in := clock.asBool

  withClock(pll.io.out.asClock) {
    val uart0 = Module(new UART(UARTConfig(clock_freq = FREQ)))
    uart0.io.rx := io.rx(0)
    uart0.io.tx_data <> uart0.io.rx_data

    val uart1 = Module(new UART(UARTConfig(clock_freq = FREQ)))
    uart1.io.rx := io.rx(1)
    uart1.io.tx_data <> uart1.io.rx_data

    val uart2 = Module(new UART(UARTConfig(clock_freq = FREQ)))
    uart2.io.rx := io.rx(2)
    uart2.io.tx_data <> uart2.io.rx_data

    io.tx := Cat(uart2.io.tx, uart1.io.tx, uart0.io.tx)

    io.led := uart0.io.rx_data.bits ^ io.btn
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
