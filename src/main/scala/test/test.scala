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

  io.tx := Reverse(io.rx)

  val led_reg = RegInit(0.U(8.W))
  led_reg := io.btn
  io.led := led_reg
}
