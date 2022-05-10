package test

import chisel3._
import chisel3.util._

case class UARTConfig(
  clock_freq: Int = 921600, // input clock frequency (Hz)
  baud_rate: Int  = 115200, // baud rate (bps)
  N: Int          = 8,      // # of data bits (5-9)
  parity: String  = "none", // none, odd, even
  stop: Int       = 1,      // # of stop bits (1-2)
)

class UART(cfg: UARTConfig) extends Module {
  val io = IO(new Bundle {
    val tx = Output(Bool())
    val rx = Input(Bool())
    val tx_data = Flipped(Decoupled(UInt(cfg.N.W)))
    val rx_data = Decoupled(UInt(cfg.N.W))
  })

  val PKT_LEN = 1 + cfg.N + (if (cfg.parity == "none") 0 else 1) + cfg.stop
  val BIT_CYCLES = cfg.clock_freq / cfg.baud_rate // floor div for faster TX

  /* TX */
  val tx_dat = RegInit(1.U(PKT_LEN.W))
  val tx_ctr = RegInit((BIT_CYCLES-1).U(math.max(log2Ceil(BIT_CYCLES),1).W))

  val tx_bits_left = tx_dat(PKT_LEN-1,1).orR
  val tx_ctr_edge = tx_ctr === (BIT_CYCLES-1).U
  val tx_parity = io.tx_data.bits.xorR ^ (cfg.parity == "odd").B

  when(tx_bits_left || !tx_ctr_edge) {
    tx_ctr := Mux(tx_ctr_edge, 0.U, tx_ctr + 1.U)
  }

  when(io.tx_data.fire) {
    tx_dat := (if (cfg.parity == "none") Cat(Fill(cfg.stop, true.B), io.tx_data.bits, false.B)
               else Cat(Fill(cfg.stop, true.B), tx_parity, io.tx_data.bits, false.B))
    tx_ctr := 0.U
  }.elsewhen(tx_bits_left && tx_ctr_edge) {
    tx_dat := tx_dat >> 1;
  }

  io.tx_data.ready := !tx_bits_left && tx_ctr_edge
  io.tx := tx_dat(0)

  /* RX */
  io.rx_data <> DontCare

  // use majority circuit
}
