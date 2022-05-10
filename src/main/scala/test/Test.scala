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

  withClockAndReset(pll.io.out.asClock, io.btn(0)) {
    val uart0 = Module(new UART(UARTConfig(clock_freq = FREQ)))
    uart0.io.rx := io.rx(0)
    uart0.io.tx_data <> uart0.io.rx_data
    val c = uart0.io.rx_data.bits
    uart0.io.tx_data.bits := Mux((c >= 65.U && c <= 90.U) || (c >= 97.U && c <= 122.U), c ^ 0x20.U, c)

    val uart1 = Module(new UART(UARTConfig(clock_freq = FREQ, parity = "odd")))
    uart1.io.rx := io.rx(1)
    uart1.io.tx_data <> uart1.io.rx_data

    val uart2 = Module(new UART(UARTConfig(clock_freq = FREQ, parity = "even")))
    uart2.io.rx := io.rx(2)
    uart2.io.tx_data <> uart2.io.rx_data

    io.tx := Cat(uart2.io.tx, uart1.io.tx, uart0.io.tx)

    val r = RegInit(0.U(8.W))
    when(uart0.io.rx_data.fire) { r := uart0.io.rx_data.bits }
    io.led := r ^ io.btn
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
