package test

import chisel3._
import chisel3.util._

class test extends Module {
  val io = IO(new Bundle {
    val led = Output(UInt(8.W))
    val btn = Input(UInt(8.W))
    val tx  = Output(UInt(3.W))
    val rx  = Input(UInt(3.W))
  })

  private val FREQ = 24e6

  io.tx := Reverse(io.rx)

  val (ctrVal, ctrWrap) = Counter(true.B, (0.25 * FREQ).toInt)
  val shift = RegInit(1.U(8.W))
  when (ctrWrap) {
    shift := Cat(shift(0), shift(7,1))
  }
  io.led := shift | io.btn
}
